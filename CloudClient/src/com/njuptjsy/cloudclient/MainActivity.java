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

import com.njuptjsy.cloudclient.InfoContainer.*;

import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private String [] MainItem;  
	public enum enumView{vauthen,vgirdAct,vhelp,vabout,vemail,vresource,vdownload};//for use switch case in setActiveView function 
	private View authenView,helpView,aboutView,EmailView,resourceView,gridAct,currentView,downloadView;
	private long firstTime;
	private WebView webview,resmanageView;
	public TextView username,password,problem,fileToDownload;
	public Button login,forgetpsw,help,sendmail,download;
	private AlertDialog aboutDialog;
	private Handler mainHandler,messageHandler;
	public static ProgressDialog progressDialog;
	private ListView fileList;
	private List<Map<String, Object>> displayList,filesList,selectedFlies;
	private SimpleAdapter adapterForFileList;
	private String selectedFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainItem = new String [] {getString(R.string.authentication),getString(R.string.res_manage),
				getString(R.string.upload_data),getString(R.string.download_data),getString(R.string.show_data),
				getString(R.string.send_mail),getString(R.string.user_guide),getString(R.string.about_cloudclient)};

		gridAct = getLayoutInflater().inflate( R.layout.activity_main, null);
		authenView = getLayoutInflater().inflate( R.layout.authen, null);
		helpView = getLayoutInflater().inflate(R.layout.cc_user_guide, null);
		EmailView = getLayoutInflater().inflate(R.layout.emailview, null);
		resourceView = getLayoutInflater().inflate(R.layout.resourcemanage, null);
		downloadView = getLayoutInflater().inflate(R.layout.download_file, null);

		displayList =new ArrayList<Map<String,Object>>();
		filesList = new ArrayList<Map<String,Object>>();
		selectedFlies = new ArrayList<Map<String,Object>>();
		initHandler();
		initgridAct();
		initAuthen();
		intiEmailView();
		intiResourceView();
		initDownloadView();
		setActiveView(enumView.vgirdAct);//切换目前的显示界面

	}

	private void initDownloadView() {
		fileList = (ListView)downloadView.findViewById(R.id.cloud_file_list);
		buildDownlaodListView(fileList);
		fileToDownload = (TextView)downloadView.findViewById(R.id.result_name);
		selectedFile = getString(R.string.Select_file_path) + System.getProperty("line.separator");
		download = (Button)downloadView.findViewById(R.id.download);
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//start download files thread
				showProcessDialog(getString(R.string.download_data),getString(R.string.please_wait),MainActivity.this);
				DownLoadFiles downLoadFiles = new DownLoadFiles(selectedFlies,MainActivity.this,mainHandler);
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
		String tag = "MainActivity：initAuthen";
		username = (TextView)authenView.findViewById(R.id.username);
		password = (TextView)authenView.findViewById(R.id.userkey);
		login = (Button)authenView.findViewById(R.id.log_in);
		forgetpsw = (Button)authenView.findViewById(R.id.forgetpassword);
		help = (Button)authenView.findViewById(R.id.authenhelp);

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("MainActivity：initAuthen", "login button is clicked");
				showProcessDialog(getString(R.string.login_now),getString(R.string.please_wait),MainActivity.this);
				String name = username.getText().toString();//get the input string in login TextView
				String pwd =password.getText().toString();
				UserAuthen authen = new UserAuthen(name, pwd,MainActivity.this,mainHandler);
				Thread authenThread = new Thread(authen);
				authenThread.start();
			}
		});
	}

	public void initgridAct()
	{
		GridView gridView;

		gridView = (GridView) gridAct.findViewById(R.id.gridView1);

		gridView.setAdapter(new ImageAdapter(this, MainItem, gridView));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				onGridItemClicked(parent, v, position, id);
			}
		});
	}

	private void onGridItemClicked (AdapterView<?> parent, View v, int position, long id)
	{
		Intent intent;
		switch(position)
		{
		case 0:
			setActiveView(enumView.vauthen);
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
			fileToDownload.setText(R.string.file_in_cloud);
			setActiveView(enumView.vdownload);
			showProcessDialog(getString(R.string.checking_cloud), getString(R.string.please_wait), MainActivity.this);
			startQueryCloud();
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
				case USER_UNAUTHEN:
					Toast.makeText(MainActivity.this, getString(R.string.user_unauthen), Toast.LENGTH_LONG).show();
					break;
				case LOGIN_SUCCESS:
					Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();
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
		QueryCloud queryCloud = new QueryCloud(MainActivity.this,messageHandler);
		Thread queryThread = new Thread(queryCloud);
		queryThread.start();
	}

	private void refactorQueryResult(Map<String, List<String>> queryResult){
		//private List<Map<String, Object>> buildListForSimpleAdapter
		//SimpleAdapter notes = new SimpleAdapter(this, list, R.layout.file_row,new String[] { "name", "path" ,"img"}, new int[] { R.id.name,R.id.desc ,R.id.img});//构建listView
		displayList.clear();
		filesList.clear();
		Set <String> keys = queryResult.keySet();
		Iterator<String> iterator = keys.iterator();
		Collection <List<String>> values = queryResult.values();
		Iterator<List<String>> listIterator = values.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> rootListItem = new HashMap<String,Object>();
			String bucketName = iterator.next().toString();
			rootListItem.put("name", bucketName);
			rootListItem.put("path", "/");
			rootListItem.put("img",R.drawable.directory );
			displayList.add(rootListItem);

			List<String> fileItem = new ArrayList<String>();
			fileItem = listIterator.next();
			Iterator<String> itemIterator = fileItem.iterator();
			while (itemIterator.hasNext()) {
				Map<String, Object> fileListItem = new HashMap<String,Object>();
				fileListItem.put("name", itemIterator.next().toString());
				fileListItem.put("path", "/"+bucketName);
				fileListItem.put("img",R.drawable.file_doc );
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
				buildDownlaodListView(fileList);	
			}
			else {
				String bucketName,fileKey;
				bucketName = displayList.get(position).get("path").toString().replace("/", "");
				fileKey = displayList.get(position).get("name").toString();
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
	
	private void recordSelectedFiles(String bucketName,String fileKey){
		Map<String, Object> fileAttribute = new HashMap<String, Object>();
		fileAttribute.put("bucketName", bucketName);
		fileAttribute.put("fileKey", fileKey);
		selectedFlies.add(fileAttribute);
	}
	
	private void buildDownlaodListView(ListView listView){
		adapterForFileList = new SimpleAdapter(this, displayList, R.layout.file_row,
				new String[] { "name", "path" ,"img"}, new int[] { R.id.name,
						R.id.desc ,R.id.img});//构建listView
		listView.setAdapter(adapterForFileList);
		listView.setSelection(0);
		listView.setOnItemClickListener(new ListViewListener());
	}

	private void showSelectedFile(String fileKey){
		if (selectedFile.contains(fileKey)) {
			Toast.makeText(this, getString(R.string.had_selected), Toast.LENGTH_SHORT).show();
			return;
		}
		selectedFile = selectedFile + fileKey + System.getProperty("line.separator");
		fileToDownload.setText(selectedFile);
	}
}
