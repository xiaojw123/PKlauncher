package com.pkit.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public abstract class SmoothPagedView extends PagedView {
    private static final float SMOOTHING_SPEED = 0.75f;
    private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math.log(SMOOTHING_SPEED));

    private float mBaseLineFlingVelocity;
    private float mFlingVelocityInfluence;

    static final int DEFAULT_MODE = 0;
    static final int X_LARGE_MODE = 1;

    int mScrollMode;

    private Interpolator mScrollInterpolator;

    public static class OvershootInterpolator implements Interpolator {
        private static final float DEFAULT_TENSION = 1.3f;
        private float mTension;

        public OvershootInterpolator() {
            mTension = DEFAULT_TENSION;
        }

        public void setDistance(int distance) {
            mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
        }

        public void disableSettle() {
            mTension = 0.f;
        }

        public float getInterpolation(float t) {
            // _o(t) = t * t * ((tension + 1) * t + tension)
            // o(t) = _o(t - 1) + 1
            t -= 1.0f;
            return t * t * ((mTension + 1) * t + mTension) + 1.0f;
        }
    }

    public SmoothPagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothPagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mUsePagingTouchSlop = false;

        mDeferScrollUpdate = mScrollMode != X_LARGE_MODE;
    }

    protected int getScrollMode() {
        return X_LARGE_MODE;
    }

    @Override
    protected void init() {
        super.init();

        mScrollMode = getScrollMode();
        if (mScrollMode == DEFAULT_MODE) {
            mBaseLineFlingVelocity = 2500.0f;
            mFlingVelocityInfluence = 0.4f;
            mScrollInterpolator = new OvershootInterpolator();
            mScroller = new Scroller(getContext(), mScrollInterpolator);
        }
    }

    @Override
    protected void snapToDestination() {
        if (mScrollMode == X_LARGE_MODE) {
            super.snapToDestination();
        } else {
            snapToPageWithVelocity(getPageNearestToCenterOfScreen(), 0);
        }
    }

    @Override
    protected void snapToPageWithVelocity(int whichPage, int velocity) {
        if (mScrollMode == X_LARGE_MODE) {
            super.snapToPageWithVelocity(whichPage, velocity);
        } else {
            snapToPageWithVelocity(whichPage, 0, true);
        }
    }

    private void snapToPageWithVelocity(int whichPage, int velocity, boolean settle) {

        whichPage = Math.max(0, Math.min(whichPage, getChildCount() - 1));

        final int screenDelta = Math.max(1, Math.abs(whichPage - mCurrentPage));
        final int newX = getChildOffset(whichPage) - getRelativeChildOffset(whichPage);
        final int delta = newX - mUnboundedScrollX;
        int duration = (screenDelta + 1) * 100;

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        if (settle) {
            ((OvershootInterpolator) mScrollInterpolator).setDistance(screenDelta);
        } else {
            ((OvershootInterpolator) mScrollInterpolator).disableSettle();
        }

        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration += (duration / (velocity / mBaseLineFlingVelocity)) * mFlingVelocityInfluence;
        } else {
            duration += 100;
        }

        snapToPage(whichPage, delta, duration);
    }

    @Override
    protected void snapToPage(int whichPage) {
       if (mScrollMode == X_LARGE_MODE) {
           super.snapToPage(whichPage);
       } else {
           snapToPageWithVelocity(whichPage, 0, false);
       }
    }

    @Override
    public void computeScroll() {
        if (mScrollMode == X_LARGE_MODE) {
            super.computeScroll();
        } else {
            boolean scrollComputed = computeScrollHelper();

            if (!scrollComputed && mTouchState == TOUCH_STATE_SCROLLING) {
                final float now = System.nanoTime() / NANOTIME_DIV;
                final float e = (float) Math.exp((now - mSmoothingTime) / SMOOTHING_CONSTANT);

                final float dx = mTouchX - mUnboundedScrollX;
                scrollTo(Math.round(mUnboundedScrollX + dx * e), getScrollY());
                mSmoothingTime = now;

                if (dx > 1.f || dx < -1.f) {
                    invalidate();
                }
            }
        }
    }
}
