package com.njuptjsy.cloudclient.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.mail.internet.NewsAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.sdk.android.oss.OSSService;
import com.aliyun.mbaas.tools.ToolKit;
import com.amazonaws.services.s3.model.Bucket;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;
import android.content.Context;
import android.content.Entity;
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
     * 	GET / HTTP/1.1
		Date: Thu, 15 May 2014 11:18:32 GMT
		Host: 10.97.188.37
		Authorization: OSS nxj7dtl1c24jwhcyl5hpvnhi:COS3OQkfQPnKmYZTEHYv2qUl5jI=
     * http://oss-example.oss-cn-hangzhou.aliyuncs.com/oss-api.pdf?OSSAccessKeyId=44CF9590006BF252F707&Expires=1141889120&Signature=vjbyPxybdZaNmGa%2ByT272YEAiv4%3D
	 * */
	
	
	private void sendHttpGet() throws URISyntaxException {
		HttpClient httpClient = new DefaultHttpClient();
		String host = "oss.aliyuncs.com";
		List<BasicNameValuePair> queryParams = new ArrayList<BasicNameValuePair>(); 
		queryParams.add(new BasicNameValuePair("Date", getSystemTime()));
		
		queryParams.add(new BasicNameValuePair("Authorization", getSignature()));
		
		URI url = URIUtils.createURI("http", host, -1, "/", null, null);//ublic static URI createURI(String scheme,String host,int port,String path,String query,String fragment)
		LogUtil.i("QueryAliyun:sendHttpGet", url+"");
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Date", getSystemTime());
		httpGet.addHeader("Host","oss.aliyuncs.com");
		httpGet.addHeader("Authorization", getSignature());
		LogUtil.i("QueryAliyun:sendHttpGet", "httpget : "+httpGet.toString());
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
//		Date now = new Date();//set to the current date and time  
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.CHINA);  
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT  
//		String systemGMTTime =  simpleDateFormat.format(now.getTime());  
//		LogUtil.i("QueryAliyun:sendHttpGet", "the time given by aliyun API" + (ToolKit.getGMTDate()));
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		calendar.add(Calendar.MINUTE, 1);
//		return Long.toString(calendar.getTimeInMillis());
		
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

	private String getSignature(){
		String encryptText = "GET" + "\n" 
	            + "" + "\n" 
	            + "" + "\n" 
	            + getSystemTime() + "\n" 
	            + ""
	            + "/";
		
		String signature = "";
		try {
			
			LogUtil.i("QueryAliyun:getSignature", "encryptText: "+encryptText);
			
			String signatureTemp = ClientUtils.HmacSHA1Encrypt(InfoContainer.ALIYUN_SCRECT_ID, encryptText);//wrong here
			String signatureTempBase64 = Base64.encodeToString(signature.getBytes(), Base64.DEFAULT);
			
			System.out.println(signatureTempBase64+"+++++++");
			signature = ToolKit.getHmacSha1Signature(encryptText, InfoContainer.ALIYUN_SCRECT_ID);
			System.out.println(signature+"============");
			
			LogUtil.i("QueryAliyun:getSignature", "signature: "+signature);
		} catch (Exception e) {
			LogUtil.e("QueryAliyun:getSignature", " catch exception in HmacSHA1Encrypt");
			e.printStackTrace();
		}
		String authorization = "OSS " + InfoContainer.ALIYUN_ACCESS_ID + ":" + signature;
		
		return authorization;
	}

}
