package com.pkit.launcher.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.view.DateView;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.FocusTranslateHelper;
import com.pkit.launcher.view.HomePagedView;

import java.util.ArrayList;

/**
 * Created by lijiaxing on 2015/3/25.
 */
public class MainActivity extends BaseActivity {


    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private DateView mTextClock;
    private int mSelectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedTab = 0;
        initViews();
    }

    private void initViews() {

        setContentView(R.layout.home_layout);
        FocusTranslateHelper.setupFragmentItemFocus(this, FocusScale.ZOOM_FACTOR_LARGE);
        mTextClock = (DateView) findViewById(R.id.text_clock);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "HelveticaNeueLTPro-ThEx.otf");
        mTextClock.setTypeface(tf);
        mTextClock.setFormat("HH:mm");

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        if (mTabsAdapter == null) {
            mViewPager = (ViewPager) findViewById(R.id.view_pager);
            mViewPager.setOffscreenPageLimit(3);
            mTabsAdapter = new TabsAdapter(this, mViewPager, mTabHost);
            createTabs();
            mTabsAdapter.setTextViewFocusChangeListener();
            mTabsAdapter.notifySelectedPage(mSelectedTab);
        }
    }


    private void createTabs() {
        if (mTabsAdapter != null) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec("tab0");
            tabSpec.setIndicator(getTabView("推荐", R.id.tab1));
            mTabsAdapter.addTabs(tabSpec, RecommendFragment.class, 0);

            TabHost.TabSpec tabSpec1 = mTabHost.newTabSpec("tab1");
            tabSpec1.setIndicator(getTabView("影视", R.id.tab2));
            mTabsAdapter.addTabs(tabSpec1, TelevisionFragment.class, 1);

            TabHost.TabSpec tabSpec2 = mTabHost.newTabSpec("tab2");
            tabSpec2.setIndicator(getTabView("我的", R.id.tab3));
            mTabsAdapter.addTabs(tabSpec2, MinFragment.class, 2);

            TabHost.TabSpec tabSpec3 = mTabHost.newTabSpec("tab3");
            tabSpec3.setIndicator(getTabView("设置", R.id.tab4));
            mTabsAdapter.addTabs(tabSpec3, SettingsFragment.class, 3);
        }
    }

    private TextView getTabView(String title, int id) {
        final TextView textView = new TextView(this);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setId(id);
        int focusDownId = 0;
        int focusRight = 0;
        int focusLeft = 0;
        switch (id) {
            case R.id.tab1:
                focusLeft = R.id.tab4;
                focusRight = R.id.tab2;
                break;
            case R.id.tab2:
                focusDownId = R.id.television_first_item;
                focusRight = R.id.tab3;
                break;
            case R.id.tab3:
                focusDownId = R.id.mine_first_item;
                focusRight = R.id.tab4;
                break;
            case R.id.tab4:
                focusDownId = R.id.settings_first_item;
                focusRight = R.id.tab1;
                break;
        }
        if (focusDownId != 0) {
            textView.setNextFocusDownId(focusDownId);
        }
        if (focusRight != 0) {
            textView.setNextFocusRightId(focusRight);
        }
        if (focusLeft != 0) {
            textView.setNextFocusLeftId(focusLeft);
        }
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.tabselector);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        textView.setText(title);
        int width = (int) this.getResources().getDimension(R.dimen.home_tabwedget_view1_width);
        int height = (int) this.getResources().getDimension(R.dimen.home_tabwedget_view1_height);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) this.getResources().getDimension(R.dimen.home_tabwedget_view1_margin);
        lp.setMargins(margin, 0, margin, 0);
        lp.gravity = Gravity.CENTER;
        textView.setLayoutParams(lp);
        textView.setPadding(margin - margin / 4, 0, 0, 0);
        return textView;
    }

    private class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

        private static final String KEY_TAB_POSITION = "tab_position";
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();


        final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, int position) {
                clss = _class;
                args = new Bundle();
                args.putInt(KEY_TAB_POSITION, position);
            }

            public int getPosition() {
                return args.getInt(KEY_TAB_POSITION, 0);
            }
        }

        class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context context;

            public DummyTabFactory(Context context) {
                this.context = context;
            }

            public View createTabContent(String tag) {
                View v = new View(context);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        Context mContext;
        ViewPager mPager;
        public TabHost mTabHost;
        TabWidget mTabwidget;
        public TextView tab1;
        public TextView tab2;
        public TextView tab3;
        public TextView tab4;


        public void notifySelectedPage(int page) {
            notifyPageChanged(page);
        }

        private void notifyPageChanged(int newPage) {
            TabWidget tabWidget = mTabHost.getTabWidget();
            int oldFocus = tabWidget.getDescendantFocusability();
            tabWidget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(newPage);
            mTabwidget = mTabHost.getTabWidget();
            mTabwidget.setDescendantFocusability(oldFocus);
            setAllTabColor();
            switch (newPage) {
                case 0:
                    setTextViewColor(tab1);
                    break;
                case 1:
                    setTextViewColor(tab2);
                    break;
                case 2:
                    setTextViewColor(tab3);
                    break;
                case 3:
                    setTextViewColor(tab4);
                    break;

            }

        }

        final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

        final class OnFocusChangeListener implements View.OnFocusChangeListener {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (mFocusHighlight != null) {
                    mFocusHighlight.onItemFocused(view, hasFocus);
                }

                if (hasFocus) {
                    setAllTabColor();
                    if (view.getId() == R.id.tab1) {
                        mTabHost.setCurrentTab(0);
                    } else if (view.getId() == R.id.tab2) {
                        mTabHost.setCurrentTab(1);
                    } else if (view.getId() == R.id.tab3) {
                        mTabHost.setCurrentTab(2);
                    } else if (view.getId() == R.id.tab4) {
                        mTabHost.setCurrentTab(3);
                    }
                }
                setTextViewColor((TextView) view);
            }
        }

        public void setTextViewFocusChangeListener() {
            tab1 = (TextView) mTabwidget.findViewById(R.id.tab1);
            tab2 = (TextView) mTabwidget.findViewById(R.id.tab2);
            tab3 = (TextView) mTabwidget.findViewById(R.id.tab3);
            tab4 = (TextView) mTabwidget.findViewById(R.id.tab4);
            tab1.setOnFocusChangeListener(mFocusChangeListener);
            if (mFocusHighlight != null) {
                mFocusHighlight.onInitializeView(tab1);
            }
            tab2.setOnFocusChangeListener(mFocusChangeListener);
            if (mFocusHighlight != null) {
                mFocusHighlight.onInitializeView(tab2);
            }
            tab3.setOnFocusChangeListener(mFocusChangeListener);
            if (mFocusHighlight != null) {
                mFocusHighlight.onInitializeView(tab3);
            }
            tab4.setOnFocusChangeListener(mFocusChangeListener);
            if (mFocusHighlight != null) {
                mFocusHighlight.onInitializeView(tab4);
            }
        }

        private void setAllTabColor() {
            tab1.setTextColor(Color.WHITE);
            tab2.setTextColor(Color.WHITE);
            tab3.setTextColor(Color.WHITE);
            tab4.setTextColor(Color.WHITE);
        }

        private void setTextViewColor(TextView textView) {
            if (textView.isSelected()) {
                if (!textView.hasFocus()) {
                    textView.setTextColor(getResources().getColor(R.color.home_tab_unfocus_color));
                }
            }
        }

        public TabsAdapter(Activity activity, ViewPager viewPager, TabHost tabHost) {
            super(activity.getFragmentManager());
            mContext = activity;
            mPager = viewPager;
            mTabHost = tabHost;
            mPager.setAdapter(this);
            mPager.setOnPageChangeListener(this);
            mTabHost.setOnTabChangedListener(this);
            mTabwidget = mTabHost.getTabWidget();
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            if (position == 0 && tab1 != null && tab1.hasFocus()) {
                if (mPager.getChildAt(0) instanceof HomePagedView) {
                    HomePagedView pagedView = (HomePagedView) mPager.getChildAt(0);
                    pagedView.moveToDefaultScreen(true);
                    tab1.requestFocus();
                }
            } else if (position != 0 && position != 3) {
                if (mPager.getChildAt(0) instanceof HomePagedView) {
                    HomePagedView pagedView = (HomePagedView) mPager.getChildAt(0);
                    pagedView.setCurrentPage(2);
                }
            }
            mPager.setCurrentItem(position);
        }

        @Override
        public Fragment getItem(int position) {
            final String name = makeFragmentName(R.id.view_pager, position);
            Fragment fragment = getFragmentManager().findFragmentByTag(name);
            if (fragment == null) {
                TabInfo info = mTabs.get(position);
                fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        public void addTabs(TabHost.TabSpec tab, Class<?> clss, int position) {
            TabInfo info = new TabInfo(clss, position);
            tab.setContent(new DummyTabFactory(mContext));
            mTabHost.addTab(tab);
            mTabs.add(info);
            notifyDataSetChanged();
        }

        @Override
        public void onPageSelected(int position) {
            notifyPageChanged(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Do noting
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //Do noting
        }
    }

    private String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private FocusScaleHandler mFocusHighlight;

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }


}
