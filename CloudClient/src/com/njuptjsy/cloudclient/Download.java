package com.njuptjsy.cloudclient;

import java.io.File;

import com.njuptjsy.cloudclient.InfoContainer.MESSAGE_TYPE;

public interface Download extends Runnable{
	public void download(String bucket_name,String key, File file);
	public void sendDownLoadResult(MESSAGE_TYPE msgType);
}
