package com.pkit.launcher.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.PlayerActivity;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.utils.APPLog;

public class SelectionsPop extends PopupWindow {
	public static final String TITLE_ID_PREFIX = "100";
	public static final String SET_ID_PREFIX = "101";
	private Context context;
	private Resources res;
	private List<Source> sources;
	private ArrayList<Content> contentlist;
	private Detail detail;
	private LayoutInflater layoutInflater;
	public RelativeLayout selections_window;
	private ViewPager selections_sets_viewPager;
	private ViewPager selections_titles_viewpager;
	private RelativeLayout preview_container;
	private ImageView preview_img;
	private float titleTextSzie, setTextSize;
	// 电视剧剧集数
	private int seriesSize;
	private int titlePagersNum, setPagersNum;
	private int titleWidth;
	private int setsNum;
	private int titlesNum;
	private int setWidth;
	private int lineWidth;
	private int lineHeight;
	private int pagerLeft;
	private int previewWidth;
	private List<ViewGroup> setsPagersList;
	private List<ViewGroup> titlePagersList;
	private int lastSetId = 1101;
	private boolean isVkey;
	private int setFactor;
	private int titleFactor;
	private boolean isSeries = true;
	private int vpWidth;

	public SelectionsPop(Context context, List<Source> sources, Detail detail, ArrayList<Content> contentlist) {
		super(context);
		this.context = context;
		this.sources = sources;
		this.contentlist = contentlist;
		this.detail = detail;
		initParams();
		initContentView();
	}

	private void initParams() {
		seriesSize = sources.size();
		seriesSize=57;
		layoutInflater = LayoutInflater.from(context);
		res = context.getResources();
		setsPagersList = new ArrayList<ViewGroup>();
		titlePagersList = new ArrayList<ViewGroup>();
		titleTextSzie = res.getDimension(R.dimen.text_size_22_sp);
		setTextSize = res.getDimension(R.dimen.text_size_22_sp);
		vpWidth = (int) res.getDimension(R.dimen.details_selections_viewpager_width);
		previewWidth = (int) res.getDimension(R.dimen.details_selections_preview_bg_width);
		lineWidth = (int) res.getDimension(R.dimen.details_selecionts_divsion_line_width);
		lineHeight = (int) res.getDimension(R.dimen.details_selecionts_divsion_line_height);
		setWidth = (int) res.getDimension(R.dimen.details_selections_set_width);
		titleWidth = 2 * setWidth + lineWidth;
		if (!isSeries) {
			setFactor = 5;
			titleFactor = 5;
			setsNum = setFactor;
			titlesNum = titleFactor;
			setWidth = titleWidth;
		} else {
			setFactor = 10;
			titleFactor = 5;
			setsNum = setFactor;
			titlesNum = titleFactor;
		}
		titlePagersNum = seriesSize % (setFactor * titleFactor) == 0 ? seriesSize / (setFactor * titleFactor) : seriesSize / (setFactor * titleFactor) + 1;
		setPagersNum = seriesSize % setFactor == 0 ? seriesSize / setFactor : seriesSize / setFactor + 1;
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager manager = ((DetailsActivity) context).getWindowManager();
		manager.getDefaultDisplay().getMetrics(metric);
		pagerLeft = (metric.widthPixels - vpWidth) / 2;
	}

	@SuppressLint("InflateParams")
	private void initContentView() {
		selections_window = (RelativeLayout) layoutInflater.inflate(R.layout.details_selections_layout, null);
		preview_container = (RelativeLayout) selections_window.findViewById(R.id.details_selections_preview_bg);
		preview_img = (ImageView) selections_window.findViewById(R.id.details_selections_preview_img);
		selections_sets_viewPager = (ViewPager) selections_window.findViewById(R.id.details_selections_sets_viewpager);
		selections_titles_viewpager = (ViewPager) selections_window.findViewById(R.id.details_selections_titles_viewpager);
		if (sources != null && sources.size() > 0) {
			initSetsViewPager();
			initTitleViewPager();
		}
		setContentView(selections_window);
		setBackgroundDrawable(res.getDrawable(android.R.color.transparent));
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
	}

	private void initSetsViewPager() {
		for (int i = 1; i <= setPagersNum; i++) {
			setsNum = ((i == setPagersNum) && (seriesSize % setFactor != 0)) ? seriesSize % setFactor : setsNum;
			RelativeLayout selections_sets_container = getSetsContainer(i);
			LinearLayout setsPagerItem = new LinearLayout(context);
			setsPagerItem.addView(selections_sets_container);
			setsPagersList.add(setsPagerItem);
		}
		SelectionsPagerAdapter adapter = new SelectionsPagerAdapter();
		adapter.setItems(setsPagersList);
		selections_sets_viewPager.setAdapter(adapter);
		selections_sets_viewPager.setOffscreenPageLimit(setPagersNum);
	}

