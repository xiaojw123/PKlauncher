package com.pkit.launcher.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.view.HomeRecyclerView;

import java.util.List;

/**
 * Created by jiaxing on 2015/3/25.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private Context mContext;
    private  List<String> mList;
    private ViewHolder mViewHolder;
    private HomeRecyclerView mHorizontalGridView;

    public TimeLineAdapter(Context context, List<String> list, HomeRecyclerView timelineView){
        mContext = context;
        mList = list;
        mHorizontalGridView = timelineView;
    }

    public void addItem(int position , String str) {
        mList.add(position, str);
        notifyItemInserted(position);
    }

    public void updateItem(int position, String str) {
        mList.set(position, str);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        if (position >= 0 && position <= mList.size() - 1) {
            notifyItemChanged(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recently_watched_item_time_line,parent,false);
        mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ((ViewGroup)holder.itemView).removeAllViews();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recently_watched_item_time_line,(ViewGroup)holder.itemView,true);
        view.setTag(mList.get(position));
        updateView(view, position);
        ((TextView)view.findViewById(R.id.time)).setText(mList.get(position));
    }

    private void updateView(View view,int position) {
        view.findViewById(R.id.timepoint).setVisibility(View.VISIBLE);
        view.findViewById(R.id.time).setVisibility(View.VISIBLE);
        if (position == 0) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.findViewById(R.id.line).getLayoutParams();
            lp.leftMargin = 20;
            view.findViewById(R.id.line).setLayoutParams(lp);
        }

        if(position !=0 && mList.get(position).equals(mList.get(position -1))){
            view.findViewById(R.id.timepoint).setVisibility(View.GONE);
            view.findViewById(R.id.time).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView timePointView;
        public ImageView lineView;
        public TextView timeTextView;
        public ViewHolder(View view) {
            super(view);
            timePointView = (ImageView) view.findViewById(R.id.timepoint);
            timeTextView = (TextView) view.findViewById(R.id.time);
            lineView = (ImageView) view.findViewById(R.id.line);
        }
    }
}
