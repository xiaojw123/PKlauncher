package com.pkit.launcher.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;

/**
 * 影片分类列表适配
 * 
 * @author Richard
 *
 */
public class MediaBrowserMenuAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Content> categoryList;
	private float textSize = 18;
	private int textHeight = 74;
	private ColorStateList colorList;

	public MediaBrowserMenuAdapter(Context context) {
		this.context = context;
		this.categoryList = new ArrayList<Content>();
		this.textSize = context.getResources().getDimension(R.dimen.text_size_18_sp);
		this.textHeight = (int) context.getResources().getDimension(R.dimen.media_browser_menu_item_height);
		this.colorList = context.getResources().getColorStateList(R.drawable.media_browser_text_color);
	}

	public void setCategoryList(ArrayList<Content> categoryList) {
		this.categoryList = categoryList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return categoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return categoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView categoryView = (TextView) convertView;
		if (categoryView == null) {
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, textHeight);
			categoryView = new TextView(context);
			categoryView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			categoryView.setTextColor(colorList);
			categoryView.setGravity(Gravity.CENTER);
			categoryView.setLayoutParams(params);
			categoryView.setSingleLine();
			categoryView.setEllipsize(TruncateAt.END);
		}
		Container category = (Container) categoryList.get(position);
		categoryView.setText(category.name);
		return categoryView;
	}
}
