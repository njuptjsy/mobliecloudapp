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
        //������һ�����⣨TextView����һ����ť��Button�����
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
				//����ϴ��ƶ��¼�
				
			}
		};
		
		sendtocloud = (Button)findViewById(R.id.sendToCloud);
		selectBtn=(Button)findViewById(R.id.selectFilesBtn);
		pathView=(TextView)findViewById(R.id.filepath);
		selectBtn.setOnClickListener(selectListener);
		
		IntentFilter filter_dynamic = new IntentFilter();
		filter_dynamic.addAction(DYNAMICACTION);
		registerReceiver(dynamicReceiver, filter_dynamic);//ע��һ�����ڽ���DYNAMICACTION Action�Ĺ㲥������
    }
    
    //�Զ��嶯̬�㲥������ �ڲ���,�����û�ѡ���·��
 	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
 		
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			Log.e("MainActivity", "�����Զ��嶯̬ע��㲥��Ϣ");
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
