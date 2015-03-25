package com.njuptjsy.cloudclient;

import java.util.List;
import java.util.jar.Attributes.Name;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import static com.njuptjsy.cloudclient.InfoContainer.*;
/*
 * 几种可行的实验方案和要解决的问题：

1.为了试验APP和云端交互，可以在aws的实例中在新建一个OpenStack的平台，通过aws的实例获得公网的ip,通过这个ip与OpenStack交互。这样相当于在aws云平台实例中在搭建一个云平台，是否会增加开发难度，使实验过于复杂

2.直接让应用和aws进行交互，使用aws提供好的API；aws和OpenStack之间的区别有多大，实验的有效性需要考量，英文API开发有一定难度

3.使用百度云进行开发，API是中文且比较简单；但是和OpenStack之间区别有多大，无法实验计算卸载

4.使用学习搭建的OpenStack云平台，没有公网ip无法在校外实验，OpenStack API在github有一个开源项目，其正确性有待考证

 * 
 * */
public class UserAuthen implements Runnable{
	private String username;
	private String pwd;
	private AmazonS3Client s3Client = null;
	public static CognitoCachingCredentialsProvider credentialsProvider = null;
	private Context context = null;
	private Handler handler;
	
	public UserAuthen(String username,String pwd,Context context,Handler handler)
	{
		this.username =username;
		this.pwd = pwd;
		this.context = context;
		this.handler = handler;
	}

	public void run(){
		String tag = "UserAuthen:run";
		if (authenticate()) {
			login();
		}
		else {
			Log.e(tag, "cloudclient user unauthenticated");
			sendLoginResult(MSEASSGE_TYPE.USER_UNAUTHEN);//cloudclient user unauthenticated
			return;
		}

	}

	private boolean authenticate() {
		if (InfoContainer.USER_NAME.equalsIgnoreCase(username) && InfoContainer.PASSWORD.equalsIgnoreCase(pwd)) 
			return true;
		else
			return false;
	}

	private void login()
	{
		String tag = "UserAuthen:login";
		if (InternetUtils.connectInternet(context))
		{			
			if (getS3Client().doesBucketExist(BUCKET_NAME)){
				Log.i(tag, "login success");
				sendLoginResult(MSEASSGE_TYPE.LOGIN_SUCCESS);//login success
			}
			else {
				sendLoginResult(MSEASSGE_TYPE.LOGIN_FAILED_RETRY);//login failed , please retry 
			}
		}
		else {
			sendLoginResult(MSEASSGE_TYPE.LOGIN_FAILED_NO_INTERNET);//login failed ,not Internet connect
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

	private void sendLoginResult(MSEASSGE_TYPE msgType){
		//use handler to send message to MainActivty
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}

	public AmazonS3Client getS3Client() {
		if (s3Client == null) {
			s3Client = new AmazonS3Client(getCredentialsProvider(context));
		}
		return s3Client;
	}

}
