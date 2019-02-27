package com.pkit.launcher.view.adapter;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.pkit.launcher.service.aidl.Item;

public class SearchResultAdapter extends MediaBrowserItemAdapter {

	public SearchResultAdapter(Context context) {
		super(context);
	}

	protected void setName(TextView nameView, Item item) {
		String name = ((item == null || item.name == null) ? "" : item.name);
		nameView.setText(Html.fromHtml(name));
	}

}
