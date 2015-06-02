package com.njuptjsy.cloudclient;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.njuptjsy.cloudclient.InfoContainer.MESSAGE_TYPE;

public class AWSDownLoad implements com.njuptjsy.cloudclient.Download{
	private List<Map<String, Object>> fileToDownload;
	private TransferManager transferManager = null;
	private Context context;
	private Handler handler;
	private Download download;

	public AWSDownLoad(List<Map<String, Object>> fileToDownload, Context context, Handler handler){
		this.fileToDownload = fileToDownload;
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run(){
		Iterator<Map<String, Object>> iterator = fileToDownload.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> fileAttribute = iterator.next();
			File file = saveFileTo(fileAttribute.get("fileKey").toString());
			if (file == null) {
				sendDownLoadResult(MESSAGE_TYPE.SDCARD_UNMOUNTED);
				return;
			}
			download(fileAttribute.get("bucketName").toString(), fileAttribute.get("fileKey").toString(), file);
		}
		while (!download.isDone()) {

		}
		sendDownLoadResult(MESSAGE_TYPE.DOWNLOAD_SUCCESS);
	}

	private TransferManager getTransferManager(){
		if (transferManager == null)
			transferManager = new TransferManager(UserAuthen.getCredentialsProvider(context));
		return transferManager;
	}
	
	@Override
	public void download(String bucket_name,String key, File file ){
		download = getTransferManager().download(bucket_name, key, file);
	}

	private File saveFileTo(String fileKey){
		File file = null;
		String dirName = "CloudClientDownload";
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//decide SDcard is exist
		if (sdCardExist) {
			String path = Environment.getExternalStorageDirectory().getPath();
			File directory = new File(path+"/"+dirName);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			file = new File(directory.getPath() + "/" + fileKey);
		}
		return file;
	}
	
	@Override
	public void sendDownLoadResult(MESSAGE_TYPE msgType){
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}


}
