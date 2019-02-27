package com.pkit.launcher.view.adapter;

import java.util.ArrayList;
import java.util.List;

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
import com.pkit.launcher.service.aidl.Tag;

/**
 * 影片分类列表适配
 * 
 * @author Richard
 *
 */
public class SearchSelectorTextAdapter extends BaseAdapter {
	private Context context;
	private List<Tag> tagList;
	private float textSize = 18;
	private ColorStateList colorList;
	private int height;

	public SearchSelectorTextAdapter(Context context) {
		this.context = context;
		this.tagList = new ArrayList<Tag>();
		this.textSize = context.getResources().getDimension(R.dimen.text_size_16_sp);
		this.colorList = context.getResources().getColorStateList(R.drawable.media_browser_text_color);
		this.height = (int) context.getResources().getDimension(R.dimen.z_search_selector_item_height);
	}

	public void setTextList(List<Tag> tagList) {
		this.tagList = tagList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return tagList.size();
	}

	@Override
	public Object getItem(int position) {
		return tagList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) convertView;
		if (textView == null) {
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height);
			textView = new TextView(context);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			textView.setTextColor(colorList);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(params);
			textView.setSingleLine();
			textView.setEllipsize(TruncateAt.END);
		}
		Tag tag = tagList.get(position);
		String text = tag.getTagName();
		textView.setText(text);
		textView.setTag(tag);
		return textView;
	}
}
