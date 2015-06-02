package com.njuptjsy.cloudclient;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 *通过静态导入这个类使用本类提供的全部静态工具方法 
 * */
public class ClientUtils {
	public static boolean connectInternet(Context context) {
		Log.v("InternetUtils:connectInternet", "decide wherter connect to internet");
		if (context != null) {  
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
			if (mNetworkInfo != null) {  
				return mNetworkInfo.isAvailable();  
			}
		}
		return false;
	}
	
	public static File[] getFiles(String filesName){//将获得的选中的文件名变成文件对象数组
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
	
	//可以将一些常用的语句包装成一个函数，减少输入字符数，如toast
}
