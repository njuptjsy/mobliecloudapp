package com.njuptjsy.cloudclient.upload;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.jclouds.io.Payload;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.io.payloads.FilePayload;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.common.io.Closeables;
import com.njuptjsy.cloudclient.authen.OpenStackAuthen;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;

public class OpenStackUpload implements Upload,Closeable{
	private Handler handler;
	private Context context;
	private String fileNames;
	private SwiftApi swiftApi;
	
	public OpenStackUpload(String fileNames,Context context,Handler handler){
		this.context = context;
		this.fileNames = fileNames;
		this.handler = handler;
	}
	
	
	@Override
	public void run() {
		swiftApi = OpenStackAuthen.getSwiftClient();
		upload(ClientUtils.getFiles(fileNames));
		sendUploadResult(MESSAGE_TYPE.UPLOAD_SUCCESS);
		try {
			close();
		} catch (IOException e) {
			LogUtil.e("OpenStack:run", "catch exception in close");
			e.printStackTrace();
		}
	}

	@Override
	public void sendUploadResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}

	@Override
	public void upload(File[] files) {//依次上传选中的文件
		String tag = "OpenStackUpload:upload";
		ObjectApi objectApi = swiftApi.getObjectApi("RegionOne", InfoContainer.OPENSTACK_BUCKET_NAME);
		Payload payload = null;
		for (File file:files) {
			payload = new FilePayload(file);
			objectApi.put(InfoContainer.OPENSTACK_BUCKET_NAME, payload);
			LogUtil.i(tag, file.getName());
		}
	}

	
//	private void uploadObjectFromString() {
//		System.out.println("Upload Object From String");
//
//		ObjectApi objectApi = swiftApi.getObjectApi("RegionOne", CONTAINER_NAME);
//		Payload payload = newByteSourcePayload("Hello World".getBytes());
//
//		objectApi.put(OBJECT_NAME, payload, PutOptions.Builder.metadata(ImmutableMap.of("key1", "value1")));
//
//		System.out.println("  " + OBJECT_NAME);
//	}


	@Override
	public void close() throws IOException {
		Closeables.close(swiftApi, true);
	}
}
