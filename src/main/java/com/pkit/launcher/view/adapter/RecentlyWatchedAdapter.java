package com.pkit.launcher.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.utils.DrawableUtil;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class RecentlyWatchedAdapter extends RecyclerView.Adapter<RecentlyWatchedAdapter.ViewHolder> {


    private Context mContext;
    private List<Item> mList;
    private HomeRecyclerView mTimelineView;
    private HomeRecyclerView mRecylerView;
    private TimeLineAdapter mTimeLineAdapter;
    private boolean canDelete = false;
    private IContentService mIContentService;
    private FocusScaleHandler mFocusHighlight;
    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    public void setContentService(IContentService contentService) {
        mIContentService = contentService;
    }

    final class OnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }
            if(view.hasFocus()){
                if(canDelete){
                    view.findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                }
                int position = mRecylerView.getChildPosition(view);
                if (position >= 0 && position < mList.size()) {
                    mTimelineView.smoothScrollToPosition(position);
                }
                view.findViewById(R.id.focus_image).setVisibility(View.VISIBLE);
                view.findViewById(R.id.video_name).setBackgroundColor(mContext.getResources().getColor(R.color.recently_watched_text_color));
            }else{
                view.findViewById(R.id.focus_image).setVisibility(View.GONE);
                view.findViewById(R.id.video_name).setBackgroundResource(R.drawable.recently_watched__poster_name_background);
                view.findViewById(R.id.delete_image).setVisibility(View.GONE);
            }
        }
    }

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }


    public RecentlyWatchedAdapter(Context context, HomeRecyclerView timelineView, HomeRecyclerView recylerView, TimeLineAdapter timelineAdapter, List<Item> list){
        mContext = context;
        mTimelineView = timelineView;
        mRecylerView = recylerView;
        mTimeLineAdapter = timelineAdapter;
        mList = list;

    }

    public void addItem(int position, Item info) {
        mList.add(position, info);
        notifyItemInserted(position);
    }

    public void deleteItem(int position) {
        try {
            mIContentService.deleteHistory(mList.get(position).contentID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mList.remove(position);
        notifyItemRemoved(position);
        mTimeLineAdapter.deleteItem(position);
        if (mList.size() != 0) {
            if (position > 0) {
                mRecylerView.getChildAt(position -1).requestFocus();
            }else if (position == 0 ) {
                mRecylerView.getChildAt(position+1).requestFocus();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.recently_watched_item_layout,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        if(holder.itemView != null){
            holder.itemView.setOnFocusChangeListener(mFocusChangeListener);
            holder.itemView.setOnKeyListener(onKeyListener);
        }
        if(mFocusHighlight != null){
            mFocusHighlight.onInitializeView(holder.itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mList.get(position);
        holder.itemView.setTag(item);
        holder.videoName.setText(item.name);
        DrawableUtil.displayDrawable(item.imgPaths.get(0), R.drawable.media_browser_item_loading, holder.videoImage, 0);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView videoName;
        public ImageView videoImage;
        public ViewHolder(View view) {
            super(view);
            videoName = (TextView) view.findViewById(R.id.video_name);
            videoImage = (ImageView) view.findViewById(R.id.recently_watched_image);
        }
    }

   private final View.OnKeyListener onKeyListener =  new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (v.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    if (!canDelete) {
                        v.findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                        canDelete = true;
                    }else{
                        v.findViewById(R.id.delete_image).setVisibility(View.GONE);
                        canDelete = false;
                    }
                    return true;
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (canDelete) {
                        deleteItem(mRecylerView.getChildPosition(v));
                    }else{
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.CONTENT_ID, mList.get(mRecylerView.getChildPosition(v)).contentID);
                        mContext.startActivity(intent);
                    }
                    return false;
                }

            }

            return false;
        }
    };
}
