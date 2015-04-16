package com.njuptjsy.cloudclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import static com.njuptjsy.cloudclient.InfoContainer.*;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class QueryCloud  implements Runnable{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;
	public static boolean queryCloudIsRunning = false;

	public QueryCloud(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
	}

	public void run(){
		queryCloudIsRunning = true;
		Looper.prepare();
		sendQueryResult();
		queryCloudIsRunning = false;
	}

	private Map<String, List<String>> setQueryResult() {
		String tag = "QueryCloud:setQueryResult";
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
			Log.v(tag, bucketName);
			if (getObjects(bucket) == null) {
				return null;
			}
			List<S3ObjectSummary> s3ObjectSummaries = getObjects(bucket).getObjectSummaries();
			for (S3ObjectSummary s3ObjectSummary:s3ObjectSummaries) {
				values.add(s3ObjectSummary.getKey());
				Log.v(tag, bucketName + s3ObjectSummary.getKey());
			}
			objectsInBucket.put(bucketName, values);
		}
		return objectsInBucket;
	}

	private AmazonS3Client getAmazonS3Client(){
		return UserAuthen.getS3Client(UserAuthen.getCredentialsProvider(context));
	}

	private ObjectListing getObjects(Bucket bucket){
		try {
			return getAmazonS3Client().listObjects(bucket.getName());
		} catch (Exception e) {
			//can not connect to Amazon aws
			return null;
		}

	}

	private List<Bucket> getBuckets(){
		try {
			return getAmazonS3Client().listBuckets();
		} catch (Exception e) {
			return null;
		}

	}

	private void sendQueryResult(){
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
