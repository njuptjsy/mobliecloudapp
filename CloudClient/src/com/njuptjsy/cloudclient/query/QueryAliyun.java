package com.njuptjsy.cloudclient.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.model.ListObjectOption;
import com.alibaba.sdk.android.oss.model.ListObjectResult;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.aliyun.mbaas.tools.ToolKit;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;

public class QueryAliyun implements QueryCloud{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;
	private OSSService ossService;
	private List<String> bucketNames;
	private List<OSSBucket> ossBuckets;
	
	public QueryAliyun(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
		new Thread(this).start();//两种写法，新建好thread对象线程就启动了
	}
	
	
	@Override
	public void run() {
		InfoContainer.QUERYCLOUDISRUNNING = true;
		Looper.prepare();
		sendQueryResult();
		InfoContainer.QUERYCLOUDISRUNNING = false;
	}

	@Override
	public void sendQueryResult() {
		Message message = Message.obtain();
		Map<MESSAGE_TYPE, Map<String, List<String>>> resultMap = new HashMap<MESSAGE_TYPE, Map<String, List<String>>>();
		Map<String, List<String>> qureyResult = setQueryResult();
		if (qureyResult == null) {
			message.obj = InfoContainer.MESSAGE_TYPE.NO_RESPONSE_RETRY;
			mainHandler.sendMessage(message);
		}
		else {
			resultMap.put(MESSAGE_TYPE.QUERY_RESULT, qureyResult);
			message.obj = resultMap;
			messageHandler.sendMessage(message);
		}
	}

	
	private Map<String, List<String>> setQueryResult() {
		String tag = "QueryAliyun:setQueryResult";
		String bucketName;
		Map<String, List<String>> objectsInBucket = new HashMap<String, List<String>>();
		
		getBuckets();
		
		if (ossBuckets == null) {
			return null;
		}
		for(OSSBucket ossBucket:ossBuckets)
		{
			List<String> values = new ArrayList <String>();
			bucketName = ossBucket.getBucketName();
			LogUtil.v(tag, bucketName);
			if (getObjects(ossBucket) == null) {
				return null;
			}
			List<ListObjectResult.ObjectInfo> ossObjectInfos = getObjects(ossBucket).getObjectInfoList();
			for (ListObjectResult.ObjectInfo ossObjectInfo : ossObjectInfos) {
				values.add(ossObjectInfo.getObjectKey());
				LogUtil.i(tag, bucketName + ossObjectInfo.getObjectKey());
			}
			objectsInBucket.put(bucketName, values);
		}
		return objectsInBucket;
	}


	private ListObjectResult getObjects(OSSBucket ossBucket) {
		
		try {
			return ossBucket.listObjectsInBucket(new ListObjectOption());
		} catch (OSSException e) {
			LogUtil.e("QueryAliyun:ListObjectResult", "listObjectsInBucket error");
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void getBuckets() {
		ossService = AliyunAuthen.getOSSClient();//没有接口列出所有的bucket
		
		try {
			sendHttpGet();
		} catch (URISyntaxException e) {
			LogUtil.e("QueryAliyun:sendHttpGet", "catch URISyntaxException");
			e.printStackTrace();
		}
		ossBuckets = new ArrayList<OSSBucket>();
		for (String bucketName:bucketNames) {
			ossBuckets.add(ossService.getOssBucket(bucketName));
		}
	}

	/*
	 * 构建如下http请求：
	 * GET / HTTP/1.1
	 * Host: oss.aliyuncs.com
	 * Date: GMT Date
     * Authorization: SignatureValue
	 * */
	private void sendHttpGet() throws URISyntaxException {
		HttpClient httpClient = new DefaultHttpClient();
		String host = "oss.aliyuncs.com";
		
		URI url = URIUtils.createURI("http", host, -1, "/", null, null);//ublic static URI createURI(String scheme,String host,int port,String path,String query,String fragment)
		LogUtil.i("QueryAliyun:sendHttpGet", url+"");
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Date", getSystemTime());
		httpGet.addHeader("Host","oss.aliyuncs.com");
		httpGet.addHeader("Authorization", getSignature());
		
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
//				HttpEntity entity = httpResponse.getEntity();
//				InputStream in = entity.getContent();//     服务器返回的字符串是以流的形式
//              BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//              String line = reader.readLine();
				
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity,"UTF-8");
				LogUtil.i("alyunQuery:sendHttpGet", result);
				parseXMLWithPull(result);
			}
			else {
				LogUtil.e("alyunQuery:sendHttpGet", "response wrong stauts code:"+ httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseXMLWithPull(String result) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(result));
			int eventType = xmlPullParser.getEventType();
			
			String name = "";
			String location = "";
			bucketNames = new ArrayList<String>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Location".equals(nodeName)) {
						location = xmlPullParser.nextText();
					}
					else if ("Name".equals(nodeName)){
						name = xmlPullParser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Bucket".equals(nodeName)) {
						bucketNames.add(name);
						LogUtil.i("alyunQuery:parseXMLWithPull", "get name :" + name);
					}
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e("alyunQuery:parseXMLWithPull", "parse XML error");
		}
	}


	private String getSystemTime() {
		return ToolKit.getGMTDate();
	}


	/*
	 * "Authorization: OSS " + Access Key Id + ":" + Signature
		Signature = base64(hmac-sha1(AccessKeySecret,
            VERB + "\n" 
            + CONTENT-MD5 + "\n"
            + CONTENT-TYPE + "\n" 
            + DATE + "\n" 
            + CanonicalizedOSSHeaders
            + CanonicalizedResource))
	 * */
	//base64.encodestring(h.digest()).strip()
	private String getSignature(){
		String encryptText = "GET" + "\n" 
	            + "" + "\n" 
	            + "" + "\n" 
	            + getSystemTime() + "\n" 
	            + ""
	            + "/";
		
		String signature = "";
		try {
			byte[] signatureTemp = ClientUtils.HmacSHA1Encrypt(InfoContainer.ALIYUN_SCRECT_ID, encryptText);
			LogUtil.i("QueryAliyun:getSignature", "signature before BASE64: "+ signatureTemp);
            testGetbyte(signatureTemp);//why utf-8 is wrong
			signature = Base64.encodeToString(signatureTemp, Base64.DEFAULT).trim();
			//signature = ToolKit.getHmacSha1Signature(encryptText, InfoContainer.ALIYUN_SCRECT_ID);//there is a api provide by aliyun
			LogUtil.i("QueryAliyun:getSignature", "signature: "+signature + " and "+ToolKit.getHmacSha1Signature(encryptText, InfoContainer.ALIYUN_SCRECT_ID));
		} catch (Exception e) {
			LogUtil.e("QueryAliyun:getSignature", " catch exception in HmacSHA1Encrypt");
			e.printStackTrace();
		}
		String authorization = "OSS " + InfoContainer.ALIYUN_ACCESS_ID + ":" + signature;
		
		return authorization;
	}


	private void testGetbyte(byte[] signatureTemp) {
		String testString;
		try {
			testString = new String(signatureTemp,"ISO-8859-1");
			byte[] testBytes = testString.getBytes("ISO-8859-1");
			LogUtil.i("QueryAliyun:testGetbyte", Arrays.toString(signatureTemp) + " and " + Arrays.toString(testBytes));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
