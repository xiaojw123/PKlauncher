package com.pkit.launcher.service;

public class Source {
    public String playUrl;
    public String name;
    public int seekPosition;
    public int set;

    public String getPlayUrl() {
	return playUrl;
    }

    public void setPlayUrl(String playUrl) {
	this.playUrl = playUrl;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getSeekPosition() {
	return seekPosition;
    }

    public void setSeekPosition(int seekPosition) {
	this.seekPosition = seekPosition;
    }

    public void setSourceCount(int set) {
	this.set = set;
    }
}
