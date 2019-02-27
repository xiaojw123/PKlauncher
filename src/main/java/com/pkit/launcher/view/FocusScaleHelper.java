package com.pkit.launcher.view;

import android.animation.TimeAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.RecommendFragment;
import com.pkit.launcher.activity.WallpaperAdapter;
import com.pkit.launcher.view.adapter.CollectionAdapter;
import com.pkit.launcher.view.adapter.MineAdapter;
import com.pkit.launcher.view.adapter.RecentlyWatchedAdapter;
import com.pkit.launcher.view.adapter.SettingsAdapter;
import com.pkit.launcher.view.adapter.TelevisionAdapter;


public class FocusScaleHelper {

	static class FocusAnimator implements TimeAnimator.TimeListener {
		private final View mView;
		private final int mDuration;
		private final float mScaleDiff;
		private float mFocusLevel = 0f;
		private float mFocusLevelStart;
		private float mFocusLevelDelta;
		private final TimeAnimator mAnimator = new TimeAnimator();
		private final Interpolator mInterpolator;

		void animateFocus(boolean select, boolean immediate) {
			endAnimation();
			final float end = select ? 1 : 0;
			if (immediate) {
				setFocusLevel(end);
			} else if (mFocusLevel != end) {
				mFocusLevelStart = mFocusLevel;
				mFocusLevelDelta = end - mFocusLevelStart;
				mAnimator.start();
			}
		}

		FocusAnimator(View view, float scale, int duration) {
			mView = view;
			mDuration = duration;
			mScaleDiff = scale - 1f;
			mAnimator.setTimeListener(this);
			mInterpolator = new AccelerateDecelerateInterpolator();
		}

		void setFocusLevel(float level) {
			mFocusLevel = level;
			float scale = 1f + mScaleDiff * level;
			mView.setScaleX(scale);
			mView.setScaleY(scale);
		}

		void endAnimation() {
			mAnimator.end();
		}

		@Override
		public void onTimeUpdate(TimeAnimator animation, long totalTime,
								 long deltaTime) {
			float fraction;
			if (totalTime >= mDuration) {
				fraction = 1;
				mAnimator.end();
			} else {
				fraction = (float) (totalTime / (double) mDuration);
			}
			if (mInterpolator != null) {
				fraction = mInterpolator.getInterpolation(fraction);
			}
			setFocusLevel(mFocusLevelStart + fraction * mFocusLevelDelta);
		}
	}

	static class BrowseItemFocusHighlight implements FocusScaleHandler {
		private static final int DURATION_MS = 150;

		private static float[] sScaleFactor = new float[4];

		private int mScaleIndex;

		BrowseItemFocusHighlight(int zoomIndex) {
			mScaleIndex = (zoomIndex >= 0 && zoomIndex < sScaleFactor.length) ? zoomIndex
					: FocusScale.ZOOM_FACTOR_MEDIUM;
		}

		private static void lazyInit(Resources resources) {
			if (sScaleFactor[FocusScale.ZOOM_FACTOR_NONE] == 0f) {
				sScaleFactor[FocusScale.ZOOM_FACTOR_NONE] = 1f;
				sScaleFactor[FocusScale.ZOOM_FACTOR_SMALL] = resources.getFraction(
						R.fraction.lb_focus_zoom_factor_small, 1, 1);
				sScaleFactor[FocusScale.ZOOM_FACTOR_MEDIUM] = resources.getFraction(
						R.fraction.lb_focus_zoom_factor_medium, 1, 1);
				sScaleFactor[FocusScale.ZOOM_FACTOR_LARGE] = resources.getFraction(
						R.fraction.lb_focus_zoom_factor_large, 1, 1);
			}
		}

		private float getScale(View view) {
			lazyInit(view.getResources());
			return sScaleFactor[mScaleIndex];
		}

		@Override
		public void onItemFocused(View view, boolean hasFocus) {
			view.setSelected(hasFocus);
			getOrCreateAnimator(view).animateFocus(hasFocus, false);
		}

		@Override
		public void onInitializeView(View view) {
			getOrCreateAnimator(view).animateFocus(false, true);
		}

		private FocusAnimator getOrCreateAnimator(View view) {
			FocusAnimator animator = (FocusAnimator) view
					.getTag(R.id.lb_focus_animator);
			if (animator == null) {
				animator = new FocusAnimator(view, getScale(view),
						DURATION_MS);
				view.setTag(R.id.lb_focus_animator, animator);
			}
			return animator;
		}

	}


	public static void setupFragmentItemFocus(RecentlyWatchedAdapter adapter, int zoomIndex) {
		adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
	}

    public static void setupFragmentItemFocus(CollectionAdapter adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

    public static void setupFragmentItemFocus(RecommendFragment adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

    public static void setupFragmentItemFocus(SettingsAdapter adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

    public static void setupFragmentItemFocus(MineAdapter adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

    public static void setupFragmentItemFocus(TelevisionAdapter adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

    public static void setupFragmentItemFocus(WallpaperAdapter adapter, int zoomIndex) {
        adapter.setFocusScale(new BrowseItemFocusHighlight(zoomIndex));
    }

}