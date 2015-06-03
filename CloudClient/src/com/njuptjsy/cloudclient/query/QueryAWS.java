package com.njuptjsy.cloudclient.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.njuptjsy.cloudclient.authen.AWSAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;

import static com.njuptjsy.cloudclient.utils.InfoContainer.*;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class QueryAWS  implements QueryCloud{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;
	

	public QueryAWS(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
		new Thread(this).start();
	}

	public void run(){
		InfoContainer.queryCloudIsRunning = true;
		Looper.prepare();
		sendQueryResult();
		InfoContainer.queryCloudIsRunning = false;
	}

	private Map<String, List<String>> setQueryResult() {//查询返回的数据结构是bucket对应与用list包含的该bucket中所有的bucket
		String tag = "QueryAWS:setQueryResult";
		List<Bucket> buckets = getBuckets();
		String bucketName;
		
		Map<String, List<String>> objectsInBucket = new HashMap<String, List<String>>();
		if (buckets == null) {
			return null;
		}
		for(Bucket bucket:buckets)
		{
			List<String> values = new ArrayList <String>();
			bucketName = bucket.getName();
			LogUtil.v(tag, bucketName);
			if (getObjects(bucket) == null) {
				return null;
			}
			List<S3ObjectSummary> s3ObjectSummaries = getObjects(bucket).getObjectSummaries();
			for (S3ObjectSummary s3ObjectSummary:s3ObjectSummaries) {
				values.add(s3ObjectSummary.getKey());
				LogUtil.v(tag, bucketName + s3ObjectSummary.getKey());
			}
			objectsInBucket.put(bucketName, values);
		}
		return objectsInBucket;
	}

	private AmazonS3Client getAmazonS3Client(){
		return AWSAuthen.getS3Client(AWSAuthen.getCredentialsProvider(context));
	}

	private ObjectListing getObjects(Bucket bucket){
		try {
			return getAmazonS3Client().listObjects(bucket.getName());
		} catch (Exception e) {
			//can not connect to Amazon aws
			return null;
		}

	}
	
	@Override
	public List<Bucket> getBuckets(){
		try {
			return getAmazonS3Client().listBuckets();
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public void sendQueryResult(){//返回主线程的查询结果是查询结果的MESSAGE_TYPE表示和查询结果
		Message message = Message.obtain();
		Map<MESSAGE_TYPE, Map<String, List<String>>> resultMap = new HashMap<MESSAGE_TYPE, Map<String, List<String>>>();
		Map<String, List<String>> qureyResult = setQueryResult();
		if (qureyResult == null) {
			message.obj = InfoContainer.MESSAGE_TYPE.LOGIN_FAILED_RETRY;
			mainHandler.sendMessage(message);
		}
		else {
			resultMap.put(MESSAGE_TYPE.QUERY_RESULT, qureyResult);
			message.obj = resultMap;
			messageHandler.sendMessage(message);
		}

	}

}
