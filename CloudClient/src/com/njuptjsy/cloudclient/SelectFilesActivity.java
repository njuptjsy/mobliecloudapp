package com.njuptjsy.cloudclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/*
 * file explore in upload data 
 * 
 * */
public class SelectFilesActivity extends Activity {
    private Button selectBtn,sendtocloud;
    private TextView pathView;
    private static final String DYNAMICACTION = "com.njuptjsy.cloudclient.SelectFilesActivity";
    private OnClickListener selectListener,sendListener;
    private String text="";
    private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);
        //界面有一个标题（TextView）和一个按钮（Button）组成
        text = getString(R.string.Select_file_path)+"\n";
        initHandler();
        
        selectListener=new OnClickListener() {//define listener for select file button
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SelectFilesActivity.this,FileExplore.class);
				startActivity(intent);
			}
		};
		
		sendListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!UserAuthen.isLogin) {
					Toast.makeText(SelectFilesActivity.this, SelectFilesActivity.this.getString(R.string.please_login), Toast.LENGTH_LONG).show();
					return;
				}
				//点击上传云端
				MainActivity.showProcessDialog(getString(R.string.upload_data), getString(R.string.please_wait), SelectFilesActivity.this);
				UploadFiles uploadFiles = new UploadFiles(getFilesName(), SelectFilesActivity.this, handler);
				Thread uploadThread = new Thread(uploadFiles);
				uploadThread.start();
			}
		};
		
		sendtocloud = (Button)findViewById(R.id.sendToCloud);
		selectBtn=(Button)findViewById(R.id.selectFilesBtn);
		pathView=(TextView)findViewById(R.id.filepath);
		selectBtn.setOnClickListener(selectListener);
		sendtocloud.setOnClickListener(sendListener);
		
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(DYNAMICACTION);
		registerReceiver(dynamicReceiver, filter_dynamic);//注册一个用于接受DYNAMICACTION Action的广播接收器
		
    }
    
    //自定义动态广播接收器 内部类,接收用户选择的路径
 	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
 		
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			Log.e("SelectFilesActivity:onReceive", "receive customize dynamic broadcast information");
 			if(intent.getAction().equals(DYNAMICACTION)){
 				String path = intent.getStringExtra("path");
 				Toast.makeText(context, path, Toast.LENGTH_SHORT).show();
 				
 				text=text+"\n"+path;
 				pathView.setText(text);
 			}
 		}
 	};
 	
 	@Override
 	public void onBackPressed() {
 		unregisterReceiver(dynamicReceiver);
 		finish();
        super.onBackPressed();
 	}
    
 	private String getFilesName(){
 		return text.replace(getString(R.string.Select_file_path)+"\n\n", "");
 	}
 	
 	private void initHandler(){
 		handler = new Handler(SelectFilesActivity.this.getMainLooper()){
 			@Override
 			public void handleMessage(Message msg){
 				MainActivity.progressDialog.dismiss();
 			}
 			
 		};
 		
 	}
}
