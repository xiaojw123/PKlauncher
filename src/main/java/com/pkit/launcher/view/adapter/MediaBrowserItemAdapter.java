package com.pkit.launcher.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.utils.DrawableUtil;

/**
 * 影片列表适配
 * 
 * @author Richard
 *
 */
public class MediaBrowserItemAdapter extends BaseAdapter {
	// private Context context;
	private LayoutInflater inflater;
	private Item[] itemList;

	public MediaBrowserItemAdapter(Context context) {
		this(context, new Item[0]);
	}

	public MediaBrowserItemAdapter(Context context, Item[] itemList) {
		inflater = LayoutInflater.from(context);
		this.itemList = itemList;
	}

	public void setItemList(Item[] itemList) {
		if (itemList == null) {
			return;
		}
		this.itemList = itemList;
	}

	public void addItem(Item item, int position) {
		if (position >= itemList.length) {
			return;
		}
		this.itemList[position] = item;
	}

	public void setCount(int count) {
		this.itemList = new Item[count];
	}

	public int getCount() {
		return itemList.length;
	}

	public Object getItem(int position) {
		if (position >= itemList.length) {
			return null;
		}
		return itemList[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout itemView = (RelativeLayout) convertView;
		ItemViewHolder itemViewHolder = null;
		if (itemView == null) {
			itemView = (RelativeLayout) inflater.inflate(R.layout.media_browser_item_layout, parent, false);
			ImageView itemImg = (ImageView) itemView.findViewById(R.id.media_browser_item_img);
			TextView itemName = (TextView) itemView.findViewById(R.id.media_browser_item_name);

			itemViewHolder = new ItemViewHolder();
			itemViewHolder.imgView = itemImg;
			itemViewHolder.nameView = itemName;
			itemView.setTag(itemViewHolder);
		} else {
			itemViewHolder = (ItemViewHolder) itemView.getTag();
		}

		Item item = itemList[position];
		setImage(itemViewHolder.imgView, item);
		setName(itemViewHolder.nameView, item);
		return itemView;
	}

	protected void setImage(ImageView imgView, Item item) {
		if (item == null) {
			imgView.setImageResource(R.drawable.media_browser_item_loading);
			return;
		}
		// imgView.setImageResource(R.drawable.test_siguobianlu);
		String url = null;
		if (item.imgPaths != null && item.imgPaths.size() > 0) {
			url = item.imgPaths.get(0);
		}
		DrawableUtil.displayDrawable(url, R.drawable.media_browser_item_loading, imgView, 0);
	}

	protected void setName(TextView nameView, Item item) {
		String name = ((item == null || item.name == null) ? "" : item.name);
		nameView.setText(name);
	}

	public static class ItemViewHolder {
		public ImageView imgView;
		public TextView nameView;
	}
}
