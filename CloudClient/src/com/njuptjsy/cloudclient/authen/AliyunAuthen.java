package com.njuptjsy.cloudclient.authen;

import static com.njuptjsy.cloudclient.utils.ClientUtils.authenticate;

import javax.mail.SendFailedException;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

public class AliyunAuthen implements UserAuthen{
	private String username;
	private String pwd;
	private Context context;
	private Handler handler;
	private OSSBucket bucket;
	public static OSSService ossService = OSSServiceProvider.getService();
	
	public AliyunAuthen(String username,String pwd,Context context,Handler handler)
	{
		this.username =username;
		this.pwd = pwd;
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		InfoContainer.userAuthenIsRunning = true;
		String tag = "UserAuthen:run";
		InfoContainer.USERISLEGAL = authenticate(username,pwd,InfoContainer.CLOUD.ALIYUN);
		if (InfoContainer.USERISLEGAL) {
			login();
			InfoContainer.userAuthenIsRunning = false;
		}
		else {
			Log.e(tag, "cloudclient user unauthenticated");
			sendLoginResult(MESSAGE_TYPE.USER_UNAUTHEN_FAIL);//cloudclient user unauthenticated.this information will make a toast in main UI
			InfoContainer.userAuthenIsRunning = false;
			return;
		}
	}

	@Override
	public void login() {
		if (ClientUtils.connectInternet(context))
		{
			setOSSService();
			bucket = ossService.getOssBucket(InfoContainer.ALIYUN_BUCKET_NAME);
			if (bucket.getBucketName().equalsIgnoreCase(InfoContainer.ALIYUN_BUCKET_NAME)) {
				sendLoginResult(MESSAGE_TYPE.LOGIN_SUCCESS);
			}
			else
			{
				sendLoginResult(MESSAGE_TYPE.LOGIN_FAILED_RETRY);
			}
		}
		else {
			sendLoginResult(MESSAGE_TYPE.LOGIN_FAILED_NO_INTERNET);
		}
	}

	@Override
	public void sendLoginResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);	
	}
	
	private void setOSSService(){
		ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { //为OSSclient设置一个全局默认加签器，它可以被后续bucket的设置继承或覆盖  
			@Override
			public String generateToken(String httpMethod, String md5, String type, String date,
					String ossHeaders, String resource) {

				String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
						+ resource;

				return OSSToolKit.generateToken(InfoContainer.ALIYUN_ACCESS_ID, InfoContainer.ALIYUN_SCRECT_ID, content);
			}
		});
		ossService.setGlobalDefaultHostId("oss-cn-beijing.aliyuncs.com");//设置后续操作所用bucket所在的数据中心的全局默认hostId
		ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);//如果担心手机终端系统时间不准，可以用这个接口设置从服务器拿到的时间
		ossService.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 为OSSClient设置一个全局默认的bucket访问权限

		ClientConfiguration conf = new ClientConfiguration();
		conf.setConnectTimeout(15 * 1000); 
		conf.setSocketTimeout(15 * 1000); 
		conf.setMaxConnections(50); 
		ossService.setClientConfiguration(conf);
	}
	
	public static OSSService getOSSClient(){
		return ossService;
	}
}
