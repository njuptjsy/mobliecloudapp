package com.njuptjsy.cloudclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import com.amazonaws.services.s3.model.Bucket;
import com.njuptjsy.cloudclient.MyAdapter.ViewHolder;
import com.njuptjsy.cloudclient.authen.AWSAuthen;
import com.njuptjsy.cloudclient.authen.AliyunAuthen;
import com.njuptjsy.cloudclient.authen.UserAuthen;
import com.njuptjsy.cloudclient.download.AWSDownLoad;
import com.njuptjsy.cloudclient.query.DeviceInfo;
import com.njuptjsy.cloudclient.query.QueryAWS;
import com.njuptjsy.cloudclient.upload.SelectFilesActivity;
import com.njuptjsy.cloudclient.utils.InfoContainer;
import com.njuptjsy.cloudclient.utils.LogUtil;
import com.njuptjsy.cloudclient.utils.InfoContainer.*;

import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
/**
 * the main activity of CloudClient application
 * */
public class MainActivity extends BaseActivity {
	private String [] mainItem;  
	public enum enumView{vauthen,vafterAuthen,vgirdAct,vhelp,vabout,vemail,vresource,vdownload};//for use switch case in setActiveView function 
	private View authenView,afterAuthenView,helpView,aboutView,EmailView,resourceView,gridAct,currentView,downloadView;
	private long firstTime;
	private WebView webview,resmanageView;
	public TextView username,password,problem,fileToDownload,authenTitle,authenInfo;
	public Button login,logout,sendmail,download;
	private AlertDialog aboutDialog;
	private Handler mainHandler,messageHandler;
	public static ProgressDialog progressDialog;
	private ListView fileListView;
	private List<Map<String, Object>> displayList,filesList,selectedFlies;
	private SimpleAdapter adapterForFileList;
	private String selectedFile,cloudName;
	private CheckBox rememberPwd,rememberUser;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private DatabaseHelper dbhHelper;
	private boolean needQuery;
	public static boolean isLogin;
	private DeviceInfo deviceInfo;
	public static int selectedCloud;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mainItem = new String [] {getString(R.string.authentication),getString(R.string.res_manage),
				getString(R.string.upload_data),getString(R.string.download_data),getString(R.string.show_data),
				getString(R.string.send_mail),getString(R.string.computer_offload),getString(R.string.about_cloudclient)};
		displayList =new ArrayList<Map<String,Object>>();
		filesList = new ArrayList<Map<String,Object>>();
		selectedFlies = new ArrayList<Map<String,Object>>();
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		dbhHelper = new DatabaseHelper(this, "CloudClient.db", null, 1);
		
		afterAuthenView = getLayoutInflater().inflate(R.layout.afertauthen, null);
		gridAct = getLayoutInflater().inflate( R.layout.activity_main, null);
		authenView = getLayoutInflater().inflate( R.layout.authen, null);
		helpView = getLayoutInflater().inflate(R.layout.cc_user_guide, null);
		EmailView = getLayoutInflater().inflate(R.layout.emailview, null);
		resourceView = getLayoutInflater().inflate(R.layout.resourcemanage, null);
		downloadView = getLayoutInflater().inflate(R.layout.download_file, null);

