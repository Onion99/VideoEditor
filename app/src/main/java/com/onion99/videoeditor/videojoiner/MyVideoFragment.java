package com.onion99.videoeditor.videojoiner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.onion99.videoeditor.Ads;
import com.onion99.videoeditor.R;
import com.onion99.videoeditor.listvideoandmyvideo.ContentUtill;
import com.onion99.videoeditor.listvideoandmyvideo.VideoData;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class MyVideoFragment extends Fragment {
    MyVideoAdapter a;
    ImageLoader b;
    ArrayList<VideoData> c = new ArrayList<>();
    ListView d;
    String e;
    String[] f;
    private PowerManager g;
    private WakeLock h;
    private AdView i;
    private Ads ads;

    @SuppressLint({"NewApi"})
    private class a extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog a;

        private a() {
            this.a = null;
        }


        public void onPreExecute() {
            this.a = new ProgressDialog(MyVideoFragment.this.getActivity());
            this.a.setMessage("Loading...");
            this.a.setCancelable(false);
            this.a.show();
        }



        public Boolean doInBackground(Void... voidArr) {
            return Boolean.valueOf(MyVideoFragment.this.b());
        }



        public void onPostExecute(Boolean bool) {
            this.a.dismiss();
            if (bool.booleanValue()) {
                MyVideoFragment.this.a = new MyVideoAdapter(MyVideoFragment.this.getActivity(), MyVideoFragment.this.c, MyVideoFragment.this.b);
                MyVideoFragment.this.d.setAdapter(MyVideoFragment.this.a);
            }
        }
    }

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @SuppressLint("InvalidWakeLockTag")
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.videomyfragment, viewGroup, false);
        this.g = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        this.h = this.g.newWakeLock(6, "My Tag");
        a();
        this.d = (ListView) inflate.findViewById(R.id.VideogridView);
        new a().execute(new Void[0]);

        LinearLayout s = (LinearLayout) inflate.findViewById(R.id.banner_AdView);
        ads = new Ads();
        ads.BannerAd(getActivity(),s);

        return inflate;
    }

    private void a() {
        ImageLoaderConfiguration build = new ImageLoaderConfiguration.Builder(getActivity()).memoryCache(new WeakMemoryCache()).defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().bitmapConfig(Config.RGB_565).displayer(new FadeInBitmapDisplayer(400)).build()).build();
        this.b = ImageLoader.getInstance();
        this.b.init(build);
    }


    @SuppressLint({"NewApi"})
    public boolean b() {
        this.e = "_data like?";
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(getResources().getString(R.string.MainFolderName));
        sb.append("/");
        sb.append(getResources().getString(R.string.VideoJoiner));
        sb.append("%");
        this.f = new String[]{sb.toString()};
        Cursor managedQuery = getActivity().managedQuery(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "_display_name", "duration"}, this.e, this.f, " _id DESC");
        int count = managedQuery.getCount();
        if (count <= 0) {
            return false;
        }
        managedQuery.moveToFirst();
        for (int i2 = 0; i2 < count; i2++) {
            Uri withAppendedPath = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI, ContentUtill.getLong(managedQuery));
            this.c.add(new VideoData(managedQuery.getString(managedQuery.getColumnIndexOrThrow("_display_name")), withAppendedPath, managedQuery.getString(managedQuery.getColumnIndex("_data")), ContentUtill.getTime(managedQuery, "duration")));
            managedQuery.moveToNext();
        }
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
