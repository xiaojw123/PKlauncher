package com.pkit.launcher.thread;

import java.io.IOException;

import android.media.MediaPlayer;

import com.pkit.launcher.utils.APPLog;

public class PlayerThread extends Thread {
    private MediaPlayer player;
    private String path;
    private int position;

    public void setPlayer(MediaPlayer player) {
	this.player = player;
    }

    public void setPlayPath(String path) {
	this.path = path;
    }

    public void setSeekPosition(int position) {
	this.position = position;
    }

    @Override
    public void run() {
	try {
		APPLog.printDebug("run======");
	    player.reset();
	    APPLog.printDebug("Idle======");
	    APPLog.printDebug("play path===="+path);
	    player.setDataSource(path);
	    APPLog.printDebug("Init======");
	    player.prepare();
	    APPLog.printDebug("prepare======");
	    player.start();
	    APPLog.printDebug("start======");
	    if (position > 0) {
		player.seekTo(position);
		   APPLog.printDebug("seekTo======");
	    }
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IllegalStateException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
