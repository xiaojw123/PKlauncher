package com.pkit.launcher.message;

import java.util.List;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IServiceCallback;
import com.pkit.launcher.service.aidl.TagGroup;

public class BaseCallBack extends IServiceCallback.Stub {

	@Override
	public IBinder asBinder() {
		return null;
	}

	@Override
	public void onLoadComplete(String contentID, int pageNumber, int count, List<Content> contents) throws RemoteException {

	}

	@Override
	public void onDetailLoadComplete(Detail detail) throws RemoteException {

	}

	@Override
	public void onLoading(String contentID) throws RemoteException {

	}

	@Override
	public void onFailed(String contentID) throws RemoteException {

	}

	@Override
	public void onDeviceLoginComplete(Bundle bundle) throws RemoteException {

	}

	@Override
	public void onCheckUpgradeComplete(Bundle bundle) throws RemoteException {

	}

	@Override
	public void onTagGroupComplete(List<TagGroup> tagGroups) throws RemoteException {

	}

}
