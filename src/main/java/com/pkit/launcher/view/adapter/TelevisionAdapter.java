package com.pkit.launcher.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pkit.launcher.R;
import com.pkit.launcher.activity.TelevisionFragment;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.ModelSeat;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.utils.DrawableUtil;

import java.util.List;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class TelevisionAdapter extends RecyclerView.Adapter<TelevisionAdapter.ViewHolder> implements ImageLoadingListener {

    private Context mContext;
    private List<TelevisionFragment.TelevisionEntry> mList;
    private ViewHolder mViewHolder;

    public FocusScaleHandler mFocusHighlight;
    private IContentService mContentService;
    private TelevisionFragment.ContentServiceCallback mContentServiceCallback;

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }

    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    public void addItems(List<TelevisionFragment.TelevisionEntry> list) {
        mList = list;
        notifyDataSetChanged();
    }



    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }

    public void setContentService(IContentService contentService) {
        mContentService = contentService;
    }

    public void setCallback(TelevisionFragment.ContentServiceCallback contentServiceCallback) {
        mContentServiceCallback = contentServiceCallback;
    }

    final class OnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }


            if (hasFocus) {
                view.findViewById(R.id.home_television_focus_view).setVisibility(View.VISIBLE);
                view.findViewById(R.id.home_television_info_layout).setBackgroundResource(R.drawable.home_movie_bg_focus_txt);
                ((HomeRecyclerView) view.getParent()).postInvalidate();
            } else {
                view.findViewById(R.id.home_television_focus_view).setVisibility(View.GONE);
                view.findViewById(R.id.home_television_info_layout).setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void setupFocusListener(View view) {
        if (view != null) {
            view.setOnFocusChangeListener(mFocusChangeListener);
        }
        if (mFocusHighlight != null) {
            mFocusHighlight.onInitializeView(view);
        }
    }

    public TelevisionAdapter(Context context, List<TelevisionFragment.TelevisionEntry> list) {
        mContext = context;
        mList = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.television_item, parent, false);
        mViewHolder = new ViewHolder(view);
        setupFocusListener(mViewHolder.itemView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.itemView.setId(R.id.television_first_item);
        }
        TelevisionFragment.TelevisionEntry entry = mList.get(position);
        ModelSeat modelSeat = entry.modelseat;
        holder.itemView.setTag(modelSeat.getActionValue());
        holder.mTelevisionLayout.setBackgroundResource(entry.background);
        DrawableUtil.displayDrawable(modelSeat.getPoster(),R.drawable.media_browser_item_loading,holder.mTelevisionImage,0,this);
        holder.mTelevisionText.setText(modelSeat.getName());


        if (mContentService != null)
            try {
                mContentService.registCallback(mContentServiceCallback);
                mContentService.loadUpdates(modelSeat.getActionValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mTelevisionLayout;
        public TextView mTelevisionText;
        public ImageView mTelevisionImage;
        public LinearLayout mInfoLayout;
        public TextSwitcher mUpdateCount;
        public TextView mInfo1Text;
        public TextView mInfo2Text;
        public ImageView mFocusView;
        public ViewFlipper mViewFlipper;

        public ViewHolder(View view) {
            super(view);
            mTelevisionLayout = (LinearLayout) view.findViewById(R.id.home_television_layout);
            mTelevisionText = (TextView) view.findViewById(R.id.home_television_text);
            mTelevisionImage = (ImageView) view.findViewById(R.id.home_television_image);
            mInfoLayout = (LinearLayout) view.findViewById(R.id.home_television_info_layout);
            mUpdateCount = (TextSwitcher) view.findViewById(R.id.home_television_update_count);
            mInfo1Text = (TextView) view.findViewById(R.id.home_television_update_info1);
            mInfo2Text = (TextView) view.findViewById(R.id.home_television_update_info2);
            mFocusView = (ImageView) view.findViewById(R.id.home_television_focus_view);
            mViewFlipper = (ViewFlipper) view.findViewById(R.id.television_flipper);

        }
    }
}
