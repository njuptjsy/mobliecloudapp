package com.njuptjsy.cloudclient.download;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;
import com.njuptjsy.cloudclient.utils.LogUtil;

public class AliyunDownload implements Download{
	private List<Map<String, Object>> fileToDownload;
	private Context context;
	private Handler handler;
	private OSSService ossService;

	public AliyunDownload(List<Map<String, Object>> fileToDownload, Context context, Handler handler){
		this.fileToDownload = fileToDownload;
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
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

		sendDownLoadResult(MESSAGE_TYPE.DOWNLOAD_SUCCESS);
	}

	@Override
	public void download(String bucket_name, String key, File file) {
		ossService = AliyunAuthen.getOSSClient();
		OSSBucket ossBucket = ossService.getOssBucket(bucket_name);
		OSSFile ossFile = ossService.getOssFile(ossBucket,key);
		try {
			ossFile.downloadTo(file.getPath());
		} catch (OSSException e) {
			LogUtil.e("AliyunDownload:download", "download function error");
			e.printStackTrace();
		}
	}

	@Override
	public void sendDownLoadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
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
}
