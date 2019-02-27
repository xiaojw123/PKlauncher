package com.pkit.launcher.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;

public class ActorAdaper extends BaseAdapter {
	LayoutInflater mInflater;
	List<String> mData;
	Context mContext;
	ColorStateList colorList;
	Resources mResources;
	float selectActorTextSize, unselectActorTextSize;
	int height;
	public ActorAdaper(Context context, List<String> data) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mData = data;
		mResources = mContext.getResources();
		height = (int) mResources.getDimension(R.dimen.pLay_exit_actorlist_item_height);
		selectActorTextSize = mResources.getDimension(R.dimen.text_size_22_sp);
		unselectActorTextSize = mResources.getDimension(R.dimen.text_size_18_sp);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout actorItemView=(RelativeLayout) convertView;
		if(actorItemView==null){
		actorItemView=new RelativeLayout(mContext);
		AbsListView.LayoutParams params = getParams(); 
		actorItemView.setLayoutParams(params);
		actorItemView.setGravity(Gravity.CENTER_VERTICAL);
		TextView actorNameView=new TextView(mContext);
		actorNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, unselectActorTextSize);
		actorNameView.setTextColor(mResources.getColor(R.color.color_bfbfbf));
		actorNameView.setGravity(Gravity.CENTER_VERTICAL);
		actorNameView.setPivotX(0);
		actorNameView.setPivotY(0);
		actorItemView.addView(actorNameView);
		}
		TextView actorNameView=(TextView) actorItemView.getChildAt(0);
		actorNameView.setText(mData.get(position).trim());
		return actorItemView;
	}
	private AbsListView.LayoutParams getParams() {
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		return params;
	}

}
