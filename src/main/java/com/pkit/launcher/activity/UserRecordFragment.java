package com.pkit.launcher.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.animation.OvershootInRightAnimator;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.IServiceCallback;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.TagGroup;
import com.pkit.launcher.utils.Utils;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.launcher.view.adapter.CollectionAdapter;
import com.pkit.launcher.view.adapter.RecentlyWatchedAdapter;
import com.pkit.launcher.view.adapter.TimeLineAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.pkit.launcher.activity.UserRecordActivity.BUY_RECORD_LAYOUT;
import static com.pkit.launcher.activity.UserRecordActivity.COLLECTION_LAYOUT;
import static com.pkit.launcher.activity.UserRecordActivity.RECENTLY_WATCHED_LAYOUT;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class UserRecordFragment extends Fragment implements ServiceConnection {
    public static final String TAG = "Launcher";
    public static final String LAYOUT_ID = "layout_id";
    private static final int DURATION = 700;
    private static final String DETAIL = "detail";
    private static final int UPDATA_MESSAGE = 3;

    private IContentService mIContentService;

    private HomeRecyclerView mRecylerView;
    private HomeRecyclerView mTimelineView;
    private TimeLineAdapter mTimelineAdapter;
    private RecentlyWatchedAdapter mRecentlyWatchedAdapter;
    private CollectionAdapter mCollectionAdapter;
    private RecyclerView.RecycledViewPool mRecycledViewPool;
    private int mLayoutId;
    private List<Item> mItems;
    private List<String> mDates;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mLayoutId == RECENTLY_WATCHED_LAYOUT) {
                    mDates = generationData();
                    if (mRecentlyWatchedAdapter != null) {
                        mRecentlyWatchedAdapter.setContentService(mIContentService);
                    }
                    for (int i = 0; i < mItems.size(); i++) {
                        mTimelineAdapter.addItem(i, mDates.get(i));
                        mRecentlyWatchedAdapter.addItem(i, mItems.get(i));
                    }
                } else if (mLayoutId == COLLECTION_LAYOUT) {
                    if (mCollectionAdapter != null) {
                        mCollectionAdapter.setContentService(mIContentService);
                        mCollectionAdapter.setCallback(new ContentServiceCallback());
                    }
                    for (int i = 0; i < mItems.size(); i++) {
                        mCollectionAdapter.addItem(i, mItems.get(i));
                    }
                }
                mHandler.sendEmptyMessageDelayed(2, DURATION + 300);
            } else if (msg.what == 2) {
                if (mRecylerView.getChildCount() != 0) {
                    mRecylerView.getChildAt(0).requestFocus();
                }
            } else if (msg.what == UPDATA_MESSAGE) {
                Bundle bundle = msg.getData();
                Detail detail = bundle.getParcelable(DETAIL);
                View view = mRecylerView.findViewWithTag(detail.contentID);
                TextView textView = (TextView) view.findViewById(R.id.video_update);
                setText(textView, detail);

            }
        }
    };

    private void setText(TextView textView, Detail detail) {
        switch (detail.type) {
            case Detail.FILM_TYPE:
                break;
            case Detail.TELE_TV_TYPE:
                textView.setText("更新到"+detail.lastUpdateEpisode+"集");
                break;
            case Detail.VARIETY_TYPE:
                textView.setText("更新到"+detail.lastUpdateEpisode+"期");
                break;
        }
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
        mLayoutId = getArguments().getInt(LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }


    private void bindService() {
        Intent intent = new Intent(getActivity(), ContentService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecylerView = (HomeRecyclerView) view.findViewById(R.id.list);
        LinearLayoutManager glm = new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false);
        mRecylerView.setLayoutManager(glm);
        if (mLayoutId == RECENTLY_WATCHED_LAYOUT) {
            mTimelineView = (HomeRecyclerView) view.findViewById(R.id.time_line);
            LinearLayoutManager gl = new LinearLayoutManager(getActivity(), GridLayoutManager.HORIZONTAL, false);
            mTimelineView.setLayoutManager(gl);
        }
        mRecylerView.setHasFixedSize(true);
        if (mRecycledViewPool == null) {
            mRecycledViewPool = mRecylerView.getRecycledViewPool();
        }
        mRecylerView.setRecycledViewPool(mRecycledViewPool);
        setAdapter(mLayoutId, getActivity());
    }

    private List<String> generationData() {
        List<String> watchTime = new ArrayList<String>();
        for (Item info : mItems) {
            watchTime.add(Utils.getRelativeTimeDisplayString(getActivity(), info.insertTime));
        }
        return watchTime;
    }


    private void setAdapter(int layoutId, Activity activity) {
        switch (layoutId) {
            case RECENTLY_WATCHED_LAYOUT:
                mDates = new ArrayList<String>();
                mTimelineView.setHasFixedSize(true);
                mTimelineAdapter = new TimeLineAdapter(activity, mDates, mTimelineView);
                mTimelineView.setAdapter(mTimelineAdapter);

                mItems = new ArrayList<Item>();
                mRecentlyWatchedAdapter = new RecentlyWatchedAdapter(activity, mTimelineView, mRecylerView, mTimelineAdapter, mItems);
                FocusScaleHelper.setupFragmentItemFocus(mRecentlyWatchedAdapter, FocusScale.ZOOM_FACTOR_MEDIUM);
                mRecylerView.setItemAnimator(new OvershootInRightAnimator());
                mRecylerView.getItemAnimator().setAddDuration(DURATION);
                mRecylerView.setAdapter(mRecentlyWatchedAdapter);
                break;
            case COLLECTION_LAYOUT:
                mItems = new ArrayList<Item>();
                mCollectionAdapter = new CollectionAdapter(activity, mItems, mRecylerView);
                FocusScaleHelper.setupFragmentItemFocus(mCollectionAdapter, FocusScale.ZOOM_FACTOR_MEDIUM);
                mRecylerView.setItemAnimator(new OvershootInRightAnimator());
                mRecylerView.getItemAnimator().setAddDuration(DURATION);
                mRecylerView.setAdapter(mCollectionAdapter);
                break;
            case BUY_RECORD_LAYOUT:
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mIContentService = IContentService.Stub.asInterface(service);
        try {
            if (mLayoutId == RECENTLY_WATCHED_LAYOUT) {
                mItems = mIContentService.getHistories();
            } else if (mLayoutId == COLLECTION_LAYOUT) {
                mItems = mIContentService.getFavorites();
            }

            mHandler.sendEmptyMessageDelayed(1, 300);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public class ContentServiceCallback extends IServiceCallback.Stub {


        @Override
        public void onLoadComplete(String contentID, int pageNumber, int count, List<Content> contents) throws RemoteException {

        }

        @Override
        public void onDetailLoadComplete(Detail detail) throws RemoteException {
            Message msg = new Message();
            msg.what = UPDATA_MESSAGE;
            Bundle bundle = new Bundle();
            bundle.putParcelable(DETAIL, detail);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onLoading(String contentID) throws RemoteException {

        }

        @Override
        public void onFailed(String contentID) throws RemoteException {

        }

        @Override
        public void onDeviceLoginComplete(Bundle bundle) throws RemoteException {

        }

        @Override
        public void onCheckUpgradeComplete(Bundle bundle) throws RemoteException {

        }

        @Override
        public void onTagGroupComplete(List<TagGroup> tagGroups) throws RemoteException {

        }
    }
}
