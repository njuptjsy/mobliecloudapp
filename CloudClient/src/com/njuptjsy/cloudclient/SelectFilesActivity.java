package com.njuptjsy.cloudclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
    private OnClickListener selectListener,sendlListener;
    private String text="";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);
        //界面有一个标题（TextView）和一个按钮（Button）组成
        text=SelectFilesActivity.this.getString(R.string.Select_file_path)+"\n";
        
        selectListener=new OnClickListener() {//define listener for select file button
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SelectFilesActivity.this,FileExplore.class);
				startActivity(intent);
			}
		};
		
		sendlListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击上传云端事件
				
			}
		};
		
		sendtocloud = (Button)findViewById(R.id.sendToCloud);
		selectBtn=(Button)findViewById(R.id.selectFilesBtn);
		pathView=(TextView)findViewById(R.id.filepath);
		selectBtn.setOnClickListener(selectListener);
		
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(DYNAMICACTION);
		registerReceiver(dynamicReceiver, filter_dynamic);//注册一个用于接受DYNAMICACTION Action的广播接收器
    }
    
    //自定义动态广播接收器 内部类,接收用户选择的路径
 	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
 		
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			Log.e("MainActivity", "接收自定义动态注册广播消息");
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
    
}
