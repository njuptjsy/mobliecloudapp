package com.njuptjsy.cloudclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetUtils {
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
}
