package com.pkit.launcher.view.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.SettingsFragment;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.HomeRecyclerView;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private Context mContext;
    private List<SettingsFragment.Settings> mList;
    private HomeRecyclerView mHomeRecyclerView;

    public FocusScaleHandler mFocusHighlight;

    public void setFocusScale(FocusScaleHandler listener) {
        mFocusHighlight = listener;
    }

    final OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener();

    final class OnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(view, hasFocus);
            }

            View focusView = view.findViewById(R.id.home_settings_focus_view);
            int position = mHomeRecyclerView.getChildPosition(view);
            ImageView imageView = (ImageView) view.findViewById(R.id.home_settings_image);
            if (hasFocus) {
                if (focusView != null) {
                    imageView.setImageResource(mList.get(position).focusImageResource);
                    focusView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.home_settings_layout).setBackgroundResource(R.drawable.home_settings_bg_focus);
                    ((TextView)view.findViewById(R.id.home_settings_text)).setTextColor(mContext.getResources().getColor(R.color.settings_text_color_focus));
                    ((TextView)view.findViewById(R.id.home_settings_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                }
                ((HomeRecyclerView) view.getParent()).postInvalidate();
            } else {
                if (focusView != null) {
                    focusView.setVisibility(View.GONE);
                    view.findViewById(R.id.home_settings_layout).setBackgroundResource(R.drawable.home_settings_bg);
                    ((TextView)view.findViewById(R.id.home_settings_text)).setTextColor(mContext.getResources().getColor(R.color.settings_text_color));
                    ((TextView)view.findViewById(R.id.home_settings_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
                imageView.setImageResource(mList.get(position).unfocusImageResource);
            }
        }
    }


    public SettingsAdapter(Context context, List<SettingsFragment.Settings> list,HomeRecyclerView recyclerView) {
        mContext = context;
        mList = list;
        mHomeRecyclerView = recyclerView;
    }

    public void addItem(int position, SettingsFragment.Settings settings) {
        mList.add(position, settings);
        notifyItemInserted(position);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.settings_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        setupFocusListener(holder.itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.itemView.setId(R.id.settings_first_item);
        }
        holder.mImageView.setImageResource(mList.get(position).unfocusImageResource);
        holder.mTextView.setText(mList.get(position).textResource);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.home_settings_image);
            mTextView = (TextView) view.findViewById(R.id.home_settings_text);

        }
    }
}