	private RelativeLayout getSetsContainer(int i) {
		RelativeLayout container = new RelativeLayout(context);
		LinearLayout.LayoutParams itemParams = getPagerItemParams(setsNum, setWidth);
		container.setLayoutParams(itemParams);
		container.setBackgroundResource(R.drawable.details_selections_set_vp_bg);
		for (int j = 1; j <= setsNum; j++) {
			addSetsItem(container, i, j);
			if (j != setsNum) {
				addDvisionLine(container, setWidth, j);
			}
		}
		return container;
	}

	private void initTitleViewPager() {
		for (int i = 1; i <= titlePagersNum; i++) {
			titlesNum = (i == titlePagersNum && seriesSize % (setFactor * titleFactor) != 0) ? (seriesSize % (setFactor * titleFactor) - 1) / setFactor + 1
					: titlesNum;
			RelativeLayout selections_titles_container = getTitlesContainer(i);
			LinearLayout titlePagerItem = new LinearLayout(context);
			titlePagerItem.addView(selections_titles_container);
			titlePagersList.add(titlePagerItem);
		}
		SelectionsPagerAdapter adapter = new SelectionsPagerAdapter();
		adapter.setItems(titlePagersList);
		selections_titles_viewpager.setAdapter(adapter);
	}

	private RelativeLayout getTitlesContainer(int i) {
		RelativeLayout container = new RelativeLayout(context);
		LinearLayout.LayoutParams itemPramas = getPagerItemParams(titlesNum, titleWidth);
		container.setLayoutParams(itemPramas);
		container.setBackgroundResource(R.drawable.details_selections_title_vp_bg);
		for (int j = 1; j <= titlesNum; j++) {
			addTitlesItem(container, i, j);
			if (j != titlesNum) {
				addDvisionLine(container, titleWidth, j);
			}
		}
		return container;
	}

