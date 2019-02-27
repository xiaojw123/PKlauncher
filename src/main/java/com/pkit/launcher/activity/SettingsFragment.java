package com.pkit.launcher.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pkit.launcher.R;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.launcher.view.OnItemViewClickedListener;
import com.pkit.launcher.view.adapter.SettingsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class SettingsFragment extends Fragment implements OnItemViewClickedListener {


    public static class Settings {
        public int focusImageResource;
        public int unfocusImageResource;
        public int textResource;
    }

    int[] unfocusImage = {R.drawable.home_setting_network_unfocus, R.drawable.home_setting_child_lock_unfocus, R.drawable.home_setting_wallpaper_unfocus, R.drawable.home_setting_update_unfocus,
            R.drawable.home_setting_factory_setting_unfocus, R.drawable.home_setting_about_unfocus};
    int[] focusImage = {R.drawable.home_setting_network_focus, R.drawable.home_setting_child_lock_focus, R.drawable.home_setting_wallpaper_focus, R.drawable.home_setting_update_focus,
            R.drawable.home_setting_factory_setting_focus, R.drawable.home_setting_about_focus};
    int[] text = {R.string.home_settings_network, R.string.home_settings_child, R.string.home_settings_wallpaper, R.string.home_settings_update,
            R.string.home_settings_reset, R.string.home_settings_about};

    public List<Settings> mList;

    private HomeRecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<Settings>();
        for (int i = 0; i < 6; i++) {
            Settings settings = new Settings();
            settings.unfocusImageResource = unfocusImage[i];
            settings.focusImageResource = focusImage[i];
            settings.textResource = text[i];
            mList.add(settings);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();

        mRecyclerView = (HomeRecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setOnItemViewClickedListener(this);
        GridLayoutManager gm = new GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(gm);
        mRecyclerView.setHasFixedSize(true);
        setAdapter(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void setAdapter(Activity activity) {
        SettingsAdapter adapter = new SettingsAdapter(activity, mList, mRecyclerView);
        FocusScaleHelper.setupFragmentItemFocus(adapter, FocusScale.ZOOM_FACTOR_MEDIUM);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent();
                ComponentName comp = new ComponentName("com.xiaomi.mitv.settings", "com.xiaomi.mitv.settings.entry.MainActivity");
                intent.setComponent(comp);
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(getActivity(), ChildrenLockActivity.class);
                getActivity().startActivity(intent);
                break;
            case 2:
                intent = new Intent();
                intent.setClass(getActivity(), WallpaperChooser.class);
                startActivity(view,intent);
                break;
            case 3:
                intent = new Intent();
                intent.setClass(getActivity(), CheckUpdateActivity.class);
                startActivity(view,intent);
                break;
            case 4:

                break;
        }
    }

   private void  startActivity(View view,Intent intent) {
       ImageView iv = (ImageView) view.findViewById(R.id.home_settings_image);
       iv.setDrawingCacheEnabled(true);
       iv.setPressed(false);
       iv.refreshDrawableState();
       Bitmap bm = iv.getDrawingCache();
       ActivityOptions opts = ActivityOptions.makeThumbnailScaleUpAnimation(
               iv, bm, 0, 0);
       startActivity(intent, opts.toBundle());
       iv.setDrawingCacheEnabled(false);
   }

}