		registerBatteryReceiver();
		initHandler();
		initgridAct();
		initAuthen();
		intiEmailView();
		intiResourceView();
		initDownloadView();		
		setActiveView(enumView.vgirdAct);//切换目前的显示界面
		needQuery = true;
		getSystemInfo();
	}

	@Override
	protected void onStart() {
		//活动从不可见变为可见时调用
		super.onStart();
	}

	@Override
	protected void onResume() {
		//在活动准备好和用户进行交互的时候调用。此时的活动一定位于返回栈的栈顶，并且处于运行状态。
		super.onResume();
	}

	@Override
	protected void onPause() {
		//当本活动失去焦点的时候调用，其中代码执行速度一定要快
		super.onPause();
	}

	@Override
	protected void onStop() {
		//在活动完全不可见的时候调用
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		//在活动被销毁之前调用
		unregisterBatteryReceiver();
		super.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		//由停止状态变为运行状态之前调用，重启活动时调用
		super.onRestart();
	}
	
	private void initDownloadView() {
		fileListView = (ListView)downloadView.findViewById(R.id.cloud_file_list);
		buildDownlaodListView(fileListView);
		fileToDownload = (TextView)downloadView.findViewById(R.id.result_name);
		selectedFile = getString(R.string.Select_file_path) + System.getProperty("line.separator");
		download = (Button)downloadView.findViewById(R.id.download);
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedFlies.isEmpty()) {
					Toast.makeText(MainActivity.this, getString(R.string.please_select), Toast.LENGTH_LONG).show();
					return;
				}
				fileSelectedByCheckbox();
				//start download files thread
				showProcessDialog(getString(R.string.download_data),getString(R.string.please_wait),MainActivity.this);
				AWSDownLoad downLoadFiles = new AWSDownLoad(selectedFlies,MainActivity.this,mainHandler);
				Thread downloadThread = new Thread(downLoadFiles);
				downloadThread.start();
			}
		});
	}

	private void intiResourceView() {
		resmanageView = (WebView)resourceView.findViewById(R.id.resmanage);
		WebSettings webSettings = resmanageView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		//webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//��Ӧ��Ļ���ܲ��ÿ�
		//优化webkit加载时间
		webSettings.setUseWideViewPort(true); 
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setBlockNetworkImage(true);

		resmanageView.loadUrl("http://192.168.1.109/horizon/");
		resmanageView.setWebViewClient(new WebViewClient (){
			@Override 
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{  
				view.loadUrl(url);  
				return true;  
			}  
		});
	}

	private void intiEmailView() {
		problem = (TextView)EmailView.findViewById(R.id.problem);
		sendmail = (Button)EmailView.findViewById(R.id.sendmail);
		sendmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String problemString =  problem.getText().toString();

				if (SendEmail.state) {
					Toast.makeText(MainActivity.this, getString(R.string.wait_send), Toast.LENGTH_SHORT).show();
					return;
				}
				else {
					SendEmail sendEmail = SendEmail.getInstance();
					String moblieMac =  sendEmail.getMacAddress(MainActivity.this);
					sendEmail.getData(moblieMac,problemString);
					Thread send = new Thread(sendEmail);
					send.start();
					Toast.makeText(MainActivity.this, getString(R.string.sending), Toast.LENGTH_SHORT).show();
				}
			}
		});


	}

	private void initAuthen() {
		final String tag = "MainActivity：initAuthen";
		authenTitle = (TextView)authenView.findViewById(R.id.authentitle);
		authenInfo = (TextView)afterAuthenView.findViewById(R.id.loginfo);
		username = (EditText)authenView.findViewById(R.id.username);
		password = (EditText)authenView.findViewById(R.id.userkey);
		login = (Button)authenView.findViewById(R.id.log_in);
		logout = (Button)afterAuthenView.findViewById(R.id.log_out);
		rememberPwd = (CheckBox)authenView.findViewById(R.id.remember_pwd);
		rememberUser = (CheckBox)authenView.findViewById(R.id.remember_user);

		rememberPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rememberPwd.isChecked()) {
					rememberUser.setChecked(true);
				}
			}
		});

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.i(tag, "login button is clicked");
				if (InfoContainer.userAuthenIsRunning) {
					LogUtil.i(tag, "Authen thread is running");
					Toast.makeText(MainActivity.this,getString(R.string.loginnow), Toast.LENGTH_LONG).show();
					return;
				}

				showProcessDialog(getString(R.string.login_now),getString(R.string.please_wait),MainActivity.this);
				String name = username.getText().toString();//get the input string in login TextView
				String pwd =password.getText().toString();
				SaveUserInfo(name,pwd);
				UserAuthen authen = null;
				switch (selectedCloud) {
				case 0:
					authen = new AliyunAuthen(name, pwd,MainActivity.this,mainHandler);
					break;
				case 1:
					authen = new AWSAuthen(name, pwd,MainActivity.this,mainHandler);
					break;
				case 2:

					break;
				default:
					break;
				}
				
				Thread authenThread = new Thread(authen);
				authenThread.start();
			}
		});
		isRemeberUserInfo();
		
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isLogin) {
					Toast.makeText(MainActivity.this,getString(R.string.please_login), Toast.LENGTH_LONG).show();
					return;
				}
				isLogin = false;
				setActiveView(enumView.vauthen);
				selectCloud();
				Toast.makeText(MainActivity.this, getString(R.string.logout), Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void initgridAct()
	{
		GridView gridView;

		gridView = (GridView) gridAct.findViewById(R.id.gridView1);

		gridView.setAdapter(new ImageAdapter(this, mainItem, gridView));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				onGridItemClicked(parent, v, position, id);
			}
		});
	}

	//响应gridview的按钮被点击
	private void onGridItemClicked (AdapterView<?> parent, View v, int position, long id)
	{
		Intent intent;
		switch(position)
		{
		case 0:
			if (isLogin) {
				setActiveView(enumView.vafterAuthen);
			}
			else {
				setActiveView(enumView.vauthen);
				selectCloud();
			}
			
			break;
		case 1:
			setActiveView(enumView.vresource);
			break;
		case 2:
			intent = new Intent(this,SelectFilesActivity.class);
			startActivity(intent);
			break;
		case 3:
			/*1.show progress dialog
			 *2.new thread to find file information from cloud
			 *3.use file information fill in ExpandableListView
			 * */
			if (!isLogin) {
				Toast.makeText(MainActivity.this,getString(R.string.please_login), Toast.LENGTH_LONG).show();
				return;
			}
			fileToDownload.setText(R.string.file_in_cloud);
			setActiveView(enumView.vdownload);
			if (needQuery) {
				showProcessDialog(getString(R.string.checking_cloud), getString(R.string.please_wait), MainActivity.this);
				startQueryCloud();
			}
			break;
		case 4:
			intent = new Intent(this, SimpleChart.class);
			startActivity(intent);
			break;
		case 5:
			setActiveView(enumView.vemail);
			break;
		case 6:
			showUserGuide();
			break;
		case 7:
			setActiveView(enumView.vabout);
			showAboutDailog();
			break;
		}

	}

	private void showUserGuide() {
		readHtmlFormAssets();
	}

	public void readHtmlFormAssets()
	{
		webview = (WebView)helpView.findViewById(R.id.user_guide);
		WebSettings webSettings = webview.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		if(Locale.getDefault().getLanguage().equalsIgnoreCase("en"))
		{
			webview.loadUrl("file:///android_asset/CloudClient_UserGuide_en.html");
		}
		else
		{
			webview.loadUrl("file:///android_asset/CloudClient_UserGuide_chn.html");
		}
		webview.setWebViewClient(new WebViewClient (){
			@Override 
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{  
				view.loadUrl(url);  
				return true;  
			}  
		}); //set web view

		setActiveView(enumView.vhelp);
	}

	private void showAboutDailog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.about_program));
		builder.setTitle(getString(R.string.about));
		//builder.create().show();
		aboutDialog = builder.create();
		aboutDialog.show();
	}

	private void setActiveView(enumView activevView) {
		switch (activevView) {
		case vauthen:
			setContentView(authenView);
			currentView = authenView;
			break;
		case vafterAuthen:
			setContentView(afterAuthenView);
			currentView = afterAuthenView;
			break;	
		case vgirdAct:
			setContentView(gridAct);
			currentView = gridAct;
			break;
		case vhelp:
			setContentView(helpView);
			currentView = helpView;
			break;
		case vabout:
			currentView = aboutView;
			break;
		case vemail:
			setContentView(EmailView);
			currentView = EmailView;
			break;
		case vresource:
			setContentView(resourceView);
			currentView = resourceView;
			break;
		case vdownload:
			setContentView(downloadView);
			currentView = downloadView;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (currentView == authenView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == gridAct) {
				long secondTime = System.currentTimeMillis();   
				if (secondTime - firstTime > 2000) {//If two keys interval greater than 2 seconds,then not quit program
					Toast toast = Toast.makeText(this, MainActivity.this.getString(R.string.exit_progress), Toast.LENGTH_SHORT);  
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					firstTime = secondTime;//update firstTime  
					return true;   
				} else {//If two keys interval less  than 2 seconds,then  quit program
					System.exit(0);  
				}   
			}

			if (currentView == helpView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == EmailView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == afterAuthenView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == resourceView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == downloadView) {
				setActiveView(enumView.vgirdAct);
				return true;
			}
			if (currentView == aboutView) {
				aboutDialog.dismiss();
				setActiveView(enumView.vgirdAct);
				return true;
			}
			break;


		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initHandler(){

		mainHandler = new Handler(getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				String tag = "MainActivty:initHandler";
				progressDialog.dismiss();
				switch ((MESSAGE_TYPE)msg.obj) {
				case USER_UNAUTHEN_FAIL:
					Toast.makeText(MainActivity.this, getString(R.string.user_unauthen_fail), Toast.LENGTH_LONG).show();
					break;
				case LOGIN_SUCCESS:
					isLogin = true;
					Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();
					authenInfo.setText(getString(R.string.logtocloud)+" "+cloudName);
					setActiveView(enumView.vafterAuthen);
					break;
				case LOGIN_FAILED_RETRY:
					Toast.makeText(MainActivity.this, getString(R.string.login_failed_retry), Toast.LENGTH_LONG).show();
					break;
				case LOGIN_FAILED_NO_INTERNET:
					Toast.makeText(MainActivity.this, getString(R.string.login_failed_no_internet), Toast.LENGTH_LONG).show();
					break;
				case SDCARD_UNMOUNTED:
					progressDialog.dismiss();
					Toast.makeText(MainActivity.this, getString(R.string.sdcard_unmounted), Toast.LENGTH_LONG).show();
					break;
				case DOWNLOAD_SUCCESS:
					progressDialog.dismiss();
					String path = Environment.getExternalStorageDirectory().getPath() + "/" + "CloudClientDownload/";
					Toast.makeText(MainActivity.this, getString(R.string.download_success) + path, Toast.LENGTH_LONG).show();
					break;
				case UPLOAD_SUCCESS:
					setActiveView(enumView.vgirdAct);
					Toast.makeText(MainActivity.this, getString(R.string.upload_success), Toast.LENGTH_LONG).show();
					needQuery = true;
					break;
				default:
					Log.e(tag, "undefined message type");
					break;
				}
			}
		};

		messageHandler = new Handler(getMainLooper()){
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				Map<String, List<String>> queryResult = (Map<String, List<String>>)((Map)msg.obj).get(MESSAGE_TYPE.QUERY_RESULT);//wait to refactor
				//queryResult：String in map means bucket name , String in list of map means Object key in this bucket
				//Map<MESSAGE_TYPE, Map<String, List<String>>>
				refactorQueryResult(queryResult);
				saveToDatabase(queryResult);
				needQuery = false;
			}
		};
	}

	public static void showProcessDialog(String title,String message,Context context){
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(true);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();

	}

	private void startQueryCloud(){
		if (QueryAWS.queryCloudIsRunning) {
			return;
		}
		QueryAWS queryCloud = new QueryAWS(MainActivity.this,messageHandler,mainHandler);
		Thread queryThread = new Thread(queryCloud);
		queryThread.start();
	}

	private void refactorQueryResult(Map<String, List<String>> queryResult){
		String tag = "MainActivity:refactorQueryResult";
		//private List<Map<String, Object>> buildListForSimpleAdapter
		//SimpleAdapter notes = new SimpleAdapter(this, list, R.layout.file_row,new String[] { "name", "path" ,"img"}, new int[] { R.id.name,R.id.desc ,R.id.img});//构建listView
		displayList.clear();
		filesList.clear();
		Set <String> keys = queryResult.keySet();
		Iterator<String> iterator = keys.iterator();
//		Collection <List<String>> values = queryResult.values();
//		Iterator<List<String>> listIterator = values.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> rootListItem = new HashMap<String,Object>();
			String bucketName = iterator.next().toString();
			rootListItem.put("name", bucketName);
			rootListItem.put("path", "/");
			rootListItem.put("img",R.drawable.directory );
			displayList.add(rootListItem);

			//List<String> fileItem = new ArrayList<String>();
//			List<String> fileItem = listIterator.next();
			List<String> fileItem = queryResult.get(bucketName);
			if (fileItem.isEmpty()) {
				Log.i(tag, "bucket: "+bucketName+" is empty");
				//where bug is
				continue;
			}
			Iterator<String> itemIterator = fileItem.iterator();
			while (itemIterator.hasNext()) {
				Map<String, Object> fileListItem = new HashMap<String,Object>();
				fileListItem.put("name", itemIterator.next().toString());
				fileListItem.put("path", "/"+bucketName);
				fileListItem.put("img",R.drawable.file_doc);
				filesList.add(fileListItem);
			}
		}
		adapterForFileList.notifyDataSetChanged();
	}

	class ListViewListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (displayList.get(position).get("path").toString().equals("/")) {
				displayList = getFilesInBucket(position);
				buildDownlaodListView(fileListView);	
			}
			else {
				String bucketName,fileKey;
				bucketName = displayList.get(position).get("path").toString().replace("/", "");
				fileKey = displayList.get(position).get("name").toString();

				ViewHolder viewHolder = (ViewHolder)view.getTag();
				viewHolder.cBox.toggle();//Change the checked state of the view to the inverse of its current state
				MyAdapter.isSelected.put(position, viewHolder.cBox.isChecked());

				showSelectedFile(fileKey);
				recordSelectedFiles(bucketName,fileKey);
			}
		}

	}

	private List<Map<String, Object>> getFilesInBucket(int id){
		String bucket = (String)displayList.get(id).get("name");
		Iterator<Map<String, Object>> iterator = filesList.iterator();
		List<Map<String, Object>> fileListInBucket = new ArrayList<Map<String,Object>>();
		while (iterator.hasNext()) {
			Map <String, Object> fileInBucket = iterator.next();
			String bucketName = fileInBucket.get("path").toString().replace("/", "");
			if (bucket.equalsIgnoreCase(bucketName)) {
				fileListInBucket.add(fileInBucket);
			}
		}
		return fileListInBucket;
	}

	private void buildDownlaodListView(ListView listView){
		//		adapterForFileList = new SimpleAdapter(this, displayList, R.layout.file_row,
		//				new String[] { "name", "path" ,"img"}, new int[] { R.id.name,
		//						R.id.desc ,R.id.img});//构建listView
		adapterForFileList = new MyAdapter(this, displayList, R.layout.file_item, new String[]{"img","name","path"}, new int[]{R.id.imageOfFile,R.id.fileName,R.id.filePath,R.id.fileCheckBox});
		listView.setAdapter(adapterForFileList);
		listView.setSelection(0);
		listView.setOnItemClickListener(new ListViewListener());
	}

	private void recordSelectedFiles(String bucketName,String fileKey){
		Map<String, Object> fileAttribute = new HashMap<String, Object>();
		fileAttribute.put("bucketName", bucketName);
		fileAttribute.put("fileKey", fileKey);
		if (selectedFlies.contains(fileAttribute)) {
			selectedFlies.remove(fileAttribute);
		}
		selectedFlies.add(fileAttribute);
	}

	private void showSelectedFile(String fileKey){
		if (selectedFile.contains(fileKey)) {
			//Toast.makeText(this, getString(R.string.had_selected), Toast.LENGTH_SHORT).show();
			selectedFile = selectedFile.replace(fileKey + System.getProperty("line.separator"), "");
		}
		else {
			selectedFile = selectedFile + fileKey + System.getProperty("line.separator");
		}
		fileToDownload.setText(selectedFile);
	}

	private void fileSelectedByCheckbox(){
		for(int i=0;i<fileListView.getCount();i++){    
			if(MyAdapter.isSelected.get(i)){
				String bucketName = displayList.get(i).get("path").toString().replace("/", "");
				String fileKey = displayList.get(i).get("name").toString();
				showSelectedFile(fileKey);
				recordSelectedFiles(bucketName,fileKey);
			}
		}
	}

	private void isRemeberUserInfo(){//根据SharedPreferences中保存的信息取出用户名密码，进行填充
		boolean isRememberUser = pref.getBoolean("rememberUser", false);
		boolean isRememberPwd = pref.getBoolean("rememberPwd", false);

		if (isRememberUser) {
			String userName = pref.getString("userName", "");
			username.setText(userName);
			rememberUser.setChecked(true);

			if (isRememberPwd) {
				String pwd = pref.getString("pwd", "");
				password.setText(pwd);
				rememberPwd.setChecked(true);
			}
		}
	}

	private void SaveUserInfo(String userName,String pwd){//在登录界面根据CheckBox的选择情况，记录用户名密码
		editor = pref.edit();
		if (rememberPwd.isChecked()) {
			editor.putString("userName", userName);
			editor.putString("pwd",pwd);
			editor.putBoolean("rememberUser", true);
			editor.putBoolean("rememberPwd", true);
		}

		if (rememberUser.isChecked()) {
			editor.putString("UserName", pwd);
			editor.putBoolean("rememberUser", true);
		}

		editor.commit();
	}

	private void saveToDatabase(Map<String, List<String>> queryResult){
		SQLiteDatabase database = dbhHelper.getWritableDatabase();
		database.delete("BucketInCloud", null, null);
		database.delete("FileInCloud", null, null);
		
		Set<String> bucketNames = queryResult.keySet();
		for(String bucketName : bucketNames){
			ContentValues values = new ContentValues();
			values.put("bucketName", bucketName);
			database.insert("BucketInCloud", null, values);
			values.clear();
			
			List<String> files = queryResult.get(bucketName);
			Iterator<String> iterator = files.iterator();
			while (iterator.hasNext()) {
				values.put("bucketName", bucketName);
				Log.v("MainActivit:saveToDatabase", "bucketName"+bucketName);
				String fileName = iterator.next();
				values.put("fileName", fileName);
				Log.v("MainActivit:saveToDatabase","fileName" + fileName);
				database.insert("FileInCloud", null, values);
			}
		}
		
	}

	private void getSystemInfo(){
		
		Map<String, Integer> battery = deviceInfo.getBatteryLevel();
		
		try {
			deviceInfo.CpuUsage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Integer> cpuUsage = deviceInfo.getCpuUsage();
		
		deviceInfo.SDCardSize();
		deviceInfo.internalStorageSize();
		Map<String, Long> storageInfo = deviceInfo.getStorageInfo();
		
		deviceInfo.memoryInfo();
		Map<String, Long> memoryInfo = deviceInfo.getMemoryInfo();
		Log.i("MainActivity:getSystemInfo", "battery level: " + battery + "\n"
				+ "CPU: " + cpuUsage + "\n"
				+ "Storage: "+ storageInfo + "\n"
				+ "Memory: "+ memoryInfo + "\n");
	}

	private void registerBatteryReceiver(){
		deviceInfo = new DeviceInfo();
		deviceInfo.batteryLevel();
	}
	
	private void unregisterBatteryReceiver() {
		deviceInfo.unregisterBatteryReceiver();
	} 
	
	private void selectCloud() {//显示云平台的选择框
		AlertDialog selectCloudDialog = new AlertDialog.Builder(this)  
		.setTitle(getString(R.string.selectcloud))  
		.setIcon(android.R.drawable.ic_dialog_info)                  
		.setSingleChoiceItems(new String[] {getString(R.string.aliyun),getString(R.string.aws),getString(R.string.openstack)}, selectedCloud,   
				new DialogInterface.OnClickListener() {  
			@Override
			public void onClick(DialogInterface dialog, int which) {  
				selectedCloud = which;//0 = aliyu 1=aws 2=openstack
				setAuthenTitle();
			}
			
		})
		.setNegativeButton(getString(R.string.sure), null)  
		.show();
		
	}
	
	private void setAuthenTitle() {
		switch (selectedCloud) {
		case 0:
			cloudName = getString(R.string.aliyun);
			break;
		case 1:
			cloudName = getString(R.string.aws);
			break;
		case 2:
			cloudName = getString(R.string.openstack);
			break;
		default:
			cloudName = getString(R.string.aliyun);
			break;
		}
		authenTitle.setText(cloudName);
	}
}
