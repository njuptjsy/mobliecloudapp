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
import android.os.Message;
import android.view.View;
import android.widget.ListView;

public class QueryCloud  implements Runnable{
	private Context context;
	private Handler handler;
	
	public QueryCloud(Context context,Handler handler){
		this.context = context;
		this.handler = handler;
	}

	public void run(){
		sendQueryResult();
	}
	
	private Map<String, List<String>> setQueryResult() {
		List<Bucket> buckets = getBuckets();
		String bucketName;
		List<String> keys = new ArrayList <String>();
		Map<String, List<String>> objectsInBucket = new HashMap<String, List<String>>();
		for(Bucket bucket:buckets)
		{
			bucketName = bucket.getName();
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
		return getAmazonS3Client().listObjects(bucket.getName());
	}
	
	private List<Bucket> getBuckets(){
		return getAmazonS3Client().listBuckets();
	}
	
	private void sendQueryResult(){
		Message message = Message.obtain();
		Map<MESSAGE_TYPE, Map<String, List<String>>> resultMap = new HashMap<MESSAGE_TYPE, Map<String, List<String>>>();
		resultMap.put(MESSAGE_TYPE.QUERY_RESULT, setQueryResult());
		message.obj = resultMap;
		handler.sendMessage(message);
	}
	
}
