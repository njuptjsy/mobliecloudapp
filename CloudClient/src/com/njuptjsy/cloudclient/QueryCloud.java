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
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class QueryCloud  implements Runnable{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;

	public QueryCloud(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
	}

	public void run(){
		Looper.prepare();
		sendQueryResult();
	}

	private Map<String, List<String>> setQueryResult() {
		List<Bucket> buckets = getBuckets();
		String bucketName;
		List<String> keys = new ArrayList <String>();
		Map<String, List<String>> objectsInBucket = new HashMap<String, List<String>>();
		if (buckets == null) {
			return null;
		}
		for(Bucket bucket:buckets)
		{
			bucketName = bucket.getName();
			if (getObjects(bucket) == null) {
				return null;
			}
			List<S3ObjectSummary> s3ObjectSummaries = getObjects(bucket).getObjectSummaries();
			for (S3ObjectSummary s3ObjectSummary:s3ObjectSummaries) {
				keys.add(s3ObjectSummary.getKey());
			}
			objectsInBucket.put(bucketName, keys);
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
		if (setQueryResult() == null) {
			message.obj = InfoContainer.MESSAGE_TYPE.LOGIN_FAILED_RETRY;
			mainHandler.sendMessage(message);
		}
		else {
			resultMap.put(MESSAGE_TYPE.QUERY_RESULT, setQueryResult());
			message.obj = resultMap;
			messageHandler.sendMessage(message);
		}

	}

}
