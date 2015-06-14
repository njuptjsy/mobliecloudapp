package com.njuptjsy.cloudclient.authen;

import static com.njuptjsy.cloudclient.utils.ClientUtils.authenticate;

import java.util.List;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.features.ContainerApi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.njuptjsy.cloudclient.utils.ClientUtils;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.InfoContainer.MESSAGE_TYPE;
import com.njuptjsy.cloudclient.utils.LogUtil;

public class OpenStackAuthen implements UserAuthen{
	private String username;
	private String pwd;
	private Context context;
	private Handler handler;
	private static SwiftApi swiftApi;


	public OpenStackAuthen(String username,String pwd,Context context,Handler handler)
	{
		this.username =username;
		this.pwd = pwd;
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		InfoContainer.USERAUTHENISRUNNING = true;
		String tag = "OpenStackAuthen:run";
		InfoContainer.USERISLEGAL = authenticate(username,pwd,InfoContainer.CLOUD.OPENSTACK);//这个方法可以抽象的接口中
		if (InfoContainer.USERISLEGAL) {
			login();
			InfoContainer.USERAUTHENISRUNNING = false;
		}
		else {
			Log.e(tag, "cloudclient user unauthenticated");
			sendLoginResult(MESSAGE_TYPE.USER_UNAUTHEN_FAIL);//cloudclient user unauthenticated.this information will make a toast in main UI
			InfoContainer.USERAUTHENISRUNNING = false;
			return;
		}
	}

	@Override
	public void login() {
		String tag = "OpenStackAuthen:login";
		
		if (ClientUtils.connectInternet(context))
		{	
			setJCloudsSwift();
			
			if (listContainers()!=null){
				Log.i(tag, "login success");
				sendLoginResult(MESSAGE_TYPE.LOGIN_SUCCESS);//login success
			}
			else {
				sendLoginResult(MESSAGE_TYPE.NO_RESPONSE_RETRY);//login failed , please retry 
			}
		}
		else {
			sendLoginResult(MESSAGE_TYPE.LOGIN_FAILED_NO_INTERNET);//login failed ,not Internet connect
		}
	}

	public void setJCloudsSwift() {//根据用户名和密码连接并配置swiftApi
		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
		String provider = "openstack-swift";
		swiftApi = ContextBuilder.newBuilder(provider)
				.endpoint("http://xxx.xxx.xxx.xxx:5000/v2.0/")
				.credentials(InfoContainer.OPENSTACK_ACCESS_ID, InfoContainer.OPENSTACK_SCRECT_ID)
				.modules(modules)
				.buildApi(SwiftApi.class);
	}

	private List<Container> listContainers() {
		String tag = "OpenStackAuthen:listContainers";
		ContainerApi containerApi = swiftApi.getContainerApi("RegionOne");//传入代表区域的参数
		List<Container> containers = containerApi.list().toList();

		for (Container container : containers) {
			LogUtil.i(tag, "  " + container);
		}
		return containers;
	}

	@Override
	public void sendLoginResult(MESSAGE_TYPE msgType) {
		Message msg = Message.obtain();
		msg.obj = msgType;
		handler.sendMessage(msg);
	}
	
	public static SwiftApi getSwiftClient() {
		return swiftApi;
	}

}
