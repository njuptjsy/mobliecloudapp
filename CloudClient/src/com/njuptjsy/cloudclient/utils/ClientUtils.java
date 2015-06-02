package com.njuptjsy.cloudclient.utils;

import java.io.File;
//import static com.njuptjsy.cloudclient.utils.InfoContainer.*;
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
	
	public static boolean authenticate(String username,String pwd,InfoContainer.CLOUD cloudType) {//这里第三个参数表示云的类型，可以根据不同的云验证不同的账号和密码，目前此功能没有开发
		if (InfoContainer.USER_NAME.equalsIgnoreCase(username) && InfoContainer.PASSWORD.equalsIgnoreCase(pwd)) 
			return true;
		else
			return false;
	}
	
	//可以将一些常用的语句包装成一个函数，减少输入字符数，如toast
}
