package com.njuptjsy.cloudclient.utils;

import java.io.File;
//import static com.njuptjsy.cloudclient.utils.InfoContainer.*;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 *ͨ����̬���������ʹ�ñ����ṩ��ȫ����̬���߷��� 
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
	
	public static File[] getFiles(String filesName){//����õ�ѡ�е��ļ�������ļ���������
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
	
	public static boolean authenticate(String username,String pwd,InfoContainer.CLOUD cloudType) {//���������������ʾ�Ƶ����ͣ����Ը��ݲ�ͬ������֤��ͬ���˺ź����룬Ŀǰ�˹���û�п���
		if (InfoContainer.USER_NAME.equalsIgnoreCase(username) && InfoContainer.PASSWORD.equalsIgnoreCase(pwd)) 
			return true;
		else
			return false;
	}
	
	//���Խ�һЩ���õ�����װ��һ�����������������ַ�������toast
}
