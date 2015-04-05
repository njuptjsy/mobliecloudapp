package com.njuptjsy.cloudclient;

import java.io.File;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import static com.njuptjsy.cloudclient.InfoContainer.*;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;

public class UploadFiles implements Runnable{
	private String filesName;
	private TransferManager transferManager = null;
	private Context context;
	private Handler handler;
	public UploadFiles(String filesName,Context context,Handler handler){
		this.filesName = filesName;
		this.context = context;
		this.handler = handler;
	}
	
	
	public void run(){
		Looper.prepare();
		upload(getFiles(filesName));
		sendUploadResult(MESSAGE_TYPE.UPLOAD_SUCCESS);
	}
	
	private void sendUploadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}


	private TransferManager getTransferManager(){
		if (transferManager == null)
			transferManager = new TransferManager(UserAuthen.getCredentialsProvider(context));
		return transferManager;
	}
	
	private File[] getFiles(String filesName){
		String tag = "UploadFiles:getFiles";
		String[] filePaths = filesName.split("\n");
		File[] files = new File[filePaths.length];
		int i = 0;
		for(String filepath:filePaths){
			files[i] = new File(filepath);
			i++;
		}
		Log.i(tag, "total upload files num is "+i);
		return files;
	}
	
	
	private void upload(File[] files) {
		for(File file:files)
		{
			if (file.exists()) {
				Upload upload = getTransferManager().upload(InfoContainer.BUCKET_NAME, file.getName(), file);
				while (!upload.isDone()){
				    Toast.makeText(context, "Uploading...", Toast.LENGTH_LONG).show();
//				    try {
//				        //Show a progress bar...
//				    } catch (Exception e) {
//				       
//				    }
				}
				Toast.makeText(context, "Uploaded", Toast.LENGTH_LONG).show();
			}
			}
			
		
	}
}
