package com.pkit.launcher.view.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.MinFragment;
import com.pkit.launcher.view.FocusScaleHandler;
import com.pkit.launcher.view.HomeRecyclerView;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class MineAdapter extends RecyclerView.Adapter<MineAdapter.ViewHolder> {

    private Context mContext;
    private List<MinFragment.Mine> mList;
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

            View focusView = view.findViewById(R.id.home_mine_focus_view);
            ImageView imageView = (ImageView) view.findViewById(R.id.home_mine_image);
            int position = mHomeRecyclerView.getChildPosition(view);

            if (hasFocus) {
                if (focusView != null) {
                    imageView.setImageResource(mList.get(position).focusImageResource);
                    focusView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.home_mine_layout).setBackgroundResource(R.drawable.home_my_bg_focus);
                    ((TextView)view.findViewById(R.id.home_mine_text)).setTextColor(mContext.getResources().getColor(R.color.settings_text_color_focus));
                    ((TextView)view.findViewById(R.id.home_mine_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                }
                ((HomeRecyclerView) view.getParent()).postInvalidate();
            } else {
                if (focusView != null) {
                    imageView.setImageResource(mList.get(position).imageResource);
                    focusView.setVisibility(View.GONE);
                    view.findViewById(R.id.home_mine_layout).setBackgroundResource(R.drawable.home_my_bg);
                    ((TextView)view.findViewById(R.id.home_mine_text)).setTextColor(mContext.getResources().getColor(R.color.settings_text_color));
                    ((TextView)view.findViewById(R.id.home_mine_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
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

    public MineAdapter(Context context, List<MinFragment.Mine> list, HomeRecyclerView recyclerView){
        mContext = context;
        mList = list;
        mHomeRecyclerView = recyclerView;
    }

    public void addItem(int position,MinFragment.Mine mine) {
        mList.add(position, mine);
        notifyItemInserted(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.mine_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        setupFocusListener(holder.itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.itemView.setId(R.id.mine_first_item);
        }
        holder.mImageView.setImageResource(mList.get(position).imageResource);
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

            mImageView = (ImageView) view.findViewById(R.id.home_mine_image);
            mTextView = (TextView) view.findViewById(R.id.home_mine_text);

        }
    }
}
