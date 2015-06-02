package com.njuptjsy.cloudclient.upload;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import static com.njuptjsy.cloudclient.utils.ClientUtils.*;
import static com.njuptjsy.cloudclient.utils.InfoContainer.*;

import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.njuptjsy.cloudclient.R;
import com.njuptjsy.cloudclient.authen.AWSAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

public class AWSUpload implements com.njuptjsy.cloudclient.upload.Upload {
	private String filesName;
	private TransferManager transferManager = null;
	private Context context;
	private Handler handler;
	public AWSUpload(String filesName,Context context,Handler handler){
		this.filesName = filesName;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void run(){
		if (filesName.equals("")) {
			Toast.makeText(context, context.getString(R.string.nofileselected), Toast.LENGTH_LONG).show();
			return;
		}
		Looper.prepare();
		upload(getFiles(filesName));
		sendUploadResult(MESSAGE_TYPE.UPLOAD_SUCCESS);
	}
	
	@Override
	public void sendUploadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}


	private TransferManager getTransferManager(){
		if (transferManager == null)
			transferManager = new TransferManager(AWSAuthen.getCredentialsProvider(context));
		return transferManager;
	}
	
	
	@Override
	public void upload(File[] files) {
		for(File file:files)
		{
			if (file.exists()) {
				Upload upload = getTransferManager().upload(InfoContainer.AWS_BUCKET_NAME, file.getName(), file);
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
