package com.njuptjsy.cloudclient;

public class InfoContainer {
	public static final String AWS_ACCOUNT_ID = "328837747656";//AWS Account id
	public static final String COGNITO_POOL_ID = "us-east-1:1d55d22b-0386-4cba-a075-1b0bc7a8ca82";// Identity Pool ID
	public static final String COGNITO_ROLE_UNAUTH = "arn:aws:iam::328837747656:role/Cognito_CloudClientUnauth_DefaultRole"; // an unauthenticated role ARN;
	public static final String COGNITO_ROLE_AUTH =  "arn:aws:iam::328837747656:role/Cognito_CloudClientAuth_DefaultRole";// an authenticated role ARN
	public static final String BUCKET_NAME = "s3njuptjsy";
	
	public static final String USER_NAME = "admin";
	public static final String PASSWORD = "admin";
	
	public static enum MSEASSGE_TYPE{
		USER_UNAUTHEN,
		LOGIN_SUCCESS,
		LOGIN_FAILED_RETRY,
		LOGIN_FAILED_NO_INTERNET,
		UPLOAD_SUCCESS
	};
}
