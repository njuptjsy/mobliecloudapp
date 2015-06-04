package com.njuptjsy.cloudclient.utils;

import java.io.File;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

//import static com.njuptjsy.cloudclient.utils.InfoContainer.*;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 *已静态方法的形式保存cloudclient的常用操作
 * * */
public class ClientUtils {
	public static boolean connectInternet(Context context) {//是否连接网络
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
	
	public static File[] getFiles(String filesName){//将文件字符串名装换成文件数组
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
	
	public static boolean authenticate(String username,String pwd,InfoContainer.CLOUD cloudType) {//验证用户的账号密码，这里没有根据不同的云验证不同的账号密码
		if (InfoContainer.USER_NAME.equalsIgnoreCase(username) && InfoContainer.PASSWORD.equalsIgnoreCase(pwd)) 
			return true;
		else
			return false;
	}
	
	//可以将toast这样常用的方法进行简化
	
	/**  
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名  
     * @param encryptText 被签名的字符串  
     * @param encryptKey  密钥  
     * @return  
     * @throws Exception  
     */   
	public static String HmacSHA1Encrypt(String encryptKey,String encryptText) throws Exception     
    {   
		String macMethod = "HmacSHA1";
		String encoding = "UTF-8";
        byte[] keyBytes=encryptKey.getBytes(encoding);  
        SecretKey secretKey = new SecretKeySpec(keyBytes, macMethod);//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称     
        Mac mac = Mac.getInstance(macMethod);//生成一个指定 Mac算法 的 Mac对象     
        mac.init(secretKey);//用给定密钥初始化 Mac 对象      
        byte[] text = encryptText.getBytes(encoding);    
        return new String(mac.doFinal(text));    
    }
}
