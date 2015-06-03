package com.njuptjsy.cloudclient.query;

import java.util.List;

import com.alibaba.sdk.android.oss.OSSService;
import com.amazonaws.services.s3.model.Bucket;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class QueryAliyun implements QueryCloud{
	private Context context;
	private Handler messageHandler;
	private Handler mainHandler;
	private OSSService ossService;

	public QueryAliyun(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
		new Thread(this).start();//两种写法，新建好thread对象线程就启动了
	}
	
	
	@Override
	public void run() {
		InfoContainer.queryCloudIsRunning = true;
		Looper.prepare();
		sendQueryResult();
		InfoContainer.queryCloudIsRunning = false;
	}

	@Override
	public void sendQueryResult() {
		String tag = "QueryAWS:setQueryResult";
		List<Bucket> buckets = getBuckets();
	}

	
	@Override
	public List<Bucket> getBuckets() {
		ossService = AliyunAuthen.getOSSClient();//没有方法列出所有的bucket
		return null;
	}


	

}
