package com.njuptjsy.cloudclient;

import java.util.List;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.auth.BasicAWSCredentials;

/*
 * 几种可行的实验方案和要解决的问题：

1.为了试验APP和云端交互，可以在aws的实例中在新建一个OpenStack的平台，通过aws的实例获得公网的ip,通过这个ip与OpenStack交互。这样相当于在aws云平台实例中在搭建一个云平台，是否会增加开发难度，使实验过于复杂

2.直接让应用和aws进行交互，使用aws提供好的API；aws和OpenStack之间的区别有多大，实验的有效性需要考量，英文API开发有一定难度

3.使用百度云进行开发，API是中文且比较简单；但是和OpenStack之间区别有多大，无法实验计算卸载

4.使用学习搭建的OpenStack云平台，没有公网ip无法在校外实验，OpenStack API在github有一个开源项目，其正确性有待考证

 * 
 * */
public class UserAuthen {
	private String username;
	private String pwd;
	private AmazonS3Client s3Client;
	private BasicAWSCredentials awsCredentials;
	public UserAuthen(String username,String pwd)
	{
		this.username =username;
		this.pwd = pwd;
		
	}

	private boolean login()
	{
		if (connectInternet())
		awsCredentials = new BasicAWSCredentials(username, pwd);//Constructs a new BasicAWSCredentials object, with the specified AWS access key and AWS secret key.		
		s3Client = new AmazonS3Client(awsCredentials);
		List<Bucket> bucketsList =s3Client.listBuckets();
		
		return false;	
	}

	private boolean connectInternet() {
		
		return false;
	}

}
