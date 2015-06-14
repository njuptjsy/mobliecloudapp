package com.njuptjsy.cloudclient.download;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.njuptjsy.cloudclient.authen.OpenStackAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;
import com.njuptjsy.cloudclient.utils.LogUtil;

public class OpenStackDownload implements Download,Closeable{
	private List<Map<String, Object>> fileToDownload;
	private Context context;
	private Handler handler;
	private SwiftApi swiftApi;


	public OpenStackDownload(List<Map<String, Object>> fileToDownload, Context context, Handler handler){
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
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendDownLoadResult(MESSAGE_TYPE.DOWNLOAD_SUCCESS);
	}

	@Override
	public void download(String bucket_name, String key, File saveTo) {
		swiftApi = OpenStackAuthen.getSwiftClient();
		SwiftObject swiftObject = getObject(bucket_name, key);
		try {
			writeObject(swiftObject,saveTo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private SwiftObject getObject(String containerName, String fileKey) {
		String tag = "OpenStackDownload:getObject";

		ObjectApi objectApi = swiftApi.getObjectApi("RegionOne", containerName);
		SwiftObject swiftObject = objectApi.get(fileKey);

		LogUtil.i(tag,swiftObject.getName());

		return swiftObject;
	}

	private void writeObject(SwiftObject swiftObject,File saveTo) throws IOException {
		String tag = "OpenStackDownload:writeObject";

		InputStream inputStream = swiftObject.getPayload().openStream();
		
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveTo));

		try {
			ByteStreams.copy(inputStream, outputStream);
		}
		finally {
			inputStream.close();
			outputStream.close();
		}

		LogUtil.i(tag, saveTo.getAbsolutePath());
	}

	@Override
	public void sendDownLoadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}

	@Override
	public void close() throws IOException {
		Closeables.close(swiftApi, true);		
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
