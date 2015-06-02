package com.njuptjsy.cloudclient.upload;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.HandleAliyunException;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;
import com.njuptjsy.cloudclient.utils.LogUtil;

public class AliyunUpload implements Upload{
	private Handler handler;
	private Context context;
	private String fileNames;
	private OSSService ossService;
	
	public AliyunUpload(String fileNames,Context context,Handler handler){
		this.context = context;
		this.fileNames = fileNames;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		ossService = AliyunAuthen.getOSSClient();
		upload(ClientUtils.getFiles(fileNames));
		sendUploadResult(MESSAGE_TYPE.UPLOAD_SUCCESS);
	}

	
	@Override
	public void sendUploadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}

	@Override
	public void upload(File[] files) {
		for (File file:files) {
			OSSFile fileToUpload = ossService.getOssFile(getBucket(), file.getName());
			
			try {
				fileToUpload.setUploadFilePath(file.getPath(), "raw/binary");
				fileToUpload.upload();
				LogUtil.d("AliyunUpload:upload", "upload "+ file.getName());
			} catch (OSSException e) {
				HandleAliyunException.handleException(e);
			}catch (FileNotFoundException e){
				e.printStackTrace();
				LogUtil.e("AliyunUpload:upload", file.getName() + " not found");
			}
			
		}
		LogUtil.d("AliyunUpload:upload", "upload finish");
		
	}
	
	private OSSBucket getBucket(){
		return ossService.getOssBucket(InfoContainer.ALIYUN_BUCKET_NAME);
	}
}
