package com.pkit.launcher.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkit.launcher.R;
import com.pkit.launcher.view.FocusScale;
import com.pkit.launcher.view.FocusScaleHelper;
import com.pkit.launcher.view.HomeRecyclerView;
import com.pkit.launcher.view.OnItemViewClickedListener;
import com.pkit.launcher.view.adapter.MineAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaxing on 2015/3/19.
 */
public class MinFragment extends Fragment implements OnItemViewClickedListener{

	private HomeRecyclerView mRecyclerView;
	private List<Mine> mList;

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent();
                intent.setClass(getActivity(), UserRecordActivity.class);
                intent.putExtra(UserRecordActivity.USER_RECORD_TYPE, UserRecordActivity.USER_RECORD_RECENTLY_WATCHED);
                getActivity().startActivity(intent);
                break;
            case 1:
                intent = new Intent();
                intent.setClass(getActivity(), UserRecordActivity.class);
                intent.putExtra(UserRecordActivity.USER_RECORD_TYPE, UserRecordActivity.USER_RECORD_COLLECTION);
                getActivity().startActivity(intent);
                break;
        }
    }

    public static class Mine {
		public int imageResource;
        public int focusImageResource;
		public int textResource;
	}

	int[] image = { R.drawable.home_my_icon_time, R.drawable.home_my_icon_like, R.drawable.home_my_icon_shop };
    int[] focusImage = { R.drawable.home_my_icon_time_focus, R.drawable.home_my_icon_like_focus, R.drawable.home_my_icon_shop_focus };
	int[] text = { R.string.home_mine_recently_watched, R.string.home_mine_collection, R.string.home_mine_buy, };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mList = new ArrayList<Mine>();
		for (int i = 0; i < 3; i++) {
			Mine mine = new Mine();
			mine.imageResource = image[i];
			mine.textResource = text[i];
            mine.focusImageResource = focusImage[i];
            mList.add(mine);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mine_layout, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Activity activity = getActivity();

		mRecyclerView = (HomeRecyclerView) view.findViewById(R.id.list);
		GridLayoutManager gm = new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);
		mRecyclerView.setLayoutManager(gm);
		mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnItemViewClickedListener(this);
		setAdapter(activity);
	}

    @Override
    public void onResume() {
        super.onResume();
    }

	private void setAdapter(Activity activity) {
		MineAdapter adapter = new MineAdapter(activity, mList,mRecyclerView);
		FocusScaleHelper.setupFragmentItemFocus(adapter, FocusScale.ZOOM_FACTOR_MEDIUM);
		mRecyclerView.setAdapter(adapter);
	}
}
