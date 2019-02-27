package com.pkit.launcher.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pkit.launcher.R;

public class SreenTextAdapter extends BaseAdapter {
	private Context context;
	private List<String> datas;
	private ColorStateList itemTextColor;
	public SreenTextAdapter(Context context,List<String> datas){
		this.context=context;
		this.datas=datas;
		itemTextColor=context.getResources().getColorStateList(R.drawable.screen_list_text_color);
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	     TextView itemView=new TextView(context);
	     LayoutParams textParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	     itemView.setLayoutParams(textParams);
	     itemView.setTextSize(16);
	     itemView.setTextColor(itemTextColor);
	     itemView.setText(datas.get(position));
		return itemView;
	}
}
