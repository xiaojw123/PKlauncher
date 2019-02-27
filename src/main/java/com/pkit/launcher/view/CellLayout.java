package com.pkit.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jiaxing on 2015/4/21.
 */
public class CellLayout extends RelativeLayout {
    private static final String TAG = "HomePagedView";

    public CellLayout(Context context) {
        this(context,null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChildrenDrawingOrderEnabled(true);
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return getChildDrawingOrder(this, childCount, i);
    }

    int getChildDrawingOrder(CellLayout layout, int childCount, int i) {
        View view = getFocusedChild();
        if (view == null) {
            return i;
        }
        int focusIndex = layout.indexOfChild(view);
        if (i < focusIndex) {
            return i;
        } else if (i < childCount - 1) {
            return focusIndex + childCount - 1 - i;
        } else {
            return focusIndex;
        }
    }
}
