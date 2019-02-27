package com.pkit.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pkit.launcher.R;
import com.pkit.launcher.animation.Rotate3dAnimation;
import com.pkit.launcher.message.BaseCallBack;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.ModelSeat;
import com.pkit.launcher.view.CellLayout;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HomePagedView;
import com.pkit.utils.DrawableUtil;

/**
 * Created by jiaxing on 2015/4/22.
 */
public class RecommendFragment extends Fragment implements ServiceConnection, View.OnClickListener, ImageLoadingListener {
    private static final String CONTENT_ID = "contentId";
    private static final String CONTENTS = "contents";
    private static final int LOAD_SUCCESS = 1;
    private static final int CONTENT_LOAD_SUCCESS = 2;
    private static final String COUNT = "count";
    private static final String PAGE_NUMBER = "pageNumber";
    private Context mContext;
    private HomePagedView mHomePagedView;
    private ViewGroup mRecentlyWatchedItem;
    private ViewGroup mCollectionItem;
    private ViewGroup mChildLockItem;
    private ViewGroup mRecommend1Item;
    private ViewGroup mRecommend2Item;
    private ViewGroup mRecommend3Item;
    private ViewGroup mTopic1Item;
    private ViewGroup mTopic2Item;
    private ViewGroup mRecommend4Item;
    private ViewGroup mRecommend5Item;
    private ViewGroup mRecommend6Item;
    private ViewGroup mPoster1Item;
    private ViewGroup mPoster2Item;
    private ViewGroup mPoster3Item;
    private IContentService mContentService;

    private List<ModelSeat> mHomeItems = new ArrayList<ModelSeat>();

    private int mOldPage;

