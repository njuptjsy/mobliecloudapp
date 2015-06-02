package com.njuptjsy.cloudclient.upload;

import java.io.File;

import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

public interface Upload extends Runnable{
	public void sendUploadResult(MESSAGE_TYPE msgType);
	public void upload(File[] files);
}
