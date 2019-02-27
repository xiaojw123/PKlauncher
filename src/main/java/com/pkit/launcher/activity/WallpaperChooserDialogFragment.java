package com.pkit.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkit.launcher.R;
import com.pkit.launcher.animation.OvershootInRightAnimator;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HorizontalGridView;
import com.pkit.launcher.view.OnChildSelectedListener;
import com.pkit.launcher.view.OnItemViewClickedListener;

import java.io.IOException;
import java.util.ArrayList;

public class WallpaperChooserDialogFragment extends DialogFragment implements OnChildSelectedListener, OnItemViewClickedListener {

    private static final String TAG = "WallpaperChooser";
    private static final String EMBEDDED_KEY = "com.pkit.launcher.WallpaperChooserDialogFragment.EMBEDDED_KEY";
    private static final int DURATION = 700;
    private boolean mEmbedded;

    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    private WallpaperLoader mLoader;
    private WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();
    private WallpaperAdapter mAdapter;
    private HorizontalGridView mWallpaperGallery;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                 for (int i =0;i<mThumbs.size(); i++) {
                     mAdapter.addItem(i,mThumbs.get(i));
                }
                mHandler.sendEmptyMessageDelayed(2, DURATION+200);
            }else if (msg.what == 2) {

                mWallpaperGallery.getChildAt(0).requestFocus();
            }
        }
    };

    public static WallpaperChooserDialogFragment newInstance() {
        WallpaperChooserDialogFragment fragment = new WallpaperChooserDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(EMBEDDED_KEY)) {
            mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY);
        } else {
            mEmbedded = isInLayout();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EMBEDDED_KEY, mEmbedded);
    }

    private void cancelLoader() {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel(true);
            mLoader = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cancelLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelLoader();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        findWallpapers();
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findWallpapers();

        if (mEmbedded) {
            View view = inflater.inflate(R.layout.wallpaper_chooser, container, false);
            view.setBackground(mWallpaperDrawable);

            mWallpaperGallery = (HorizontalGridView) view.findViewById(R.id.wallpaper_gallery);
            mWallpaperGallery.setHasFixedSize(true);
            if (mWallpaperGallery != null) {
                RecyclerView.RecycledViewPool recycledViewPool = mWallpaperGallery.getRecycledViewPool();
                mWallpaperGallery.setRecycledViewPool(recycledViewPool);
                mWallpaperGallery.setItemAnimator(new OvershootInRightAnimator());
                mWallpaperGallery.getItemAnimator().setAddDuration(DURATION);
                mAdapter = new WallpaperAdapter(getActivity(), mThumbs);
                mHandler.sendEmptyMessageDelayed(1,300);
                FocusScaleHelper.setupFragmentItemFocus(mAdapter, FocusScale.ZOOM_FACTOR_MEDIUM);
                mWallpaperGallery.setAdapter(mAdapter);
                mWallpaperGallery.setOnChildSelectedListener(WallpaperChooserDialogFragment.this);
                mWallpaperGallery.setOnItemViewClickedListener(WallpaperChooserDialogFragment.this);
            }
            return view;
        }
        return null;
    }

    private void selectWallpaper(int position) {
        try {
            @SuppressLint("ServiceCast")
            WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(Context.WALLPAPER_SERVICE);
            wpm.setResource(mImages.get(position));
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("config",0).edit();
            editor.putInt("wallpaper", mImages.get(position));
            editor.commit();
            Activity activity = getActivity();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
    }

    private void findWallpapers() {
        mThumbs = new ArrayList<Integer>(24);
        mImages = new ArrayList<Integer>(24);

        final Resources resources = getResources();
        final String packageName = resources.getResourcePackageName(R.array.wallpapers);

        addWallpapers(resources, packageName, R.array.wallpapers);
        addWallpapers(resources, packageName, R.array.extra_wallpapers);
    }

    private void addWallpapers(Resources resources, String packageName, int list) {
        final String[] extras = resources.getStringArray(list);
        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                final int thumbRes = resources.getIdentifier(extra + "_small",
                        "drawable", packageName);

                if (thumbRes != 0) {
                    mThumbs.add(thumbRes);
                    mImages.add(res);
                     Log.d(TAG, "add: [" + packageName + "]: " + extra + " (" + res + ")");
                }
            }
        }
    }

    @Override
    public void onChildSelected(ViewGroup parent, View view, int position, long id) {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel();
        }
        mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
    }

    @Override
    public void onItemClicked(View view, int position) {
        selectWallpaper(position);
    }

    class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
        WallpaperLoader() {
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (isCancelled()) return null;
            try {
                final Drawable d = getResources().getDrawable(mImages.get(params[0]));
                if (d instanceof BitmapDrawable) {
                    return ((BitmapDrawable) d).getBitmap();
                }
                return null;
            } catch (OutOfMemoryError e) {
                Log.w(TAG, String.format(
                                "Out of memory trying to load wallpaper res=%08x", params[0]),
                        e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            if (b == null) return;

            if (!isCancelled()) {
                View v = getView();
                if (v != null) {
                    mWallpaperDrawable.setBitmap(b);
                    v.postInvalidate();
                } else {
                    mWallpaperDrawable.setBitmap(null);
                }
                mLoader = null;
            } else {
                b.recycle();
            }
        }

        void cancel() {
            super.cancel(true);
        }
    }

    static class WallpaperDrawable extends Drawable {

        Bitmap mBitmap;
        int mIntrinsicWidth;
        int mIntrinsicHeight;
        Matrix mMatrix;

        /* package */void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mBitmap == null)
                return;
            mIntrinsicWidth = mBitmap.getWidth();
            mIntrinsicHeight = mBitmap.getHeight();
            mMatrix = null;
        }

        @Override
        public void draw(Canvas canvas) {
            if (mBitmap == null) return;

            if (mMatrix == null) {
                final int vwidth = canvas.getWidth();
                final int vheight = canvas.getHeight();
                final int dwidth = mIntrinsicWidth;
                final int dheight = mIntrinsicHeight;

                float scale = 1.0f;

                if (dwidth < vwidth || dheight < vheight) {
                    scale = Math.max((float) vwidth / (float) dwidth,
                            (float) vheight / (float) dheight);
                }

                float dx = (vwidth - dwidth * scale) * 0.5f + 0.5f;
                float dy = (vheight - dheight * scale) * 0.5f + 0.5f;

                mMatrix = new Matrix();
                mMatrix.setScale(scale, scale);
                mMatrix.postTranslate((int) dx, (int) dy);
            }

            canvas.drawBitmap(mBitmap, mMatrix, null);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.OPAQUE;
        }

        @Override
        public void setAlpha(int alpha) {
            // Ignore
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            // Ignore
        }
    }
}
