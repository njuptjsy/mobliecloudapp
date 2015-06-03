package com.njuptjsy.cloudclient.query;

import java.util.List;

import com.amazonaws.services.s3.model.Bucket;

public interface QueryCloud extends Runnable{
	public void sendQueryResult();
	public List<Bucket> getBuckets();
	}
