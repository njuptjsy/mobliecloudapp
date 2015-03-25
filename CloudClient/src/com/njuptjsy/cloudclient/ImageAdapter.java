package com.njuptjsy.cloudclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private final String[] mobileValues;
	public final int ROW_NUMBER=4;
	private GridView mGv;

	public ImageAdapter(Context context, String[] mobileValues) {
		this.context = context;
		this.mobileValues = mobileValues;
	}

	public ImageAdapter(Context context, String[] mobileValues, GridView mGv) {
		this.context = context;
		this.mobileValues = mobileValues;
		this.mGv = mGv;
	}

	int [] imageIds = //在gridview中对应的图片
		{
			R.drawable.authen,
			R.drawable.manage,
			R.drawable.upload,
			R.drawable.download,
			R.drawable.draw,
			R.drawable.send_mail,
			R.drawable.faceback,
			R.drawable.about	
		};

//	int [] nameIds = 
//		{
//			
//			R.string.check_board ,
//			R.string.boot_log,
//			R.string.history_records, 
//			R.string.download_certification,
//			R.string.upload_log, 
//			R.string.reset_router,
//			R.string.help_document,
//			R.string.about_thinkbox
//			
//		};

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.block, null);

			// set value into textview
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label);
			textView.setText(mobileValues[position]);

			// set image based on selected text
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_item_image);

			String mobile = mobileValues[position];

			imageView.setImageResource(imageIds[position]);
			Log.v("ImageAdapter", "convertView is null");
		} else {
			gridView = (View) convertView;
			Log.v("ImageAdapter", "convertView is not null");
		}

		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				mGv.getHeight()/ROW_NUMBER);
		gridView.setLayoutParams(param);

		return gridView;
	}

	@Override
	public int getCount() {
		Log.v("getCount", mobileValues.length+"");
		return mobileValues.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
