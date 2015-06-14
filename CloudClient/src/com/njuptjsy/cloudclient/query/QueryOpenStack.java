package com.njuptjsy.cloudclient.query;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

import com.google.common.io.Closeables;
import com.njuptjsy.cloudclient.authen.OpenStackAuthen;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;
import com.njuptjsy.cloudclient.utils.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class QueryOpenStack implements QueryCloud,Closeable{
	private Context context;
	private Handler messageHandler,mainHandler;
	private SwiftApi swiftApi;
	private List<Container> containers;
	
	public QueryOpenStack(Context context,Handler messageHandler,Handler mainHandler){
		this.context = context;
		this.messageHandler = messageHandler;
		this.mainHandler = mainHandler;
		new Thread(this).start();//两种写法，新建好thread对象线程就启动了
	}
	
	@Override
	public void run() {
		InfoContainer.QUERYCLOUDISRUNNING = true;
		Looper.prepare();
		sendQueryResult();
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		InfoContainer.QUERYCLOUDISRUNNING = false;
	}

	@Override
	public void sendQueryResult() {
		Message message = Message.obtain();
		Map<MESSAGE_TYPE, Map<String, List<String>>> resultMap = new HashMap<MESSAGE_TYPE, Map<String, List<String>>>();
		Map<String, List<String>> qureyResult = setQueryResult();
		if (qureyResult == null) {
			message.obj = InfoContainer.MESSAGE_TYPE.NO_RESPONSE_RETRY;
			mainHandler.sendMessage(message);
		}
		else {
			resultMap.put(MESSAGE_TYPE.QUERY_RESULT, qureyResult);
			message.obj = resultMap;
			messageHandler.sendMessage(message);
		}
	}

	private Map<String, List<String>> setQueryResult() {
		String tag = "QueryOpenStack:setQueryResult";
		swiftApi = OpenStackAuthen.getSwiftClient();
		Map<String, List<String>> objectsInBucket = new HashMap<String, List<String>>();
		String containerName = "";
		getBuckets();
		
		if (containers == null) {
			return null;
		}
		else {
			for (Container container:containers) {
				List<String> values = new ArrayList <String>();
				containerName = container.getName();
				LogUtil.v(tag, containerName);
				if (container.getObjectCount() == 0) {
					return null;
				}
				
				List<SwiftObject> swiftObjects = getObjects(container);
				for (SwiftObject swiftObject : swiftObjects) {
					values.add(swiftObject.getName());
					LogUtil.i(tag, swiftObject + swiftObject.getName());
				}
				objectsInBucket.put(containerName, values);
			}
		}
		return objectsInBucket;
	}

	@Override
	public void getBuckets() {
		 String tag = "QueryOpenStack:getBuckets";

	      ContainerApi containerApi = swiftApi.getContainerApi("RegionOne");
	      containers = containerApi.list().toList();

	      for (Container container : containers) {
	         LogUtil.i(tag, container.toString());
	      }
	}

	private ObjectList getObjects(Container container) {
	      String tag = "QueryOpenStack:getObjects";

	      ObjectApi objectApi = swiftApi.getObjectApi("RegionOne", container.getName());
	      ObjectList objects = objectApi.list();
	      
	      for (SwiftObject object: objects) {
	         LogUtil.i(tag, object.getName());
	      }
	      
	      return objects;
	   }

	@Override
	public void close() throws IOException {
		Closeables.close(swiftApi, true);
	}
}
