package com.njuptjsy.cloudclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
/**
 * listView use MyAdapter can show picture ,
 * text and checkbox together
 * */
public class MyAdapter extends SimpleAdapter{

	private LayoutInflater mInflater;    
	private List<Map<String, Object>> data;
	private int viewResourceId;
	private String[] from;
	private int[] to;
	public static Map<Integer, Boolean> isSelected;    
	
	public MyAdapter(Context context,List<Map<String, Object>> data, int viewResourceId,String[] from, int[] to) {
		super(context, data, viewResourceId, from, to);
		mInflater = LayoutInflater.from(context);
		this.data = data;
		this.viewResourceId = viewResourceId;
		this.from = from;
		this.to = to;
		init();    
	}    

	private void init() {    
		isSelected = new HashMap<Integer, Boolean>();    
		for (int i = 0; i < data.size(); i++) {    
			isSelected.put(i, false);    
		}    
	}    

	@Override    
	public int getCount() {
		super.getCount();
		return data.size();    
	}    

	@Override    
	public Object getItem(int position) {
		super.getItem(position);
		return null;    
	}    

	@Override    
	public long getItemId(int position) {
		super.getItemId(position);
		return 0;    
	}    

	@Override    
	public View getView(int position, View convertView, ViewGroup parent) {
		//super.getView(position, convertView, parent);
		ViewHolder holder = null;    
		//convertView涓簄ull鐨勬椂鍊欏垵濮嬪寲convertView銆�   
		if (convertView == null) {    
			holder = new ViewHolder();    
			convertView = mInflater.inflate(viewResourceId, null);    
			holder.img = (ImageView) convertView.findViewById(to[0]);    
			holder.name = (TextView) convertView.findViewById(to[1]);
			holder.desc = (TextView) convertView.findViewById(to[2]);
			holder.cBox = (CheckBox) convertView.findViewById(to[3]);    
			convertView.setTag(holder);
		} else {    
			holder = (ViewHolder) convertView.getTag();    
		}    
		holder.img.setImageResource((int)data.get(position).get(from[0])); 
		holder.name.setText(data.get(position).get(from[1]).toString());
		holder.desc.setText(data.get(position).get(from[2]).toString());
		//holder.cBox.setChecked(true);    
		return convertView;    
	}    

	public final class ViewHolder {    
		public ImageView img;    
		public TextView name;
		public TextView desc;
		public CheckBox cBox;    
	}    
}    