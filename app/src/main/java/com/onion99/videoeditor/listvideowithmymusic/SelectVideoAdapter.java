package com.onion99.videoeditor.listvideowithmymusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onion99.videoeditor.Helper;
import com.onion99.videoeditor.R;
import com.onion99.videoeditor.videocompress.VideoCompressor;
import com.onion99.videoeditor.videocutter.VideoCutter;
import com.onion99.videoeditor.videotomp3.VideoToMP3ConverterActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SelectVideoAdapter extends BaseAdapter {
    ImageLoader a;
    ArrayList<VideoData> b = new ArrayList<>();
    ArrayList<VideoData> c = new ArrayList<>();
    private LayoutInflater d;

    public final Context e;

    private class a {
        ImageView a;
        TextView b;
        TextView c;

        private a() {
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public SelectVideoAdapter(Context context, ArrayList<VideoData> arrayList, ImageLoader imageLoader) {
        this.e = context;
        this.a = imageLoader;
        this.d = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.c.addAll(arrayList);
        this.b.addAll(arrayList);
    }

    public int getCount() {
        return this.c.size();
    }

    public Object getItem(int i) {
        return this.c.get(i);
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = this.d.inflate(R.layout.row_video, null);
            aVar = new a();
            aVar.a = (ImageView) view.findViewById(R.id.image_preview);
            aVar.c = (TextView) view.findViewById(R.id.file_name);
            aVar.b = (TextView) view.findViewById(R.id.duration);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        this.a.displayImage(((VideoData) this.c.get(i)).VideoUri.toString(), aVar.a, new Builder().showImageForEmptyUri(0).cacheInMemory(true).showStubImage(R.color.trans).cacheOnDisk(true).resetViewBeforeLoading(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Config.ARGB_8888).delayBeforeLoading(100).postProcessor(new BitmapProcessor() {
            public Bitmap process(Bitmap bitmap) {
                return Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            }
        }).displayer(new SimpleBitmapDisplayer()).build());
        view.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (Helper.ModuleId == 1) {
                    Intent intent = new Intent(SelectVideoAdapter.this.e, VideoCutter.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("path", ((VideoData) SelectVideoAdapter.this.c.get(i)).videoPath);
                    SelectVideoAdapter.this.e.startActivity(intent);
                } else if (Helper.ModuleId == 2) {
                    Intent intent2 = new Intent(SelectVideoAdapter.this.e, VideoCompressor.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("videouri", ((VideoData) SelectVideoAdapter.this.c.get(i)).videoPath);
                    SelectVideoAdapter.this.e.startActivity(intent2);
                } else if (Helper.ModuleId == 3) {
                    Intent intent3 = new Intent(SelectVideoAdapter.this.e, VideoToMP3ConverterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("videopath", ((VideoData) SelectVideoAdapter.this.c.get(i)).videoPath);
                    intent3.putExtras(bundle);
                    SelectVideoAdapter.this.e.startActivity(intent3);
                }
            }
        });
        if (i % 2 == 0) {
            view.setBackgroundResource(R.drawable.divider_1);
        } else {
            view.setBackgroundResource(R.drawable.divider_2);
        }
        TextView textView = aVar.c;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(((VideoData) this.c.get(i)).videoName);
        textView.setText(sb.toString());
        TextView textView2 = aVar.b;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("");
        sb2.append(((VideoData) this.c.get(i)).Duration);
        textView2.setText(sb2.toString());
        return view;
    }

    public void filter(String str) {
        String lowerCase = str.toLowerCase(Locale.getDefault());
        this.c.clear();
        if (lowerCase.length() == 0) {
            this.c.addAll(this.b);
        } else {
            Iterator it = this.b.iterator();
            while (it.hasNext()) {
                VideoData videoData = (VideoData) it.next();
                if (videoData.videoName.toLowerCase(Locale.getDefault()).contains(lowerCase)) {
                    this.c.add(videoData);
                }
            }
        }
        notifyDataSetChanged();
    }
}
