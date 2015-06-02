package com.njuptjsy.cloudclient;

import java.io.File;

import com.njuptjsy.cloudclient.InfoContainer.MESSAGE_TYPE;

public interface Upload extends Runnable{
	public void sendUploadResult(MESSAGE_TYPE msgType);
	public void upload(File[] files);
}
