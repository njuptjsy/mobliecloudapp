package com.njuptjsy.cloudclient.query;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.sdk.android.oss.OSSService;
import com.aliyun.mbaas.tools.ToolKit;
import com.amazonaws.services.s3.model.Bucket;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

public class QueryAliyun implements QueryCloud{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;
	private OSSService ossService;

	public QueryAliyun(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
		new Thread(this).start();//两种写法，新建好thread对象线程就启动了
	}
	
	
	@Override
	public void run() {
		InfoContainer.queryCloudIsRunning = true;
		Looper.prepare();
		sendQueryResult();
		InfoContainer.queryCloudIsRunning = false;
	}

	@Override
	public void sendQueryResult() {
		String tag = "QueryAWS:setQueryResult";
		List<Bucket> buckets = getBuckets();
	}

	
	@Override
	public List<Bucket> getBuckets() {
		ossService = AliyunAuthen.getOSSClient();//没有接口列出所有的bucket
		
		try {
			sendHttpGet();
		} catch (URISyntaxException e) {
			LogUtil.e("QueryAliyun:sendHttpGet", "catch URISyntaxException");
			e.printStackTrace();
		}
		return null;
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
				HttpEntity entity = httpResponse.getEntity();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//				String result;
//				while ((result = reader.readLine()) != null) {
//					result = result + reader.readLine();
//				}
				String result = EntityUtils.toString(entity);
				LogUtil.i("alyunQuery:sendHttpGet", result);
			}
			else {
				
				LogUtil.e("alyunQuery:sendHttpGet", "response wrong stauts code:"+ httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
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
            testGetbyte(signatureTemp);
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
			LogUtil.i("QueryAliyun:testGetbyte", signatureTemp + " and " + testBytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
	}

}
