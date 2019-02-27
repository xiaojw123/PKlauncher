package com.pkit.launcher.view;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.service.aidl.Tag;
import com.pkit.launcher.view.adapter.SearchSelectorTextAdapter;

public class SearchSelectorFragment extends Fragment implements OnFocusChangeListener, OnItemSelectedListener, OnItemClickListener {
	private static final long DELAYMILLIS = 2 * 1000;
	private Activity activity;
	private LinearLayout selectorLayout;
	private SearchSelectorTextAdapter zoneAdapter;
	private SearchSelectorTextAdapter catAdapter;
	private SearchSelectorTextAdapter yearAdapter;
	private Handler handler;
	private int stayColor;
	private float normalSize;
	private float selectedSize;
	private TextView lastClickZone;
	private TextView lastClickCat;
	private TextView lastClickYear;
	private TextView lastSelectedZone;
	private TextView lastSelectedCat;
	private TextView lastSelectedYear;
	private ListView zoneListView;
	private ListView catListView;
	private ListView yearListView;
	private String currentZone;
	private String currentCat;
	private String currentYear;
	private int selectedColor;
	private int normalColor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		selectorLayout = (LinearLayout) inflater.inflate(R.layout.search_selector_layout, container, false);
		init();
		return selectorLayout;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setZoneList(List<Tag> tagList) {
		zoneAdapter.setTextList(tagList);
	}

	public void setCatList(List<Tag> tagList) {
		catAdapter.setTextList(tagList);
	}

	public void setYearList(List<Tag> tagList) {
		yearAdapter.setTextList(tagList);
	}

	public String getCurrentZone() {
		return currentZone;
	}

	public void setCurrentZone(String currentZone) {
		this.currentZone = currentZone;
	}

	public String getCurrentCat() {
		return currentCat;
	}

	public void setCurrentCat(String currentCat) {
		this.currentCat = currentCat;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}

	private void init() {
		stayColor = activity.getResources().getColor(R.color.green_2);
		normalSize = activity.getResources().getDimension(R.dimen.text_size_16_sp);
		selectedSize = activity.getResources().getDimension(R.dimen.text_size_18_sp);
		selectedColor = activity.getResources().getColor(R.color.white_0);
		normalColor = activity.getResources().getColor(R.color.gray_1);

		currentZone = "0";
		currentCat = "0";
		currentYear = "0";

		TextView zoneHeader = getListHeader();
		TextView catHeader = getListHeader();
		TextView yearHeader = getListHeader();

		zoneAdapter = new SearchSelectorTextAdapter(activity);
		catAdapter = new SearchSelectorTextAdapter(activity);
		yearAdapter = new SearchSelectorTextAdapter(activity);

		zoneListView = (ListView) selectorLayout.findViewById(R.id.search_selector_zone_list);
		zoneListView.setNextFocusLeftId(R.id.search_selector_zone_list);
		zoneListView.setNextFocusUpId(R.id.search_selector_zone_list);
		zoneListView.setNextFocusDownId(R.id.search_selector_zone_list);
		zoneListView.setNextFocusRightId(R.id.search_selector_cat_list);
		zoneListView.setOnItemSelectedListener(this);
		zoneListView.setOnItemClickListener(this);
		zoneListView.setOnFocusChangeListener(this);
		zoneListView.setAdapter(zoneAdapter);
		zoneListView.addHeaderView(zoneHeader);
		zoneListView.requestFocus();

		catListView = (ListView) selectorLayout.findViewById(R.id.search_selector_cat_list);
		catListView.setNextFocusLeftId(R.id.search_selector_zone_list);
		catListView.setNextFocusUpId(R.id.search_selector_cat_list);
		catListView.setNextFocusDownId(R.id.search_selector_cat_list);
		catListView.setNextFocusRightId(R.id.search_selector_year_list);
		catListView.setOnItemSelectedListener(this);
		catListView.setOnItemClickListener(this);
		catListView.setOnFocusChangeListener(this);
		catListView.setAdapter(catAdapter);
		catListView.addHeaderView(catHeader);

		yearListView = (ListView) selectorLayout.findViewById(R.id.search_selector_year_list);
		yearListView.setNextFocusLeftId(R.id.search_selector_cat_list);
		yearListView.setNextFocusUpId(R.id.search_selector_year_list);
		yearListView.setNextFocusDownId(R.id.search_selector_year_list);
		yearListView.setOnItemSelectedListener(this);
		yearListView.setOnItemClickListener(this);
		yearListView.setOnFocusChangeListener(this);
		yearListView.setAdapter(yearAdapter);
		yearListView.addHeaderView(yearHeader);

		if (this.handler != null) {
			this.handler.sendEmptyMessage(SearchMessageManager.LOAD_SELECTOR);
		}
	}