	private void addSetsItem(ViewGroup container, int i, int j) {
		TextView single_episode_tv = new TextView(context);
		RelativeLayout.LayoutParams single_set_params = getItemParams(setWidth, j);
		single_episode_tv.setLayoutParams(single_set_params);
		single_episode_tv.setGravity(Gravity.CENTER);
		single_episode_tv.setFocusable(true);
		single_episode_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, setTextSize);
		single_episode_tv.setTextColor(res.getColorStateList(R.drawable.details_selections_sets_textcolor));
		if (isSeries) {
			single_episode_tv.setBackground(res.getDrawable(R.drawable.details_selections_series_sets_bg));
		} else {
			single_episode_tv.setBackground(res.getDrawable(R.drawable.details_selections_variety_sets_bg));
		}
		int set_position = (i - 1) * setFactor + j;
		int id = Integer.parseInt(SET_ID_PREFIX + set_position);
		int nextDownId = Integer.parseInt(TITLE_ID_PREFIX + i);
		String text = String.valueOf(set_position);
		single_episode_tv.setId(id);
		single_episode_tv.setTag(R.id.set_pager_position, i);
		single_episode_tv.setTag(R.id.set_position, set_position);
		single_episode_tv.setNextFocusDownId(nextDownId);
		if (i == setPagersNum && j == setsNum) {
			single_episode_tv.setNextFocusRightId(single_episode_tv.getId());
		}
		single_episode_tv.setText(text);
		single_episode_tv.setOnFocusChangeListener(setFocusListener);
		single_episode_tv.setOnClickListener(setClickListener);
		container.addView(single_episode_tv);
	}

	private void addTitlesItem(ViewGroup container, int i, int j) {
		TextView title_tv = new TextView(context);
		RelativeLayout.LayoutParams titleParams = getItemParams(titleWidth, j);
		title_tv.setLayoutParams(titleParams);
		title_tv.setFocusable(true);
		title_tv.setGravity(Gravity.CENTER);
		title_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSzie);
		title_tv.setTextColor(res.getColorStateList(R.drawable.details_selectinos_title_textcolor));
		title_tv.setBackground(res.getDrawable(R.drawable.details_selections_title_bg));
		int title_position = (i - 1) * titleFactor + j;
		int id = Integer.parseInt(TITLE_ID_PREFIX + title_position);
		int nextUpId = Integer.parseInt(SET_ID_PREFIX + ((title_position - 1) * setFactor + 1));
		title_tv.setId(id);
		title_tv.setTag(R.id.title_position, title_position);
		title_tv.setTag(R.id.title_pager_position, i);
		title_tv.setNextFocusUpId(nextUpId);
		if (i == 1 && j == 1) {
			title_tv.setNextFocusLeftId(title_tv.getId());
		}
		if (i == titlePagersNum && j == titlesNum) {
			title_tv.setNextFocusRightId(title_tv.getId());
		}
		int from = (setFactor * titleFactor) * (i - 1) + ((j - 1) * setFactor + 1);
		int to = (i == titlePagersNum && j == titlesNum) ? seriesSize : from + (setFactor - 1);
		String text = from + "-" + to;
		title_tv.setText(text);
		title_tv.setOnFocusChangeListener(titleFocusListener);
		title_tv.setOnKeyListener(titleKeyListener);
		container.addView(title_tv);
	}

	private void addDvisionLine(ViewGroup container, int itemWidth, int position) {
		RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(lineWidth, lineHeight);
		parmas.leftMargin = position * itemWidth + (position - 1) * lineWidth;
		parmas.addRule(RelativeLayout.CENTER_VERTICAL);
		TextView line = new TextView(context);
		line.setLayoutParams(parmas);
		line.setBackgroundResource(R.color.color_232323);
		container.addView(line);
	}

	private LinearLayout.LayoutParams getPagerItemParams(int itemNum, int itemWidth) {
		int width = itemNum * itemWidth + (itemNum - 1) * lineWidth;
		int height = LinearLayout.LayoutParams.MATCH_PARENT;
		LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width, height);
		return itemParams;
	}

	private RelativeLayout.LayoutParams getItemParams(int itemWidth, int position) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.leftMargin = (itemWidth + lineWidth) * (position - 1);
		return params;
	}

	private OnKeyListener titleKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				isVkey = false;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				isVkey = true;
			}
			return false;
		}
	};

	private OnFocusChangeListener titleFocusListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			int tag = (Integer) v.getTag(R.id.title_position);
			lastSetId = isVkey == true ? lastSetId : Integer.parseInt(SET_ID_PREFIX + ((tag - 1) * setFactor + 1));
			v.setNextFocusUpId(lastSetId);
			if (hasFocus) {
				APPLog.printDebug("titleFocusListener=======hasFocus");
				if (!isVkey) {
					selections_sets_viewPager.setCurrentItem(tag - 1);
				}
				v.setSelected(false);
			} else {
				APPLog.printDebug("titleFocusListener=======noFocus");
				selections_window.invalidate();
			}
		}

	};
	private OnFocusChangeListener setFocusListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			int position = (Integer) v.getTag(R.id.set_pager_position);
			lastSetId = v.getId();
			int offset = v.getLeft() + setWidth / 2 - previewWidth / 2;
			int left = pagerLeft + offset;
			if (hasFocus) {
				setSelectedTile(position);
				showPreviewView(left);
			} else {
				hidePreviewView();
				selections_window.invalidate();
			}
		}

		private void showPreviewView(int left) {
			if (preview_container.getVisibility() != View.VISIBLE) {
				preview_container.setVisibility(View.VISIBLE);
			}
			RelativeLayout.LayoutParams parmas = (android.widget.RelativeLayout.LayoutParams) preview_container.getLayoutParams();
			parmas.leftMargin = left;
			preview_container.setLayoutParams(parmas);
		}

		private void hidePreviewView() {
			if (preview_container.getVisibility() == View.VISIBLE) {
				preview_container.setVisibility(View.INVISIBLE);
			}
		}

		private void setSelectedTile(int position) {
			if (titlePagersList != null && titlePagersList.size() > 0) {
				for (ViewGroup item : titlePagersList) {
					for (int i = 0; i < item.getChildCount(); i++) {
						ViewGroup container = (ViewGroup) item.getChildAt(i);
						for (int j = 0; j < container.getChildCount(); j++) {
							if (j % 2 == 0) {
								View title = container.getChildAt(j);
								int title_pos = Integer.parseInt(title.getTag(R.id.title_position).toString());
								if (title_pos == position) {
									int tag_pager_pos = Integer.parseInt(title.getTag(R.id.title_pager_position).toString());
									selections_titles_viewpager.setCurrentItem(tag_pager_pos - 1);
									title.setSelected(true);
								} else {
									title.setSelected(false);
								}
							}

						}

					}
				}
			}

		}

	};
	private OnClickListener setClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 选集播放
			if (detail == null) {
				return;
			}
			int position = (Integer) v.getTag(R.id.set_position);
			Intent intent = new Intent((DetailsActivity) context, PlayerActivity.class);
			intent.putExtra(PlayerActivity.MEDIA_DETAILS, detail);
			intent.putExtra(PlayerActivity.MEDIA_SEEK_POSITION, 0);
			intent.putExtra(PlayerActivity.MEDIA_POSITION, position - 1);
			intent.putParcelableArrayListExtra(PlayerActivity.MEDIA_RECOMMENDS, contentlist);
			((DetailsActivity) (context)).startActivityForResult(intent, DetailsActivity.UPDATE_DATA);

		}
	};

	class SelectionsPagerAdapter extends PagerAdapter {
		private List<ViewGroup> items;

		public void setItems(List<ViewGroup> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(items.get(position));
			return items.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(items.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
