package com.pkit.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.pkit.launcher.R;
import com.pkit.launcher.message.BaseCallBack;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.ModelSeat;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.launcher.view.OnItemViewClickedListener;
import com.pkit.launcher.view.adapter.TelevisionAdapter;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class TelevisionFragment extends Fragment implements ViewSwitcher.ViewFactory, OnItemViewClickedListener, ServiceConnection {

    public static final int LOAD_SUCCESS = 1;
    private static final int UPDATE_MESSAGE_LOAD_SUCCESS = 2;
    private static final int LOOP_MESSAGE = 3;
    public static final String CONTENTS = "contents";
    private static final String CONTENT_ID = "contentID";
    private static final String LOOP_NUMBER = "loop_number";

    private HomeRecyclerView mRecyclerView;
    private List<TelevisionEntry> mList;
    private TelevisionAdapter mAdapter;
    private IContentService mContentService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_SUCCESS) {
                Bundle bundle = msg.getData();
                List<Content> contents = bundle.getParcelableArrayList(CONTENTS);
                for (int i = 0; i < contents.size(); i++) {
                    TelevisionEntry entry = new TelevisionEntry();
                    ModelSeat modelSeat = (ModelSeat) contents.get(i);
                    entry.background = backgrounds[i];
                    entry.modelseat = modelSeat;
                    mList.add(entry);
                }
                mAdapter.addItems(mList);
                mAdapter.setContentService(mContentService);
                mAdapter.setCallback(new ContentServiceCallback());
            } else if (msg.what == UPDATE_MESSAGE_LOAD_SUCCESS) {
                Bundle bundle = msg.getData();
                List<Item> items = bundle.getParcelableArrayList(CONTENTS);
                View view = mRecyclerView.findViewWithTag(bundle.getString(CONTENT_ID));
                if (view != null) {
                    ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.television_flipper);
                    TextSwitcher switcher = (TextSwitcher) view.findViewById(R.id.home_television_update_count);
                    switcher.setFactory(TelevisionFragment.this);
                    switcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_up_in));
                    switcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_up_out));
                    TextView updateMessage1 = (TextView) view.findViewById(R.id.home_television_update_info1);
                    TextView updateMessage2 = (TextView) view.findViewById(R.id.home_television_update_info2);
                    int loop_number = 0;
                    TextView textview = (TextView) view.findViewById(R.id.home_television_text);
                    CharSequence text = textview.getText();
                    updateMessage1.setText(items.get(loop_number).name);
                    setUpdateText(text, loop_number, updateMessage2, items);
                    viewFlipper.startFlipping();
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_up_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                            R.anim.push_up_out));
                    setText(switcher, items.size());
                    Message message = new Message();
                    message.what = LOOP_MESSAGE;
                    bundle.putInt(LOOP_NUMBER, loop_number);
                    message.setData(bundle);
                    sendMessageDelayed(message, 3000);
                }
            }else if (msg.what == LOOP_MESSAGE) {
                Bundle bundle = msg.getData();
                List<Item> items = bundle.getParcelableArrayList(CONTENTS);
                int loop_number = bundle.getInt(LOOP_NUMBER);
                Log.e("tag",bundle.getString(CONTENT_ID));
                View view = mRecyclerView.findViewWithTag(bundle.getString(CONTENT_ID));
                TextView textview = (TextView) view.findViewById(R.id.home_television_text);
                if (view != null) {
                    TextSwitcher switcher = (TextSwitcher) view.findViewById(R.id.home_television_update_count);
                    TextView updateMessage1 = (TextView) view.findViewById(R.id.home_television_update_info1);
                    TextView updateMessage2 = (TextView) view.findViewById(R.id.home_television_update_info2);
                    updateMessage1.setText(items.get(loop_number).name);
                    CharSequence text = textview.getText();
                    setUpdateText(text, loop_number, updateMessage2, items);
                    if (loop_number < items.size()-1) {
                        loop_number++;
                    }else{
                        loop_number = 0;
                    }

                    setText(switcher, items.size());
                    Message message = new Message();
                    message.what = LOOP_MESSAGE;
                    bundle.putInt(LOOP_NUMBER, loop_number);
                    message.setData(bundle);
                    sendMessageDelayed(message, 5000);
                }
            }
        }
    };

    private void setUpdateText(CharSequence text,int loop_number,TextView updateMessage2,List<Item> items){
        if(!TextUtils.isEmpty(text)){
            CharSequence updateStr = "";
            if(text.equals("电影")) {
                updateStr = "上线";
            }else if(text.equals("电视剧")) {
                updateStr = "更新到" + items.get(loop_number).lastUpdateEpisode + "集";
            }else if(text.equals("综艺")){
                updateStr = "更新到第" + items.get(loop_number).lastUpdateEpisode + "期";
            }else if(text.equals("动漫")){
                updateStr = "更新到" + items.get(loop_number).lastUpdateEpisode + "集";
            }
            updateMessage2.setText(updateStr);
        }
    }

    private void setText(TextSwitcher ts, int count) {
        ts.setText(count > 10 ? count + "" : "0" + count);
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), MediaBrowserActivity.class);
        intent.putExtra(MediaBrowserActivity.CONTAINER_PARAM, new Container(mList.get(position).modelseat));
        startActivity(intent);
    }

    @Override
    public View makeView() {
        TextView textview = new TextView(getActivity());
        textview.setTextColor(Color.WHITE);
        textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.text_size_44_sp));
        return textview;
    }


    public static class TelevisionEntry {
        public int background;
        public ModelSeat modelseat;
    }

    int[] backgrounds = {
            R.drawable.home_movie_bg01,
            R.drawable.home_movie_bg02,
            R.drawable.home_movie_bg03,
            R.drawable.home_movie_bg04
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.television_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();

        mRecyclerView = (HomeRecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setOnItemViewClickedListener(this);
        GridLayoutManager glm = new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.setHasFixedSize(true);
        setAdapter(activity);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mContentService = IContentService.Stub.asInterface(service);
        try {
            mContentService.registCallback(new ContentServiceCallback());
            mContentService.loadRecommendContents(1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onResume() {
        super.onResume();
        bindService();
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

    private void setAdapter(Activity activity) {
        mList = new ArrayList<TelevisionEntry>();
        mAdapter = new TelevisionAdapter(activity, mList);
        FocusScaleHelper.setupFragmentItemFocus(mAdapter, FocusScale.ZOOM_FACTOR_MEDIUM);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class ContentServiceCallback extends BaseCallBack {

        @Override
        public void onLoadComplete(String contentID, int pageNumber, int count, List<Content> contents) throws RemoteException {
            if (contents.size() != 0 && "0".equals(contentID)) {
                Message msg = new Message();
                msg.what = LOAD_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(CONTENTS, (ArrayList<Content>) contents);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = UPDATE_MESSAGE_LOAD_SUCCESS;
                Bundle bundle = new Bundle();
                if(contents.size()!=0) {
                    bundle.putParcelableArrayList(CONTENTS, (ArrayList<Content>) contents);
                    bundle.putString(CONTENT_ID, contentID);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        }
    }

}
