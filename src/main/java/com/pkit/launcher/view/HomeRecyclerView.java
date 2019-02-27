package com.pkit.launcher.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by jiaxing on 2015/3/31.
 */
public class HomeRecyclerView extends RecyclerView {

    private OnItemViewClickedListener mOnItemViewClickedListener;

    public HomeRecyclerView(Context context) {
        super(context);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public HomeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public HomeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    /**
     * see StaggeredGridLayoutManager.smoothScrollToPosition
     */
    @Override
    public void smoothScrollToPosition(int position) {
        getLayoutManager().smoothScrollToPosition(this,new State(),position);
        //super.smoothScrollToPosition(position);
    }

    public void setOnItemViewClickedListener(OnItemViewClickedListener listener){
        mOnItemViewClickedListener = listener;
    }

    public OnItemViewClickedListener getOnItemViewClickedListener(){
        return mOnItemViewClickedListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                if(mOnItemViewClickedListener != null){
                    View view = this.getFocusedChild();
                    getOnItemViewClickedListener().onItemClicked(view, getChildPosition(view));
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return getChildDrawingOrder(this, childCount, i);
    }


    int getChildDrawingOrder(RecyclerView recyclerView, int childCount, int i) {
        View view = getFocusedChild();
        if (view == null) {
            return i;
        }
        int focusIndex = recyclerView.indexOfChild(view);
        // supposely 0 1 2 3 4 5 6 7 8 9, 4 is the center item
        // drawing order is 0 1 2 3 9 8 7 6 5 4
        if (i < focusIndex) {
            return i;
        } else if (i < childCount - 1) {
            return focusIndex + childCount - 1 - i;
        } else {
            return focusIndex;
        }
    }
}
