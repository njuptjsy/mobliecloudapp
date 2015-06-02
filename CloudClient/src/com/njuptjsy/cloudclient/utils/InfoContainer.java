package com.njuptjsy.cloudclient.utils;

public class InfoContainer {
	public static final String AWS_ACCOUNT_ID = "328837747656";//AWS Account id
	public static final String COGNITO_POOL_ID = "us-east-1:1d55d22b-0386-4cba-a075-1b0bc7a8ca82";// Identity Pool ID
	public static final String COGNITO_ROLE_UNAUTH = "arn:aws:iam::328837747656:role/Cognito_CloudClientUnauth_DefaultRole"; // an unauthenticated role ARN;
	public static final String COGNITO_ROLE_AUTH =  "arn:aws:iam::328837747656:role/Cognito_CloudClientAuth_DefaultRole";// an authenticated role ARN
	public static final String AWS_BUCKET_NAME = "s3njuptjsy";
	
	public static final String ALIYUN_BUCKET_NAME = "njuptjsy";
	public static final String ALIYUN_ACCESS_ID = "o7AhHgNtyuLUqOSE";
	public static final String ALIYUN_SCRECT_ID = "bD2sQpyJzgkhIbfKOlshKLVwkNWYr6";
	
	public static final String USER_NAME = "admin";
	public static final String PASSWORD = "admin";
	
	public static boolean userAuthenIsRunning;
	public static boolean USERISLEGAL;
	
	public static enum MESSAGE_TYPE{
		USER_UNAUTHEN_FAIL,
		LOGIN_SUCCESS,
		LOGIN_FAILED_RETRY,
		LOGIN_FAILED_NO_INTERNET,
		UPLOAD_SUCCESS,
		QUERY_RESULT,
		SDCARD_UNMOUNTED,
		DOWNLOAD_SUCCESS
	}
	
	public static enum CLOUD{
		ALIYUN,
		AWS,
		OPENSTACK
	}
}
