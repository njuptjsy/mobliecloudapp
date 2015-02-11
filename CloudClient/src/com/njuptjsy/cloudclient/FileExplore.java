package com.njuptjsy.cloudclient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FileExplore extends Activity implements OnItemClickListener {
	private static final String TAG = "FileExplore";
	private static final int IM_PARENT = Menu.FIRST + 1;
	private static final int IM_BACK = IM_PARENT + 1;
	private static final String DYNAMICACTION = "com.njuptjsy.cloudclient.SelectFilesActivity";
	ListView itemlist = null;
	String path = "/";//��ʼ·���Ǹ�·��
	List<Map<String, Object>> list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files);
		setTitle(FileExplore.this.getString(R.string.file_explorer));
		itemlist = (ListView) findViewById(R.id.itemlist);
		refreshListItems(path);
	}
	
    /*����path����·���б�*/
	private void refreshListItems(String path) {
		setTitle(FileExplore.this.getString(R.string.file_explorer)+path);
		list = buildListForSimpleAdapter(path);
		
		SimpleAdapter notes = new SimpleAdapter(this, list, R.layout.file_row,
				new String[] { "name", "path" ,"img"}, new int[] { R.id.name,
						R.id.desc ,R.id.img});//����listView
		
		itemlist.setAdapter(notes);
		itemlist.setOnItemClickListener(this);//ΪlistView�󶨼����������ʱ�Զ�����onItemClick����
		itemlist.setSelection(0);
	}
    /*����·������һ������·�����б�
     * ���������б�����list������
     * ��������listView
     * */
	private List<Map<String, Object>> buildListForSimpleAdapter(String path) {

		List<Map<String, Object>> list;
		File[] files = new File(path).listFiles();//�г���·�����������ļ�
		if (files == null) {
			list = new ArrayList<Map<String, Object>>(2);//�½�һ�����������ļ����鳤��list
		}
		else {
			list = new ArrayList<Map<String, Object>>(files.length+2);//�½�һ�����������ļ����鳤��list
		}

		/*
		 *����listView��ͷ����ѡ�� 
		 *
		 * */
		Map<String, Object> root = new HashMap<String, Object>();//�����Ŀ¼
		root.put("name", "/");
		root.put("img", R.drawable.file_root);
		root.put("path", FileExplore.this.getString(R.string.to_root));
		list.add(root);
		Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("name", "...");
		pmap.put("img", R.drawable.file_parent);
		pmap.put("path", FileExplore.this.getString(R.string.to_parent));
		list.add(pmap);
		if (files != null) {
			for (File file : files){
				Map<String, Object> map = new HashMap<String, Object>();
				if(file.isDirectory()){
					map.put("img", R.drawable.directory);
				}else{
					map.put("img", R.drawable.file_doc);
				}
				map.put("name", file.getName());
				map.put("path", file.getPath());
				list.add(map);
			}
		}

		return list;
	}
	/*��ת����һ��*/
	
	private void goToParent() {//�Ż���һ��Ŀ¼
		File file = new File(path);
		File str_pa = file.getParentFile();
		if(str_pa == null){
			Toast.makeText(FileExplore.this,
					FileExplore.this.getString(R.string.alreadly_root),
					Toast.LENGTH_SHORT).show();
			refreshListItems(path);	
		}else{
			path = str_pa.getAbsolutePath();
			refreshListItems(path);	
		}
	}
    /*ʵ��OnItemClickListener�ӿ�
     * ��д����¼���Ӧ����
     * */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Log.i(TAG, "item clicked! [" + position + "]");
		if (position == 0) {
			path = "/";
			refreshListItems(path);
		}else if(position == 1){
			goToParent();
		} else {
			path = (String) list.get(position).get("path");
			File file = new File(path);
			if (file.isDirectory())
				refreshListItems(path);
			else
			{
				Toast.makeText(FileExplore.this,path,Toast.LENGTH_SHORT).show();
				sendPathToActivity(path);//����һ���㲥��SelectFilesActivit
				finish();
			}
			
		}

	}
	
	public void sendPathToActivity(String path){
		Intent intent = new Intent();
		intent.setAction(DYNAMICACTION);
		intent.putExtra("path", path);
		sendBroadcast(intent);
	}
	
	@Override
 	public void onBackPressed() {
		finish();
        super.onBackPressed();
 	}
}

