package com.onion99.videoeditor.listvideoandmyvideo;

import android.net.Uri;

public class VideoData {
    public String duration;
    public Uri videouri;
    public long videoId;
    public String videoName;
    public String videoPath;

    public VideoData(String str, Uri uri, String str2, String str3) {
        this.videoName = str;
        this.videouri = uri;
        this.videoPath = str2;
        this.duration = str3;
    }

    public VideoData(String str, Uri uri, String str2) {
        this.videoName = str;
        this.videouri = uri;
        this.videoPath = str2;
    }
}
