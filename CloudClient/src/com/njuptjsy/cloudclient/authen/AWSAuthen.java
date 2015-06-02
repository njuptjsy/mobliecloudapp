package com.njuptjsy.cloudclient.authen;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

import static com.njuptjsy.cloudclient.utils.ClientUtils.*;
import static com.njuptjsy.cloudclient.utils.InfoContainer.*;
/*
 * 几种可行的实验方案和要解决的问题：

1.为了试验APP和云端交互，可以在aws的实例中在新建一个OpenStack的平台，通过aws的实例获得公网的ip,通过这个ip与OpenStack交互。这样相当于在aws云平台实例中在搭建一个云平台，是否会增加开发难度，使实验过于复杂

2.直接让应用和aws进行交互，使用aws提供好的API；aws和OpenStack之间的区别有多大，实验的有效性需要考量，英文API开发有一定难度

3.使用百度云进行开发，API是中文且比较简单；但是和OpenStack之间区别有多大，无法实验计算卸载

4.使用学习搭建的OpenStack云平台，没有公网ip无法在校外实验，OpenStack API在github有一个开源项目，其正确性有待考证

 * 
 * */
public class AWSAuthen implements UserAuthen{
	private String username;
	private String pwd;
	private static AmazonS3Client s3Client = null;
	public static CognitoCachingCredentialsProvider credentialsProvider = null;
	private Context context = null;
	private Handler handler;
	
	//public static boolean userAuthenIsRunning = false;
	public AWSAuthen(String username,String pwd,Context context,Handler handler)
	{
		this.username =username;
		this.pwd = pwd;
		this.context = context;
		this.handler = handler;
	}

	public void run(){
		InfoContainer.userAuthenIsRunning = true;
		String tag = "UserAuthen:run";
		InfoContainer.USERISLEGAL = authenticate(username,pwd,InfoContainer.CLOUD.AWS);
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
	public void login()
	{
		String tag = "UserAuthen:login";
		boolean reponse = false;
		if (ClientUtils.connectInternet(context))
		{	
			try {
				reponse =  getS3Client(getCredentialsProvider(context)).doesBucketExist(AWS_BUCKET_NAME);
			} catch (Exception e) {//HTTP connect fail more than 3 times
				reponse = false;
			}

			if (reponse){
				Log.i(tag, "login success");
				sendLoginResult(MESSAGE_TYPE.LOGIN_SUCCESS);//login success
			}
			else {
				sendLoginResult(MESSAGE_TYPE.LOGIN_FAILED_RETRY);//login failed , please retry 
			}
		}
		else {
			sendLoginResult(MESSAGE_TYPE.LOGIN_FAILED_NO_INTERNET);//login failed ,not Internet connect
		}

	}

	public static CognitoCachingCredentialsProvider getCredentialsProvider(Context context){
			// Initialize the Amazon Cognito credentials provider
			credentialsProvider = new CognitoCachingCredentialsProvider(
					context, // Context
					AWS_ACCOUNT_ID,
					COGNITO_POOL_ID, // Identity Pool ID
					COGNITO_ROLE_UNAUTH, // an unauthenticated role ARN
					COGNITO_ROLE_AUTH,// an authenticated role ARN
					Regions.US_EAST_1 // Region
					);
		return credentialsProvider;
	}

	@Override
	public void sendLoginResult(MESSAGE_TYPE msgType){
		//use handler to send message to MainActivty
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}

	public static AmazonS3Client getS3Client(CognitoCachingCredentialsProvider cachingCredentialsProvider) {
		if (s3Client == null) {
			s3Client = new AmazonS3Client(cachingCredentialsProvider);
		}
		return s3Client;
	}

}
