package com.pkit.launcher.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.view.FocusScaleHelper.BrowseItemFocusHighlight;

public class SearchKeyboardFragment extends Fragment implements OnClickListener, OnFocusChangeListener {
	public static final String KEY_A = "A";
	public static final String KEY_B = "B";
	public static final String KEY_C = "C";
	public static final String KEY_D = "D";
	public static final String KEY_E = "E";
	public static final String KEY_F = "F";
	public static final String KEY_G = "G";
	public static final String KEY_H = "H";
	public static final String KEY_I = "I";
	public static final String KEY_J = "J";
	public static final String KEY_K = "K";
	public static final String KEY_L = "L";
	public static final String KEY_M = "M";
	public static final String KEY_N = "N";
	public static final String KEY_O = "O";
	public static final String KEY_P = "P";
	public static final String KEY_Q = "Q";
	public static final String KEY_R = "R";
	public static final String KEY_S = "S";
	public static final String KEY_T = "T";
	public static final String KEY_U = "U";
	public static final String KEY_V = "V";
	public static final String KEY_W = "W";
	public static final String KEY_X = "X";
	public static final String KEY_Y = "Y";
	public static final String KEY_Z = "Z";
	public static final String NUMBER_1 = "1";
	public static final String NUMBER_2 = "2";
	public static final String NUMBER_3 = "3";
	public static final String NUMBER_4 = "4";
	public static final String NUMBER_5 = "5";
	public static final String NUMBER_6 = "6";
	public static final String NUMBER_7 = "7";
	public static final String NUMBER_8 = "8";
	public static final String NUMBER_9 = "9";
	public static final String NUMBER_0 = "0";
	public static final String DEL = "删除";
	private static final long DELAYMILLIS = 2 * 1000;
	private RelativeLayout keyboardLayout;
	private TextView inputText;
	private StringBuffer inputBuf;
	private Handler handler;
	private Activity activity;
	private int selectedColor;
	private int normalColor;
	private float selectedSize;
	private float normalSize;
	private BrowseItemFocusHighlight focusedAnim;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		inputBuf = new StringBuffer();
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		keyboardLayout = (RelativeLayout) inflater.inflate(R.layout.search_keyboard_layout, container, false);
		init();
		return keyboardLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private void init() {
		selectedColor = activity.getResources().getColor(R.color.color_fafafa);
		normalColor = activity.getResources().getColor(R.color.color_b0b0b0);
		selectedSize = activity.getResources().getDimension(R.dimen.text_size_20_sp);
		normalSize = activity.getResources().getDimension(R.dimen.text_size_18_sp);

		inputText = (TextView) keyboardLayout.findViewById(R.id.search_input);
		inputText.setText("");
		TextView exampleText = (TextView) keyboardLayout.findViewById(R.id.search_example);
		String source = "支持 “拼音首字母” 搜索<br/>例如：<font color='#00a54a'>y</font>ue <font color='#00a54a'>y</font>u 《越狱》";
		Spanned exampleSpanned = Html.fromHtml(source);
		exampleText.setText(exampleSpanned);

		focusedAnim = new BrowseItemFocusHighlight(FocusScale.ZOOM_FACTOR_MEDIUM);

		TextView keyA = (TextView) keyboardLayout.findViewById(R.id.search_key_a);
		TextView keyB = (TextView) keyboardLayout.findViewById(R.id.search_key_b);
		TextView keyC = (TextView) keyboardLayout.findViewById(R.id.search_key_c);
		TextView keyD = (TextView) keyboardLayout.findViewById(R.id.search_key_d);
		TextView keyE = (TextView) keyboardLayout.findViewById(R.id.search_key_e);
		TextView keyF = (TextView) keyboardLayout.findViewById(R.id.search_key_f);
		TextView keyG = (TextView) keyboardLayout.findViewById(R.id.search_key_g);
		TextView keyH = (TextView) keyboardLayout.findViewById(R.id.search_key_h);
		TextView keyI = (TextView) keyboardLayout.findViewById(R.id.search_key_i);
		TextView keyJ = (TextView) keyboardLayout.findViewById(R.id.search_key_j);
		TextView keyK = (TextView) keyboardLayout.findViewById(R.id.search_key_k);
		TextView keyL = (TextView) keyboardLayout.findViewById(R.id.search_key_l);
		TextView keyM = (TextView) keyboardLayout.findViewById(R.id.search_key_m);
		TextView keyN = (TextView) keyboardLayout.findViewById(R.id.search_key_n);
		TextView keyO = (TextView) keyboardLayout.findViewById(R.id.search_key_o);
		TextView keyP = (TextView) keyboardLayout.findViewById(R.id.search_key_p);
		TextView keyQ = (TextView) keyboardLayout.findViewById(R.id.search_key_q);
		TextView keyR = (TextView) keyboardLayout.findViewById(R.id.search_key_r);
		TextView keyS = (TextView) keyboardLayout.findViewById(R.id.search_key_s);
		TextView keyT = (TextView) keyboardLayout.findViewById(R.id.search_key_t);
		TextView keyU = (TextView) keyboardLayout.findViewById(R.id.search_key_u);
		TextView keyV = (TextView) keyboardLayout.findViewById(R.id.search_key_v);
		TextView keyW = (TextView) keyboardLayout.findViewById(R.id.search_key_w);
		TextView keyX = (TextView) keyboardLayout.findViewById(R.id.search_key_x);
		TextView keyY = (TextView) keyboardLayout.findViewById(R.id.search_key_y);
		TextView keyZ = (TextView) keyboardLayout.findViewById(R.id.search_key_z);
		TextView key0 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_0);
		TextView key1 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_1);
		TextView key2 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_2);
		TextView key3 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_3);
		TextView key4 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_4);
		TextView key5 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_5);
		TextView key6 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_6);
		TextView key7 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_7);
		TextView key8 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_8);
		TextView key9 = (TextView) keyboardLayout.findViewById(R.id.search_key_number_9);
		TextView keyDel = (TextView) keyboardLayout.findViewById(R.id.search_key_del);

		keyA.setOnClickListener(this);
		keyA.setOnFocusChangeListener(this);
		keyA.requestFocus();

		keyB.setOnClickListener(this);
		keyB.setOnFocusChangeListener(this);

		keyC.setOnClickListener(this);
		keyC.setOnFocusChangeListener(this);

		keyD.setOnClickListener(this);
		keyD.setOnFocusChangeListener(this);

		keyE.setOnClickListener(this);
		keyE.setOnFocusChangeListener(this);

		keyF.setOnClickListener(this);
		keyF.setOnFocusChangeListener(this);

		keyG.setOnClickListener(this);
		keyG.setOnFocusChangeListener(this);

		keyH.setOnClickListener(this);
		keyH.setOnFocusChangeListener(this);

		keyI.setOnClickListener(this);
		keyI.setOnFocusChangeListener(this);

		keyJ.setOnClickListener(this);
		keyJ.setOnFocusChangeListener(this);

		keyK.setOnClickListener(this);
		keyK.setOnFocusChangeListener(this);

		keyL.setOnClickListener(this);
		keyL.setOnFocusChangeListener(this);

		keyM.setOnClickListener(this);
		keyM.setOnFocusChangeListener(this);

		keyN.setOnClickListener(this);
		keyN.setOnFocusChangeListener(this);

		keyO.setOnClickListener(this);
		keyO.setOnFocusChangeListener(this);

		keyP.setOnClickListener(this);
		keyP.setOnFocusChangeListener(this);

		keyQ.setOnClickListener(this);
		keyQ.setOnFocusChangeListener(this);

		keyR.setOnClickListener(this);
		keyR.setOnFocusChangeListener(this);

		keyS.setOnClickListener(this);
		keyS.setOnFocusChangeListener(this);

		keyT.setOnClickListener(this);
		keyT.setOnFocusChangeListener(this);

		keyU.setOnClickListener(this);
		keyU.setOnFocusChangeListener(this);

		keyV.setOnClickListener(this);
		keyV.setOnFocusChangeListener(this);

		keyW.setOnClickListener(this);
		keyW.setOnFocusChangeListener(this);

		keyX.setOnClickListener(this);
		keyX.setOnFocusChangeListener(this);

		keyY.setOnClickListener(this);
		keyY.setOnFocusChangeListener(this);

		keyZ.setOnClickListener(this);
		keyZ.setOnFocusChangeListener(this);

		key0.setOnClickListener(this);
		key0.setOnFocusChangeListener(this);

		key1.setOnClickListener(this);
		key1.setOnFocusChangeListener(this);

		key2.setOnClickListener(this);
		key2.setOnFocusChangeListener(this);

		key3.setOnClickListener(this);
		key3.setOnFocusChangeListener(this);

		key4.setOnClickListener(this);
		key4.setOnFocusChangeListener(this);

		key5.setOnClickListener(this);
		key5.setOnFocusChangeListener(this);

		key6.setOnClickListener(this);
		key6.setOnFocusChangeListener(this);

		key7.setOnClickListener(this);
		key7.setOnFocusChangeListener(this);

		key8.setOnClickListener(this);
		key8.setOnFocusChangeListener(this);

		key9.setOnClickListener(this);
		key9.setOnFocusChangeListener(this);

		keyDel.setOnClickListener(this);
		keyDel.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		int inputBufLen = inputBuf.length();
		if (id == R.id.search_key_del) {
			if (inputBufLen > 0) {
				inputBuf.deleteCharAt(inputBufLen - 1);
			}
		} else {
			String inputStr = ((TextView) v).getText().toString();
			inputBuf.append(inputStr);
		}
		inputText.setText(inputBuf);
		sendSearchMessage(inputBuf.toString(), 16);
	}

	private void sendSearchMessage(String key, int count) {
		if (handler == null) {
			return;
		}

		handler.removeMessages(SearchMessageManager.SEARCH);
		Message msg = handler.obtainMessage(SearchMessageManager.SEARCH, 0, count, key);
		handler.sendMessageDelayed(msg, DELAYMILLIS);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		TextView inputKey = (TextView) v;
		focusedAnim.onItemFocused(v, hasFocus);
		if (hasFocus) {
			inputKey.setTextColor(selectedColor);
			inputKey.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
			inputKey.bringToFront();
		} else {
			inputKey.setTextColor(normalColor);
			inputKey.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
		}
	}
}
