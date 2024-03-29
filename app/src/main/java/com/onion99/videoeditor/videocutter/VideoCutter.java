package com.onion99.videoeditor.videocutter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.ReturnCode;
import com.onion99.videoeditor.Adclick;
import com.onion99.videoeditor.Ads;
import com.onion99.videoeditor.R;
import com.onion99.videoeditor.UtilCommand;
import com.onion99.videoeditor.VideoPlayer;
import com.onion99.videoeditor.VideoSliceSeekBar;
import com.onion99.videoeditor.VideoSliceSeekBar.SeekBarChangeListener;
import com.onion99.videoeditor.listvideoandmyvideo.ListVideoAndMyAlbumActivity;
import com.onion99.videoeditor.videojoiner.VideoJoinerActivity;
import com.onion99.videoeditor.videojoiner.model.VideoPlayerState;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressLint({"WrongConstant"})
public class VideoCutter extends AppCompatActivity implements MediaScannerConnectionClient, OnClickListener {
    public static Context AppContext = null;
    static final boolean k = true;
    MediaScannerConnection a;
    int b = 0;
    int c = 0;
    TextView d;
    TextView e;
    TextView f;
    TextView g;
    ImageView h;
    VideoSliceSeekBar i;
    VideoView j;
    private String l = "";
    private String m;

    public String n;

    public VideoPlayerState o = new VideoPlayerState();
    private a p = new a();
    private Ads ads;

    private class a extends Handler {
        private boolean b;
        private Runnable c;

        private a() {
            this.b = false;
            this.c = new Runnable() {
                public void run() {
                    a.this.a();
                }
            };
        }


        public void a() {
            if (!this.b) {
                this.b = VideoCutter.k;
                sendEmptyMessage(0);
            }
        }

