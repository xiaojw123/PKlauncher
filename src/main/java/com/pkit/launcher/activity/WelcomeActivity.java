package com.pkit.launcher.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pkit.launcher.R;
import com.pkit.launcher.bean.DeviceConfig;
import com.pkit.launcher.bean.Upgrade;
import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.message.BaseCallBack;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.TimerService;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.utils.Utils;
import com.pkit.utils.UpgradeUtil;

import java.io.File;

/**
 * Created by jiaxing on 2015/6/30.
 */
public class WelcomeActivity extends Activity implements ServiceConnection {
    private String mMACAddress;
    private IContentService mContentService;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                DeviceConfig config = bundle.getParcelable("deviceLogin");
                Toast.makeText(WelcomeActivity.this, "正在检查更新，请稍后！", Toast.LENGTH_LONG).show();
                String upgradeurl = Configuration.getUpgradeListUri(Configuration.deviceId, config.getRuleId(), UpgradeUtil.getVersionCode(WelcomeActivity.this));
                Log.e("tag", upgradeurl);
                try {
                    mContentService.registCallback(new ContentServiceCallback());
                    mContentService.checkUpgrade(upgradeurl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } else if (msg.what == 2) {
                /*Intent intent = new Intent(WelcomeActivity.this, TimerService.class);
                startService(intent);
                intent =  new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);*/
                WelcomeActivity.this.finish();
            } else if (msg.what == 3) {
                Bundle bundle = msg.getData();
                Upgrade upgrade = bundle.getParcelable("upgradeinfo");
                if (upgrade != null) {
                    if (!TextUtils.isEmpty(upgrade.getPackageLocation())) {
                        new UpgradeTask(WelcomeActivity.this).execute(upgrade.getPackageLocation());
                    }
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, TimerService.class);
                    startService(intent);
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        bindService();
        mMACAddress = Utils.getLocalMacAddress(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private void bindService() {
        Intent intent = new Intent(this, ContentService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mContentService = IContentService.Stub.asInterface(service);
        try {
            mContentService.registCallback(new ContentServiceCallback());
            Log.e("tag", mMACAddress);
            mContentService.deviceLogin(mMACAddress);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    class UpgradeTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;

        public UpgradeTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                File file = UpgradeUtil.upgrade(params[0], Environment.getExternalStorageDirectory() + "/ottdownload", "upgrade_apk.apk");
                if (file != null) {
                    UpgradeUtil.install(mContext, file);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            } else {
                Toast.makeText(WelcomeActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    class ContentServiceCallback extends BaseCallBack {

        @Override
        public void onDeviceLoginComplete(Bundle bundle) throws RemoteException {
            Message msg = new Message();
            msg.what = 1;
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        }

        @Override
        public void onCheckUpgradeComplete(Bundle bundle) throws RemoteException {
            Message msg = new Message();
            msg.what = 3;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }
}