	private TextView getListHeader() {
		int height = (int) activity.getResources().getDimension(R.dimen.z_search_selector_item_height);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height);
		TextView textView = new TextView(activity);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
		textView.setTextColor(normalColor);
		textView.setGravity(Gravity.CENTER);
		textView.setLayoutParams(params);
		textView.setSingleLine();
		textView.setEllipsize(TruncateAt.END);
		textView.setText("全部");
		return textView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// APPLog.printInfo("onItemSelected:" + position);
		if (parent == zoneListView) {
			if (lastSelectedZone == null) {
				lastSelectedZone = (TextView) view;
				return;
			}
			unSelected(lastSelectedZone);
			lastSelectedZone = (TextView) view;
			selected(lastSelectedZone);
		} else if (parent == catListView) {
			if (lastSelectedCat == null) {
				lastSelectedCat = (TextView) view;
				return;
			}
			unSelected(lastSelectedCat);
			lastSelectedCat = (TextView) view;
			selected(lastSelectedCat);
		} else if (parent == yearListView) {
			if (lastSelectedYear == null) {
				lastSelectedYear = (TextView) view;
				return;
			}
			unSelected(lastSelectedYear);
			lastSelectedYear = (TextView) view;
			selected(lastSelectedYear);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		ListView listView = (ListView) v;
		if (!hasFocus) {
			TextView lastSelected = (TextView) listView.getSelectedView();
			unSelected(lastSelected);
		}
	}

	private void unSelected(TextView textView) {
		if (textView == null) {
			return;
		}
		if (textView != lastClickZone && textView != lastClickCat && textView != lastClickYear) {
			textView.setTextColor(normalColor);
		}
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
	}

	private void selected(TextView textView) {
		if (textView == null) {
			return;
		}
		if (textView != lastClickZone && textView != lastClickCat && textView != lastClickYear) {
			textView.setTextColor(selectedColor);
		}
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (view == lastClickZone || view == lastClickCat || view == lastClickYear) {
			return;
		}

		Tag tag = (Tag) view.getTag();
		if (parent == zoneListView) {
			if (lastClickZone != null) {
				lastClickZone.setTextColor(normalColor);
			}
			lastClickZone = (TextView) view;
			lastClickZone.setTextColor(stayColor);
			currentZone = (tag == null ? "0" : tag.getCode());
			catListView.requestFocus();
			catListView.setSelection(0);
		}
		if (parent == catListView) {
			if (lastClickCat != null) {
				lastClickCat.setTextColor(normalColor);
			}
			lastClickCat = (TextView) view;
			lastClickCat.setTextColor(stayColor);
			currentCat = (tag == null ? "0" : tag.getCode());
			yearListView.requestFocus();
			yearListView.setSelection(0);
		}
		if (parent == yearListView) {
			if (lastClickYear != null) {
				lastClickYear.setTextColor(normalColor);
			}
			lastClickYear = (TextView) view;
			lastClickYear.setTextColor(stayColor);
			currentYear = (tag == null ? "0" : tag.getCode());
		}
		String selectionArg = currentZone + "," + currentCat + "," + currentYear;
		sendSelector(selectionArg, 16);
	}

	private void sendSelector(String selectionArg, int count) {
		handler.removeMessages(SearchMessageManager.SEARCH);
		Message msg = handler.obtainMessage(SearchMessageManager.SEARCH, 0, 16, selectionArg);
		handler.sendMessageDelayed(msg, DELAYMILLIS);
	}
}
