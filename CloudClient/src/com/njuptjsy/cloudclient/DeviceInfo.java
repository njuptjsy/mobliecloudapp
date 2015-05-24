package com.njuptjsy.cloudclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class DeviceInfo {
	private StringBuffer tv;
	private long totalSizeInSd;
	private long usefulSizeInSd;
	private long totalSizeInternal;
	private long usefulSizeInternal;
	private long usefulMemorySize;
	private int scale = -1;
	private int level = -1;
	private int voltage = -1;
	private int temperature = -1;
	private BroadcastReceiver batteryLevelReceiver;
	private Map<String, Integer> cpuInfo;
	
	public Map<String, Integer> getBatteryLevel() {
		Map<String, Integer> batteryLevel = new HashMap<String, Integer>();
		batteryLevel.put("level", level);
		batteryLevel.put("scale", scale);
		batteryLevel.put("voltage", voltage);
		batteryLevel.put("temperature", temperature);
		return batteryLevel;
	}

	public Map<String, Integer> getCpuUsage(){
		return cpuInfo;
	}

	public Map<String, Long> getStorageInfo() {
		Map<String, Long> storageSize = new HashMap<>();
		storageSize.put("totalSizeInSd", totalSizeInSd);
		storageSize.put("usefulSizeInSd", usefulSizeInSd);
		storageSize.put("totalSizeInternal",totalSizeInternal);
		storageSize.put("usefulSizeInternal", usefulSizeInternal);
		return storageSize;
	}

	public Map<String, Long> getMemoryInfo(){
		Map<String, Long> memoryInfo = new HashMap<>();
		memoryInfo.put("usefulMemorySize", usefulMemorySize);
		//memoryInfo.put("totalMemorySize", totalMemorySize);
		return memoryInfo;
	}

	public void batteryLevel() {  
		batteryLevelReceiver = new BroadcastReceiver() {  
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
	            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
	            Log.i("BatteryManager", "level is "+level+"/"+scale+", temp is "+ temperature +", voltage is "+voltage);
	        }
	    };
	    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    MyApplication.getContext().registerReceiver(batteryLevelReceiver, filter);
		} 

	public void  unregisterBatteryReceiver() {
		MyApplication.getContext().unregisterReceiver(batteryLevelReceiver);
	}
	
	public void CpuUsage() throws Exception{
		String topresult;
		Process process = Runtime.getRuntime().exec("top -n 1");

		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
		while((topresult = br.readLine())!=null)
		{
			if(topresult.trim().length()<1){
				continue;
			}else{
				Log.i("CpuUsage", topresult);
				String[] cpuInfoStrings = topresult.split(",");
				cpuInfo = new HashMap<String,Integer>();
				cpuInfo.put("Users",findNumInString(cpuInfoStrings[0]));
				cpuInfo.put("sys", findNumInString(cpuInfoStrings[1]));
				break;
			}
		}
	}

	private int findNumInString(String cpuInfoString){
		int percent;
		Pattern pattern = Pattern.compile("\\w*\\s(\\d*)\\%"); 
		Matcher matcher = pattern.matcher(cpuInfoString);
		if (matcher.find()) {
			String temp = matcher.group(1);
			percent = Integer.parseInt(temp);
		}
		else {
			percent = -1;
		}
		return percent;
	}
	
	public void SDCardSize() {  
		String state = Environment.getExternalStorageState();  
		if (Environment.MEDIA_MOUNTED.equals(state)) {  
			File sdcardDir = Environment.getExternalStorageDirectory();  
			StatFs sf = new  StatFs(sdcardDir.getPath());  
			long  blockSize = sf.getBlockSize();  
			long  blockCount = sf.getBlockCount();  
			long  availCount = sf.getAvailableBlocks();  
			Log.d("" ,  "block大小:" + blockSize+ ",block数目:" + blockCount+ ",总大小:" +blockSize*blockCount/ 1024 /1024+ "MB" ); 
			totalSizeInSd = blockSize*blockCount/ 1024 /1024;
			Log.d("" ,  "可用的block数目：:" + availCount+ ",剩余空间:" + availCount*blockSize/ 1024 /1024+ "MB" );
			usefulSizeInSd = availCount*blockSize/ 1024 / 1024;
		}
		else {
			totalSizeInSd = 0L;
			usefulSizeInSd = 0L;
		}
	}

	public void internalStorageSize(){
		File root = Environment.getRootDirectory();  
		StatFs sf = new StatFs(root.getPath());  
		long blockSize = sf.getBlockSize();  
		long blockCount = sf.getBlockCount();  
		long availCount = sf.getAvailableBlocks();  
		Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"MB"); 
		totalSizeInternal = blockSize*blockCount/1024/1024;
		Log.d("", "可用的block数目：:"+ availCount+",可用大小:"+ availCount*blockSize/1024+"MB"); 
		usefulSizeInternal = availCount*blockSize/1024/1024;
	}

	public void memoryInfo(){
		//获得MemoryInfo对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		//获取系统服务信息
		ActivityManager myActivityManager = (ActivityManager)MyApplication.getContext().getSystemService(Activity.ACTIVITY_SERVICE);
		//获得系统可用内存，保存在MemoryInfo对象上
		myActivityManager.getMemoryInfo(memoryInfo);
		usefulMemorySize = memoryInfo.availMem / 1024 /1024;
		//totalMemorySize = memoryInfo.totalMem;
	}
}