    public FocusScaleHandler mFocusHighlight;
    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    private ViewGroup[] mItems = {mRecentlyWatchedItem, mCollectionItem, mChildLockItem,
            mRecommend1Item, mRecommend2Item, mRecommend3Item,
            mTopic1Item, mTopic2Item, mRecommend4Item,
            mRecommend5Item, mRecommend6Item, mPoster1Item,
            mPoster2Item, mPoster3Item};



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_SUCCESS) {
                Bundle bundle = msg.getData();
                List<Content> contents = bundle.getParcelableArrayList(CONTENTS);
                ModelSeat item = new ModelSeat();
                item.setAction(5);
                item.contentID = UserRecordActivity.USER_RECORD_RECENTLY_WATCHED;
                mHomeItems.add(item);
                item = new ModelSeat();
                item.setAction(5);
                item.contentID = UserRecordActivity.USER_RECORD_COLLECTION;
                mHomeItems.add(item);
                item = new ModelSeat();
                item.setAction(5);
                item.contentID = UserRecordActivity.USER_RECORD_BUY_RECORD;
                mHomeItems.add(item);
                for (int i = 0; i < contents.size(); i++) {
                    mHomeItems.add((ModelSeat) contents.get(i));
                }
                for (int i = 0; i < mItems.length; i++) {

                    setItemLayout(i, mItems[i], mHomeItems.get(i));

                }
            } else if (msg.what == CONTENT_LOAD_SUCCESS) {
                Bundle bundle = msg.getData();
                List<Content> contents = bundle.getParcelableArrayList(CONTENTS);
                String contentID = bundle.getString(CONTENT_ID);
                View view = mHomePagedView.findViewWithTag(contentID);
                if (view != null && contents.size() > 2) {
                    TextView textView1 = (TextView) view.findViewById(R.id.recommend_poster_text1);
                    TextView textView2 = (TextView) view.findViewById(R.id.recommend_poster_text2);
                    TextView textView3 = (TextView) view.findViewById(R.id.recommend_poster_text3);
                    Item item = (Item) contents.get(0);
                    Item item1 = (Item) contents.get(1);
                    Item item2 = (Item) contents.get(2);
                    textView1.setText(item.name);
                    textView2.setText(item1.name);
                    textView3.setText(item2.name);
                }
            }

        }
    };

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommend_layout2, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(this);
    }

    private void bindService() {
        Intent intent = new Intent(getActivity(), ContentService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FocusScaleHelper.setupFragmentItemFocus(this, FocusScale.ZOOM_FACTOR_MEDIUM);
        mHomePagedView = (HomePagedView) view.findViewById(R.id.workspace);
        mItems[0] = (ViewGroup) view.findViewById(R.id.user_record);
        mItems[1] = (ViewGroup) view.findViewById(R.id.user_record1);
        mItems[2] = (ViewGroup) view.findViewById(R.id.user_record2);
        mItems[3] = (ViewGroup) view.findViewById(R.id.recommend_view_1);
        mItems[4] = (ViewGroup) view.findViewById(R.id.recommend_view_2);
        mItems[5] = (ViewGroup) view.findViewById(R.id.recommend_view_3);
        mItems[6] = (ViewGroup) view.findViewById(R.id.topic_view_1);
        mItems[7] = (ViewGroup) view.findViewById(R.id.topic_view_2);
        mItems[8] = (ViewGroup) view.findViewById(R.id.recommend_view_4);
        mItems[9] = (ViewGroup) view.findViewById(R.id.recommend_view_5);
        mItems[10] = (ViewGroup) view.findViewById(R.id.recommend_view_6);
        mItems[11] = (ViewGroup) view.findViewById(R.id.poster_view_1);
        mItems[12] = (ViewGroup) view.findViewById(R.id.poster_view_2);
        mItems[13] = (ViewGroup) view.findViewById(R.id.poster_view_3);
        for (int i = 0; i < mItems.length; i++) {
            setupFocusListener(mItems[i]);
            mItems[i].setOnClickListener(this);
            if (i >= 3) {
                mItems[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setUserRecordData(View item, int imageResource, int name) {
        ImageView imageView = (ImageView) item.findViewById(R.id.user_record_iamge);
        imageView.setBackgroundResource(imageResource);
        TextView textView = (TextView) item.findViewById(R.id.user_record_text);
        textView.setText(name);
    }

    private void setRecommendData(View item, ModelSeat homeItem) {
        ImageView imageView = (ImageView) item.findViewById(R.id.recommend_film_image);
        DrawableUtil.displayDrawable(homeItem.getPoster(), R.drawable.media_browser_item_loading, imageView, 0, this);
        TextView qualityText = (TextView) item.findViewById(R.id.recommend_film_quality);
        if (qualityText != null) {
            //TODO::清晰度
            qualityText.setVisibility(View.GONE);
        }
        TextView filmName = (TextView) item.findViewById(R.id.recommend_film_name);
        filmName.setText(homeItem.getName());
    }

    private void setPosterViewData(View view, ModelSeat item) {
        view.setTag(item.getActionValue());
        ImageView imageView0 = (ImageView) view.findViewById(R.id.home_recommend_poster);
        ImageView imageView1 = (ImageView) view.findViewById(R.id.recommend_poster_image1);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.recommend_poster_image2);
        ImageView imageView3 = (ImageView) view.findViewById(R.id.recommend_poster_image3);
        DrawableUtil.displayDrawable(item.getPoster(), R.drawable.media_browser_item_loading, imageView0, 0);
        imageView1.setImageResource(R.drawable.home_recommend_item_number_1_unfocus);
        imageView2.setImageResource(R.drawable.home_recommend_item_number_2_unfocus);
        imageView3.setImageResource(R.drawable.home_recommend_item_number_3_unfocus);
        try {
            mContentService.registCallback(new ContentServiceCallback());
            mContentService.loadItems(item.getActionValue(), 0,3);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setItemLayout(int i, View item, ModelSeat homeItem) {
        switch (i) {
            case 0:
                setUserRecordData(item, R.drawable.home_record_unfocus, R.string.home_recommend_user_record_recently_watched);
                break;
            case 1:
                setUserRecordData(item, R.drawable.home_like_unfocus, R.string.home_recommend_user_record_collection);
                break;
            case 2:
                setUserRecordData(item, R.drawable.home_child_lock_unfocus, R.string.home_recommend_user_record_child_lock);
                break;
            case 3:
                setRecommendData(item, homeItem);
                break;
            case 4:
                setRecommendData(item, homeItem);
                break;
            case 5:
                setRecommendData(item, homeItem);
                break;
            case 6:
                setRecommendData(item, homeItem);
                break;
            case 7:
                setRecommendData(item, homeItem);
                break;
            case 8:
                setRecommendData(item, homeItem);
                break;
            case 9:
                setRecommendData(item, homeItem);
                break;
            case 10:
                setRecommendData(item, homeItem);
                break;
            case 11:
                setPosterViewData(item, homeItem);
                break;
            case 12:
                setPosterViewData(item, homeItem);
                break;
            case 13:
                setPosterViewData(item, homeItem);
                break;
            case 14:
                setRecommendData(item, homeItem);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mContentService = IContentService.Stub.asInterface(service);
        try {
            mContentService.registCallback(new ContentServiceCallback());
            mContentService.loadRecommendContents(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }


    private void setupFocusListener(View view) {
        if (view != null) {
            view.setOnFocusChangeListener(mFocusChangeListener);
        }
        if (mFocusHighlight != null) {
            mFocusHighlight.onInitializeView(view);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        Container container;
        switch (id) {
            case R.id.user_record:
                intent = new Intent();
                intent.setClass(getActivity(), UserRecordActivity.class);
                intent.putExtra(UserRecordActivity.USER_RECORD_TYPE, UserRecordActivity.USER_RECORD_RECENTLY_WATCHED);
                getActivity().startActivity(intent);
                break;
            case R.id.user_record1:
                intent = new Intent();
                intent.setClass(getActivity(), UserRecordActivity.class);
                intent.putExtra(UserRecordActivity.USER_RECORD_TYPE, UserRecordActivity.USER_RECORD_COLLECTION);
                getActivity().startActivity(intent);
                break;
            case R.id.user_record2:
                intent = new Intent();
                intent.setClass(getActivity(), ChildrenLockActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.recommend_view_1:
                startActivity(mHomeItems.get(3));
                break;
            case R.id.recommend_view_2:
                startActivity(mHomeItems.get(4));
                break;
            case R.id.recommend_view_3:
                //TODO:...
                startActivity(mHomeItems.get(5));
                break;
            case R.id.topic_view_1:
                startActivity(mHomeItems.get(6));
                break;
            case R.id.topic_view_2:
                startActivity(mHomeItems.get(7));
                break;
            case R.id.recommend_view_4:
                startActivity(mHomeItems.get(8));
                break;
            case R.id.recommend_view_5:
                //TODO:
                startActivity(mHomeItems.get(9));
                break;
            case R.id.recommend_view_6:
                startActivity(mHomeItems.get(10));
                break;
            case R.id.poster_view_1:
                startActivity(mHomeItems.get(11));
                break;
            case R.id.poster_view_2:
                startActivity(mHomeItems.get(12));
                break;
            case R.id.poster_view_3:
                startActivity(mHomeItems.get(13));
                break;
            case R.id.recommend_view_7:
                break;
        }
    }

    private void startActivity(ModelSeat item) {
        Intent intent = new Intent();
        switch (item.getAction()) {
            case Item.TYPE_1:
                intent.setClass(getActivity(), MediaBrowserActivity.class);
                intent.putExtra(MediaBrowserActivity.CONTAINER_PARAM, new Container(item));
                break;
            case Item.TYPE_2:
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.CONTENT_ID, item.getActionValue());
                break;
            case Item.TYPE_3:
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra(WebActivity.WEB_URL, item.getActionValue());
                break;
            case Item.TYPE_4:

                break;
            case Item.TYPE_5:
                intent.setClass(getActivity(), UserRecordActivity.class);
                intent.putExtra(UserRecordActivity.USER_RECORD_TYPE, item.getActionValue());
                break;

        }

        getActivity().startActivity(intent);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        View view1 = (View) view.getParent();
        if (view1 == mItems[3]) {
            rotateAnimation(view1);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }

    public void rotateAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        float centerX = view.getWidth() / 2.0f;
        float centerY = view.getHeight() / 2.0f;
        Rotate3dAnimation rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 12.5f, false);
        rotation.setDuration(150);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setFocusable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setFocusable(true);
                if (view == mItems[3]) {
                    rotateAnimation(mItems[4]);
                    rotateAnimation(mItems[5]);
                } else if (view == mItems[5]) {
                    rotateAnimation(mItems[6]);
                    rotateAnimation(mItems[7]);
                } else if (view == mItems[7]) {
                    rotateAnimation(mItems[8]);
                    mItems[3].requestFocus();
                } else if (view == mItems[8]) {
                    rotateAnimation(mItems[9]);
                    rotateAnimation(mItems[10]);
                } else if (view == mItems[10]) {
                    rotateAnimation(mItems[11]);
                } else if (view == mItems[11]) {
                    rotateAnimation(mItems[12]);
                } else if (view == mItems[12]) {
                    rotateAnimation(mItems[13]);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(rotation);
    }

    final class OnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }

            View focusView = view.findViewById(R.id.recommend_focus_view);

            if (hasFocus) {
                if (focusView != null) {
                    focusView.setBackgroundResource(R.drawable.home_poster_focus);
                }
            } else {
                if (focusView != null) {
                    focusView.setBackgroundResource(R.drawable.home_poster_unfocus);
                }
            }

            ((CellLayout) view.getParent()).postInvalidate();
            HomePagedView homePagedView = (HomePagedView) view.getParent().getParent();
            if (homePagedView.getCurrentPage() != mOldPage) {
                homePagedView.postInvalidate();
                mOldPage = homePagedView.getCurrentPage();
            }
            onDifferentItemFocus(view, hasFocus);
            ((ViewGroup) homePagedView.getParent()).postInvalidate();
        }

        private void onDifferentItemFocus(View view, boolean hasFocus) {
            int id = view.getId();
            if (id == 0 && view == null && mHomeItems != null) {
                return;
            }
            switch (id) {
                case R.id.user_record:
                    setUserRecordViewFocusData(view, hasFocus, R.drawable.home_record_focus, R.drawable.home_record_unfocus);
                    break;
                case R.id.user_record1:
                    setUserRecordViewFocusData(view, hasFocus, R.drawable.home_like_focus, R.drawable.home_like_unfocus);
                    break;
                case R.id.user_record2:
                    setUserRecordViewFocusData(view, hasFocus, R.drawable.home_child_lock_focus, R.drawable.home_child_lock_unfocus);
                    break;
                case R.id.recommend_view_1:
                    setRecommendViewFocusData(view, R.id.recommend_view_1, hasFocus);
                    break;
                case R.id.recommend_view_2:
                    setRecommendViewFocusData(view, R.id.recommend_view_2, hasFocus);
                    break;
                case R.id.recommend_view_3:
                    setRecommendViewFocusData(view, R.id.recommend_view_3, hasFocus);
                    break;
                case R.id.topic_view_1:
                    setRecommendViewFocusData(view, R.id.topic_view_1, hasFocus);
                    break;
                case R.id.topic_view_2:
                    setRecommendViewFocusData(view, R.id.topic_view_2, hasFocus);
                    break;
                case R.id.recommend_view_4:
                    setRecommendViewFocusData(view, R.id.recommend_view_4, hasFocus);
                    break;
                case R.id.recommend_view_5:
                    setRecommendViewFocusData(view, R.id.recommend_view_5, hasFocus);
                    break;
                case R.id.recommend_view_6:
                    setRecommendViewFocusData(view, R.id.recommend_view_6, hasFocus);
                    break;
                case R.id.poster_view_1:
                    setPosterViewFocusData(view, hasFocus);
                    break;
                case R.id.poster_view_2:
                    setPosterViewFocusData(view, hasFocus);
                    break;
                case R.id.poster_view_3:
                    setPosterViewFocusData(view, hasFocus);
                    break;
                case R.id.recommend_view_7:
                    setRecommendViewFocusData(view, R.id.recommend_view_7, hasFocus);
                    break;
            }
        }

        private void setUserRecordViewFocusData(View view, boolean hasFocus, int focusImage, int unfocusImage) {
            TextView textView = (TextView) view.findViewById(R.id.user_record_text);
            ImageView imageView = (ImageView) view.findViewById(R.id.user_record_iamge);
            if (imageView != null) {
                if (hasFocus) {
                    textView.setTextColor(Color.WHITE);
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_film_shade_focus_color));
                    imageView.setBackgroundResource(focusImage);
                } else {
                    textView.setTextColor(mContext.getResources().getColor(R.color.recommend_film_name_color));
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_user_record_background_color));
                    imageView.setBackgroundResource(unfocusImage);
                }
            }
        }

        private void setRecommendViewFocusData(View view, int id, boolean hasFocus) {
            TextView filmNameText = (TextView) view.findViewById(R.id.recommend_film_name);
            if (filmNameText != null) {
                if (hasFocus) {
                    if (id != R.id.recommend_view_3 && id != R.id.recommend_view_6) {
                        filmNameText.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_film_shade_focus_color));
                    }
                    filmNameText.setTextColor(mContext.getResources().getColor(R.color.recommend_film_name_focus_color));
                    filmNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                } else {
                    if (id != R.id.recommend_view_3 && id != R.id.recommend_view_6) {
                        filmNameText.setBackground(mContext.getResources().getDrawable(R.drawable.home_recommend1_textview_bg));
                    }
                    filmNameText.setTextColor(mContext.getResources().getColor(R.color.recommend_film_name_color));
                    filmNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }
        }

        private void setPosterViewFocusData(View view, boolean hasFocus) {
            LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.recommend_poster_layout1);
            LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.recommend_poster_layout2);
            LinearLayout ll3 = (LinearLayout) view.findViewById(R.id.recommend_poster_layout3);
            ImageView imageView1 = (ImageView) view.findViewById(R.id.recommend_poster_image1);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.recommend_poster_image2);
            ImageView imageView3 = (ImageView) view.findViewById(R.id.recommend_poster_image3);
            if (hasFocus) {
                ll1.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text1_background_focus_color));
                ll2.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text2_background_focus_color));
                ll3.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text3_background_focus_color));
                imageView1.setImageResource(R.drawable.home_recommend_item_number_1_focus);
                imageView2.setImageResource(R.drawable.home_recommend_item_number_2_focus);
                imageView3.setImageResource(R.drawable.home_recommend_item_number_3_focus);
            } else {
                ll1.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text1_background_color));
                ll2.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text2_background_color));
                ll3.setBackgroundColor(mContext.getResources().getColor(R.color.recommend_poster_text3_background_color));
                imageView1.setImageResource(R.drawable.home_recommend_item_number_1_unfocus);
                imageView2.setImageResource(R.drawable.home_recommend_item_number_2_unfocus);
                imageView3.setImageResource(R.drawable.home_recommend_item_number_3_unfocus);
            }

        }
    }

    class ContentServiceCallback extends BaseCallBack {

        @Override
        public void onLoadComplete(String contentID, int pageNumber, int count, List<Content> contents) throws RemoteException {
            if ("0".equals(contentID)) {
                if (count != 0) {
                    Message msg = new Message();
                    msg.what = LOAD_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString(CONTENT_ID, contentID);
                    bundle.putInt(PAGE_NUMBER, pageNumber);
                    bundle.putInt(COUNT, count);
                    bundle.putParcelableArrayList(CONTENTS, (ArrayList<Content>) contents);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            } else {
                Message msg = new Message();
                msg.what = CONTENT_LOAD_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putString(CONTENT_ID, contentID);
                bundle.putParcelableArrayList(CONTENTS, (ArrayList<Content>) contents);
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }

        }
    }

}
