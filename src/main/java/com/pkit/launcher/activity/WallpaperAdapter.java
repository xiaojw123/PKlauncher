package com.pkit.launcher.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pkit.launcher.R;
import com.pkit.launcher.view.FocusScaleHandler;

import java.util.ArrayList;

/**
 * Created by lijiaxing on 2015/4/11.
 */
public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Integer> mThumbs = new ArrayList<Integer>();

    private FocusScaleHandler mFocusHighlight;
    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    final class OnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }
            if(hasFocus){
                view.findViewById(R.id.wallpaper_item_focus).setVisibility(View.VISIBLE);
            }else{
                view.findViewById(R.id.wallpaper_item_focus).setVisibility(View.GONE);
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

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }

    public WallpaperAdapter(Context context, ArrayList<Integer> thumbs) {
        mContext = context;

    }

    public void addItem(int position,int info) {
        mThumbs.add(position,info);
        notifyItemInserted(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wallpaper_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        setupFocusListener(viewHolder.itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {


        int thumbRes = mThumbs.get(position);
        viewHolder.image.setImageResource(thumbRes);
        Drawable thumbDrawable =  viewHolder.image.getDrawable();
        if (thumbDrawable != null) {
            thumbDrawable.setDither(true);
        } else {
            Log.e("wallpaper", "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
                    + position);
        }
    }

    @Override
    public int getItemCount() {
        return mThumbs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.wallpaper_image);
        }
    }
}
