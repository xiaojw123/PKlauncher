package com.pkit.launcher.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.UserRecordFragment;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.utils.DrawableUtil;

import java.util.List;

/**
 * Created by jiaxing on 2015/3/25.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {


    private Context mContext;
    private List<Item> mList;
    private boolean canDelete = false;
    private HomeRecyclerView mRecyclerView;
    private IContentService mIContentService;
    public FocusScaleHandler mFocusHighlight;
    private UserRecordFragment.ContentServiceCallback mContentServiceCallback;

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }

    public CollectionAdapter(Context context, List<Item> list, HomeRecyclerView recylerView) {
        mContext = context;
        mList = list;
        mRecyclerView = recylerView;
    }

    public void addItem(int position, Item info) {
        mList.add(position, info);
        notifyItemInserted(position);
    }

    public void deleteItem(int position) {
        try {
            mIContentService.deleteFavorite(mList.get(position).contentID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mList.remove(position);
        notifyItemRemoved(position);
        if (mList.size() != 0) {
            if (position > 0) {
                mRecyclerView.getChildAt(position - 1).requestFocus();
            }else if (position == 0 ) {
                mRecyclerView.getChildAt(position + 1).requestFocus();
            }
        }
    }

    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    public void setContentService(IContentService contentService) {
        mIContentService = contentService;
    }

    public void setCallback(UserRecordFragment.ContentServiceCallback callback) {
        mContentServiceCallback = callback;
    }

    final class OnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }

            if (view.hasFocus()) {
                if(canDelete){
                    view.findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                }
                view.findViewById(R.id.focus_image).setVisibility(View.VISIBLE);
                view.findViewById(R.id.video_text_layout).setBackgroundColor(mContext.getResources().getColor(R.color.recently_watched_text_color));
            } else {
                view.findViewById(R.id.focus_image).setVisibility(View.GONE);
                view.findViewById(R.id.video_text_layout).setBackgroundResource(R.drawable.recently_watched__poster_name_background);
                view.findViewById(R.id.delete_image).setVisibility(View.GONE);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.collection_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        setupFocusListener(holder.itemView);


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mList.get(position);
        holder.itemView.setOnKeyListener(onKeyListener);
        holder.itemView.setTag(item.contentID);
        holder.mVideoName.setText(item.name);
        DrawableUtil.displayDrawable(item.imgPaths.get(0), R.drawable.media_browser_item_loading, holder.mCollectionImage, 0);
        if (mIContentService != null) {
            try {
                mIContentService.registCallback(mContentServiceCallback);
                mIContentService.loadDetail(item.contentID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mVideoName, mUpdate;
        private ImageView mCollectionImage;

        public ViewHolder(View view) {
            super(view);
            mVideoName = (TextView) view.findViewById(R.id.video_name);
            mUpdate = (TextView) view.findViewById(R.id.video_update);
            mCollectionImage = (ImageView) view.findViewById(R.id.collection_image);
        }
    }

    private final View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (v.hasFocus() && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    if (!canDelete) {
                        v.findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                        canDelete = true;
                    } else {
                        v.findViewById(R.id.delete_image).setVisibility(View.GONE);
                        canDelete = false;
                    }
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (canDelete) {
                        deleteItem(mRecyclerView.getChildPosition(v));
                    } else {
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.CONTENT_ID, mList.get(mRecyclerView.getChildPosition(v)).contentID);
                        mContext.startActivity(intent);
                    }
                }

            }

            return false;
        }
    };
}
