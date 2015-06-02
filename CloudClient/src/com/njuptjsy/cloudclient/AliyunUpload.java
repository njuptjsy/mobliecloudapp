package com.njuptjsy.cloudclient;

import java.io.File;

import android.content.Context;
import android.os.Handler;

import com.njuptjsy.cloudclient.InfoContainer.MESSAGE_TYPE;

public class AliyunUpload implements Upload{
	private Handler handler;
	private Context context;
	private String fileNames;
	
	public AliyunUpload(String fileNames,Context context,Handler handler){
		this.context = context;
		this.fileNames = fileNames;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		
	}

	@Override
	public void sendUploadResult(MESSAGE_TYPE msgType) {
		
	}

	@Override
	public void upload(File[] files) {
		
	}

}