        @Override
        public void handleMessage(Message message) {
            this.b = false;
            VideoCutter.this.i.videoPlayingProgress(VideoCutter.this.j.getCurrentPosition());
            if (!VideoCutter.this.j.isPlaying() || VideoCutter.this.j.getCurrentPosition() >= VideoCutter.this.i.getRightProgress()) {
                if (VideoCutter.this.j.isPlaying()) {
                    VideoCutter.this.j.pause();
                    VideoCutter.this.h.setBackgroundResource(R.drawable.play2);
                }
                VideoCutter.this.i.setSliceBlocked(false);
                VideoCutter.this.i.removeVideoStatusThumb();
                return;
            }
            postDelayed(this.c, 50);
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.videocutteractivity);
        LinearLayout s = (LinearLayout) findViewById(R.id.banner_AdView);
        ads = new Ads();
        ads.BannerAd(VideoCutter.this, s);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Video Cutter");
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (k || supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(k);
            supportActionBar.setDisplayShowTitleEnabled(false);
            AppContext = this;
            this.h = (ImageView) findViewById(R.id.buttonply1);
            this.i = (VideoSliceSeekBar) findViewById(R.id.seek_bar1);
            this.f = (TextView) findViewById(R.id.Filename);
            this.d = (TextView) findViewById(R.id.left_pointer);
            this.e = (TextView) findViewById(R.id.right_pointer);
            this.g = (TextView) findViewById(R.id.dur);
            this.j = (VideoView) findViewById(R.id.videoView1);
            this.h.setOnClickListener(this);
            this.m = getIntent().getStringExtra("path");
            if (this.m == null) {
                finish();
            }
            this.f.setText(new File(this.m).getName());
            this.j.setVideoPath(this.m);
            this.j.seekTo(100);
            e();
            this.j.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    VideoCutter.this.h.setBackgroundResource(R.drawable.play2);
                }
            });
            ads.Interstitialload(this);
            return;
        }
        throw new AssertionError();
    }


    public void b() {
        ads.showInd(new Adclick() {
            @Override
            public void onclicl() {
                c();
            }
        });
    }


    public void c() {
        File file = new File(n);
        MediaScannerConnection.scanFile(this, new String[]{file.getPath()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // now visible in gallery
                    }
                });
        Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("song", this.n);

        startActivity(intent);
        finish();
    }

    // ------------------------------------------------------------------------
    // 裁剪视频
    // ------------------------------------------------------------------------
    private void d() {
        // 创建输出目录
        String valueOf = String.valueOf(this.c);
        String valueOf2 = String.valueOf(this.b - this.c);
        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsoluteFile());
        sb.append("/");
        sb.append(getResources().getString(R.string.MainFolderName));
        sb.append("/");
        sb.append(getResources().getString(R.string.VideoCutter));
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        // 视频输出路径
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Environment.getExternalStorageDirectory().getAbsoluteFile());
        sb2.append("/");
        sb2.append(getResources().getString(R.string.MainFolderName));
        sb2.append("/");
        sb2.append(getResources().getString(R.string.VideoCutter));
        sb2.append("/videocutter");
        sb2.append(format);
        sb2.append(".mp4");
        this.n = sb2.toString();
        // 官网 https://www.ffmpeg.org/ffmpeg.html ，中文wiki https://zh.wikipedia.org/wiki/FFmpeg#%E8%81%B2%E9%9F%B3%E5%8F%83%E6%95%B8
        // -ss 为指定视频剪切开头的起始时间
        // -y 表示无需询问,直接覆盖输出文件
        // -i 输入文件,表示我要输入什么文件给ffmpeg ， Todo：一般我们用ffmpeg是以“输入→处理→输出”的流程来理解，来写命令行
        // -t duration，持续时间。通过“-t”参数来设定我们对音视频等媒体文件的编辑处理要持续多长时间
        // -to，stop，截止时间。通过“-to”参数来设定对媒体文件的编辑处理到什么时间点结束。需要这个用法的话，“-to”一定要写在“-i”前面！！否则就和“-t“一样是描述持续时间的了。
        //      “-ss”、“-t”或“-to”来进行简单剪辑。
        //      “-ss” 与 “-t” 这样是设定好起点和持续时间，“-t”所记录的时间就是最后输出文件的时长。
        //      “-ss” 与 “-to” 是设定好起点与终点，而不用想持续时间是多长
        // --vcodec 指定视频解码器  和-c:v 是等价的
        // -b:a 设定音频比特率 ,这里48khz， 详情看这 https://www.zhihu.com/question/20035259
        // -b:v 设定视频比特率 ,这里2.1Gbps ,详情看这  https://blog.csdn.net/qq_41176800/article/details/122738638?ops_request_misc=&request_id=&biz_id=102&utm_term=%E7%A0%81%E7%8E%87&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduweb~default-2-122738638.nonecase&spm=1018.2226.3001.4187
        // 码流、码率、采样率、比特率、帧速率、分辨率、高清视频的概念: https://developer.aliyun.com/article/311918?spm=a2c6h.17698244.wenzhang.1.7d0d6cf5IbBrgL
        // -ac 设置声音的通道数目,一般双通道
        // -ar 设置采样率  这里22.05khz：广播使用频率。
        a(new String[]{"-ss", valueOf, "-y", "-i", this.m, "-t", valueOf2, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.n}, this.n);
    }


    private void a(String[] strArr, final String str) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        String ffmpegCommand = UtilCommand.main(strArr);
        FFmpegKit.executeAsync(ffmpegCommand, new FFmpegSessionCompleteCallback() {
            @Override
            public void apply(FFmpegSession session) {
                Log.d("TAG", String.format("FFmpeg process exited with rc %s.", session.getReturnCode()));

                Log.d("TAG", "FFmpeg process output:");



                progressDialog.dismiss();
                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    progressDialog.dismiss();
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(VideoCutter.this.n)));
                    VideoCutter.this.sendBroadcast(intent);
                    VideoCutter.this.b();
                } else if (ReturnCode.isCancel(session.getReturnCode())) {
                    Log.d("ffmpegfailure", str);
                    try {
                        new File(str).delete();
                        VideoCutter.this.deleteFromGallery(str);
                        Toast.makeText(VideoCutter.this, "Error Creating Video", Toast.LENGTH_LONG).show();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                } else {
                    try {
                        new File(str).delete();
                        VideoCutter.this.deleteFromGallery(str);
                        Toast.makeText(VideoCutter.this, "Error Creating Video", Toast.LENGTH_LONG).show();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }

            }
        });
        getWindow().clearFlags(16);
    }

    private void e() {
        this.j.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                VideoCutter.this.i.setSeekBarChangeListener(new SeekBarChangeListener() {
                    public void SeekBarValueChanged(int i, int i2) {
                        if (VideoCutter.this.i.getSelectedThumb() == 1) {
                            VideoCutter.this.j.seekTo(VideoCutter.this.i.getLeftProgress());
                        }
                        VideoCutter.this.d.setText(VideoJoinerActivity.formatTimeUnit((long) i));
                        VideoCutter.this.e.setText(VideoJoinerActivity.formatTimeUnit((long) i2));
                        VideoCutter.this.o.setStart(i);
                        VideoCutter.this.o.setStop(i2);
                        VideoCutter.this.c = i / 1000;
                        VideoCutter.this.b = i2 / 1000;
                        TextView textView = VideoCutter.this.g;
                        StringBuilder sb = new StringBuilder();
                        sb.append("duration : ");
                        sb.append(String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) / 3600), Integer.valueOf(((VideoCutter.this.b - VideoCutter.this.c) % 3600) / 60), Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) % 60)}));
                        textView.setText(sb.toString());
                    }
                });
                VideoCutter.this.i.setMaxValue(mediaPlayer.getDuration());
                VideoCutter.this.i.setLeftProgress(0);
                VideoCutter.this.i.setRightProgress(mediaPlayer.getDuration());
                VideoCutter.this.i.setProgressMinDiff(0);
            }
        });
    }

    private void f() {
        if (this.j.isPlaying()) {
            this.j.pause();
            this.i.setSliceBlocked(false);
            this.h.setBackgroundResource(R.drawable.play2);
            this.i.removeVideoStatusThumb();
            return;
        }
        this.j.seekTo(this.i.getLeftProgress());
        this.j.start();
        this.i.videoPlayingProgress(this.i.getLeftProgress());
        this.h.setBackgroundResource(R.drawable.pause2);
        this.p.a();
    }

    @Override
    public void onClick(View view) {
        if (view == this.h) {
            f();
        }
    }

    public void onMediaScannerConnected() {
        this.a.scanFile(this.l, "video/*");
    }

    public void onScanCompleted(String str, Uri uri) {
        this.a.disconnect();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public void h() {
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("Device not supported").setMessage("FFmpeg is not supported on your device").setCancelable(false).setPositiveButton(R.string.alert_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                VideoCutter.this.finish();
            }
        }).create().show();
    }

    public void deleteFromGallery(String str) {
        String[] strArr = {"_id"};
        String[] strArr2 = {str};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, "_data = ?", strArr2, null);
        if (query.moveToFirst()) {
            try {
                contentResolver.delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow("_id"))), null, null);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                new File(str).delete();
                refreshGallery(str);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        query.close();
    }

    public void refreshGallery(String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(new File(str)));
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picker, menu);
        return k;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
            return k;
        }
        if (menuItem.getItemId() == R.id.Done) {
            if (this.j.isPlaying()) {
                this.j.pause();
                this.h.setBackgroundResource(R.drawable.play2);
            }
            d();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
