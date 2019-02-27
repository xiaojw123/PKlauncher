package com.pkit.launcher.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.animation.MediaItemFocusHelper;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.utils.DrawableUtil;

public class SearchEmptyResultFragment extends Fragment {
	private Activity activity;
	private RelativeLayout resultLayout;
	private int mode = SearchMessageManager.SEARCH_MODE_1;
	private RelativeLayout recommentContainer;
	private int leftMargin;
	private int spacing;
	private int width;
	private int height;
	private List<Content> recommends;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		resultLayout = (RelativeLayout) inflater.inflate(R.layout.search_empty_result_layout, container, false);
		init();
		return resultLayout;
	}

	private void init() {
		TextView textView = (TextView) resultLayout.findViewById(R.id.search_result_text_3);
		if (mode == SearchMessageManager.SEARCH_MODE_1) {
			textView.setText("当前条件无搜索结果");
		} else {
			textView.setText("当前条件无筛选结果");
		}

		initParams();
		recommentContainer = (RelativeLayout) resultLayout.findViewById(R.id.search_recommend_item_list);
		updateRecommends();
	}

	private void initParams() {
		Resources mResources = activity.getResources();
		spacing = (int) mResources.getDimension(R.dimen.z_search_recommend_item_list_spacing);
		width = (int) mResources.getDimension(R.dimen.z_search_recommend_item_width);
		height = (int) mResources.getDimension(R.dimen.z_search_recommend_item_height);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setRecommends(List<Content> recommends) {
		this.recommends = recommends;
	}

	@SuppressLint("InflateParams")
	void updateRecommends() {
		if (recommends == null) {
			return;
		}
		int size = (recommends.size() > 4 ? 4 : recommends.size());
		for (int i = 0; i < size; i++) {
			Item item = (Item) recommends.get(i);
			View view = getView(item);
			view.setOnFocusChangeListener(allLookFocusChangeListener);
			view.setOnClickListener(allLookClickListener);
			recommentContainer.addView(view);
		}
		recommentContainer.requestLayout();
	}

	protected void setImage(ImageView imgView, Item item) {
		if (item == null) {
			imgView.setBackgroundResource(R.drawable.media_browser_item_loading);
			return;
		}
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

	@SuppressLint("InflateParams")
	private View getView(Item item) {
		LayoutParams params = new LayoutParams(width, height);
		params.leftMargin = leftMargin;
		leftMargin += (width + spacing);

		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.media_browser_item_layout, recommentContainer, false);
		ImageView itemImg = (ImageView) view.findViewById(R.id.media_browser_item_img);
		TextView itemName = (TextView) view.findViewById(R.id.media_browser_item_name);

		SearchItemViewHolder itemViewHolder = new SearchItemViewHolder();
		itemViewHolder.imgView = itemImg;
		itemViewHolder.nameView = itemName;
		itemViewHolder.id = item.contentID;
		view.setTag(itemViewHolder);
		view.setLayoutParams(params);
		view.setFocusable(true);

		setImage(itemImg, item);
		setName(itemName, item);
		return view;
	}

	private OnFocusChangeListener allLookFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				selected(v);
			} else {
				unSelected(v);
			}
			ViewGroup parent = (ViewGroup) v.getParent();
			parent.postInvalidate();
		}

		private void unSelected(View view) {
			if (view == null) {
				return;
			}
//			view.setScaleX(ZOOM_OUT);
//			view.setScaleY(ZOOM_OUT);
			MediaItemFocusHelper.unSelectedAnim(view);
			SearchItemViewHolder holder = (SearchItemViewHolder) view.getTag();
			if (holder == null) {
				return;
			}
			holder.nameView.setBackgroundColor(Color.TRANSPARENT);
			holder.nameView.setSelected(false);
		}

		private void selected(View view) {
			if (view == null) {
				return;
			}
			view.bringToFront();
//			view.setScaleX(ZOOM_IN);
//			view.setScaleY(ZOOM_IN);
			MediaItemFocusHelper.selectedAnim(view);
			SearchItemViewHolder holder = (SearchItemViewHolder) view.getTag();
			if (holder == null) {
				return;
			}
			holder.nameView.setBackgroundResource(R.drawable.media_browser_item_selected);
			holder.nameView.setSelected(true);
		}
	};
	private OnClickListener allLookClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SearchItemViewHolder holder = (SearchItemViewHolder) v.getTag();
			Intent intent = new Intent(activity, DetailsActivity.class);
			intent.putExtra(DetailsActivity.CONTENT_ID, holder.id);
			activity.startActivity(intent);
		}
	};

	class SearchItemViewHolder {
		public ImageView imgView;
		public TextView nameView;
		public String id;
	}
}
