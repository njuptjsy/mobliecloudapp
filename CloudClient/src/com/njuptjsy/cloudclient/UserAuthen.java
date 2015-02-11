package com.njuptjsy.cloudclient;

import java.util.List;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.auth.BasicAWSCredentials;

/*
 * ���ֿ��е�ʵ�鷽����Ҫ��������⣺

1.Ϊ������APP���ƶ˽�����������aws��ʵ�������½�һ��OpenStack��ƽ̨��ͨ��aws��ʵ����ù�����ip,ͨ�����ip��OpenStack�����������൱����aws��ƽ̨ʵ�����ڴһ����ƽ̨���Ƿ�����ӿ����Ѷȣ�ʹʵ����ڸ���

2.ֱ����Ӧ�ú�aws���н�����ʹ��aws�ṩ�õ�API��aws��OpenStack֮��������ж��ʵ�����Ч����Ҫ������Ӣ��API������һ���Ѷ�

3.ʹ�ðٶ��ƽ��п�����API�������ұȽϼ򵥣����Ǻ�OpenStack֮�������ж���޷�ʵ�����ж��

4.ʹ��ѧϰ���OpenStack��ƽ̨��û�й���ip�޷���У��ʵ�飬OpenStack API��github��һ����Դ��Ŀ������ȷ���д���֤

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
