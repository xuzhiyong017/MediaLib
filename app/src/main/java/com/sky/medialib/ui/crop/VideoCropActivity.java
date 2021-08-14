package com.sky.medialib.ui.crop;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.sky.media.ffmpeg.executor.FFmpegExecutor;
import com.sky.media.kit.mediakit.MediaKit;
import com.sky.media.kit.mediakit.MediaKitCompat;
import com.sky.media.kit.mediakit.Metadata;
import com.sky.medialib.R;
import com.sky.medialib.ui.dialog.SimpleAlertDialog;
import com.sky.medialib.ui.editvideo.VideoEditActivity;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.common.view.TextureVideoView;
import com.sky.medialib.ui.kit.model.PublishVideo;
import com.sky.medialib.ui.kit.view.VideoCropBar;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.MD5Util;
import com.sky.medialib.util.NumberUtil;
import com.sky.medialib.util.Storage;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.Util;
import com.sky.medialib.util.task.SimpleTask;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoCropActivity extends AppActivity {
    private static final int AVG_NUM = 6;
    public static final String KEY_TOPIC_NAME = "KEY_TOPIC_NAME";
    public static final String KEY_VIDEO = "key_video";
    private static final int TOTAL = 30;
    @BindView(R.id.crop_bar)
    VideoCropBar mCropBar;
    @BindView(R.id.crop_frame_layout)
    View mCropLayout;
    @BindView(R.id.crop_cancel)
    View mCancelView;
    @BindView(R.id.crop_done)
    View mDownlView;
    @BindView(R.id.crop_tip)
    TextView mCutTimeTip;
    private AtomicBoolean mDestroy = new AtomicBoolean(false);
    private FrameAdapter mFrameAdapter;
    @BindView(R.id.crop_frame_list)
    RecyclerView mFrameRecycler;
    private int mFrameSize = 0;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            long currentPosition = VideoCropActivity.this.mVideoView.getCurrentPosition();
            int scrollXDistance = VideoCropActivity.this.getScrollXDistance();
            if (currentPosition >= ((long) Math.round(VideoCropActivity.this.mCropBar.mo17871b(scrollXDistance)))) {
                VideoCropActivity.this.mVideoView.seekTo(Math.round(VideoCropActivity.this.mCropBar.getStartTime(scrollXDistance)));
            }
            if (VideoCropActivity.this.mVideoView.isPlaying()) {
                VideoCropActivity.this.mVideoView.postDelayed(this, 1000);
            }
        }
    };
    private SimpleTask mSimpleTask;
    private String mTopicName;
    private String mVideoPath;
    @BindView(R.id.crop_video_view)
    TextureVideoView mVideoView;


    class CropTask extends SimpleTask {

        String mVideoPath = "";

        CropTask() {
        }

        protected void onPreExecute() {
            showProgressDialog(R.string.combining);
        }

        protected void doInBackground() {
            String str;
            boolean z = false;
            boolean z2 = true;
            int scrollXDistance = getScrollXDistance();
            float startDuration = NumberUtil.valueOffloat(VideoCropActivity.this.mCropBar.getStartTime(scrollXDistance) * 0.001f, 1);
            float endDuration = NumberUtil.valueOffloat(VideoCropActivity.this.mCropBar.mo17871b(scrollXDistance) * 0.001f, 1);
            String videoPath = VideoCropActivity.this.mVideoPath;
            Metadata metadata = MediaKit.getVideoInfo(videoPath);
            float time = NumberUtil.valueOffloat(endDuration - startDuration, 1);
            if (((double) (metadata.duration * 0.001f - time)) >= 0.5d) {
                FFmpegExecutor fFmpegExecutor = FFmpegExecutor.getInstance(VideoCropActivity.this);
                String tempPath = VideoCropActivity.this.createTempPath();
                boolean z3 = fFmpegExecutor.cutVideo(videoPath, tempPath, startDuration, time) == 0;
                if (((float) MediaKit.getVideoDuration(tempPath)) * 1.0E-6f > time + 1.0f) {
                    String tempPath1 = VideoCropActivity.this.createTempPath();
                    if (z3 && fFmpegExecutor.setKeyFrameAndGop(tempPath, tempPath1, 0.0f, time, 10, 25) == 0) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    String tempPath2 = VideoCropActivity.this.createTempPath();
                    if (z3 && fFmpegExecutor.cutVideo(tempPath1, tempPath2, 0.0f, time) == 0) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    if (z3) {
                        FileUtil.INSTANCE.deleteFiles(tempPath);
                        FileUtil.INSTANCE.deleteFiles(tempPath1);
                        str = tempPath2;
                    } else {
                        str = videoPath;
                    }
                    videoPath = str;
                } else {
                    videoPath = tempPath;
                }
                if (!z3) {
                    return;
                }
            }
            float a6 = NumberUtil.valueOffloat((((float) 5120000) * 1.0f) / ((float) metadata.bitrate), 2);
            if (metadata.bitrate > 5120000 || metadata.width > 720 || metadata.height > 1280 || metadata.rotation != 0) {
                z = true;
            }
            if (z) {
                str = VideoCropActivity.this.createTempPath();
                z2 = MediaKitCompat.cropVideo(videoPath, str, a6, true, VideoCropActivity.this);
                if (z2) {
                    this.mVideoPath = str;
                }
            } else {
                this.mVideoPath = videoPath;
            }
            if (!(VideoCropActivity.this.mVideoPath.equals(videoPath) || videoPath.equals(this.mVideoPath))) {
                FileUtil.INSTANCE.deleteFiles(videoPath);
            }
            if (z2) {
                str = this.mVideoPath;
            } else {
                str = "";
            }
            this.mVideoPath = str;
        }

        protected void onPostExecute() {
            VideoCropActivity.this.dismissProgressDialog();
            if (TextUtils.isEmpty(this.mVideoPath) || !new File(this.mVideoPath).exists()) {
                ToastUtils.INSTANCE.showToast(R.string.crop_failed);
            } else {
                VideoCropActivity.this.jumpEditPage(this.mVideoPath);
            }
        }
    }

    public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.ViewHolder> {
        private ArrayList<Frame> mFrames;
        private LayoutParams mParams;

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.frame)
            ImageView frame;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind((Object) this, view);
            }
        }


        FrameAdapter(VideoCropActivity videoCropActivity) {
            this();
        }

        private FrameAdapter() {
            this.mFrames = new ArrayList();
            this.mParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void addFrame(Frame frame) {
            this.mFrames.add(frame);
            notifyItemRangeChanged(this.mFrames.size() - 2, 2);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_frame_image, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Frame frame = (Frame) this.mFrames.get(i);
            this.mParams.width = frame.width;
            this.mParams.height = frame.frameSize;
            if (i == 0) {
                this.mParams.leftMargin = VideoCropActivity.this.mFrameSize / 2;
                this.mParams.rightMargin = 0;
                viewHolder.frame.setLayoutParams(this.mParams);
            } else if (i == getItemCount() - 1) {
                this.mParams.leftMargin = 0;
                this.mParams.rightMargin = VideoCropActivity.this.mFrameSize / 2;
                viewHolder.frame.setLayoutParams(this.mParams);
            } else {
                this.mParams.leftMargin = 0;
                this.mParams.rightMargin = 0;
                viewHolder.frame.setLayoutParams(this.mParams);
            }
            Glide.with(viewHolder.frame).load(frame.path).into(viewHolder.frame);
        }

        public int getItemCount() {
            return this.mFrames.size();
        }
    }

    private class Frame {
        String path;
        int width;
        int frameSize;

        private Frame() {
        }

    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_crop);
        ButterKnife.bind((Activity) this);
        int a = ScreenUtils.getScreenWidth();
        this.mFrameSize = a / 6;
        this.mCropLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, this.mFrameSize));
        this.mCropBar.setCropImageSize(a, this.mFrameSize);
        this.mCropBar.setOnCropChangeListener(new VideoCropBar.OnCropChangeListener() {
            @Override
            public void onStopScroll() {
                VideoCropActivity.this.seek();
            }

            @Override
            public void onSelectChange(float f) {
                int i = 30;
                int round = (int) Math.round(((double) f) * 0.001d);
                if (round <= 30) {
                    i = round;
                }
                VideoCropActivity.this.mCutTimeTip.setText(VideoCropActivity.this.getString(R.string.format_select_time, new Object[]{Integer.valueOf(i)}));
            }
        });
        this.mFrameAdapter = new FrameAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        this.mFrameRecycler.setLayoutManager(linearLayoutManager);
        this.mFrameRecycler.setAdapter(this.mFrameAdapter);
        ((SimpleItemAnimator) this.mFrameRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        this.mFrameRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 0 && VideoCropActivity.this.mFrameAdapter.getItemCount() > 6) {
                    VideoCropActivity.this.seek();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        this.mVideoView.setLooping(true);
        this.mVideoView.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int i, int i2) {
                if (((float) i2) > ((float) i) * 1.5f) {
                    mVideoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
                } else {
                   mVideoView.setScaleType(TextureVideoView.ScaleType.CENTER);
                }
            }
        });
        this.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.postDelayed(mRunnable, 1000);
            }
        });
        this.mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.removeCallbacks(mRunnable);
                SimpleAlertDialog.newBuilder(VideoCropActivity.this)
                        .setCancleable(false)
                        .setMessage(R.string.video_error, 17)
                        .setRightBtn(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).build().show();
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.play();
            }
        });
        String stringExtra = getIntent().getStringExtra(KEY_VIDEO);
        mTopicName = getIntent().getStringExtra("KEY_TOPIC_NAME");
        if (TextUtils.isEmpty(stringExtra)) {
            finish();
        } else {
            readVideo(stringExtra);
        }

        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDownlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mVideoPath)) {
                    cropVideo();
                }
            }
        });
    }


    private void seek() {
        mVideoView.seekTo((int) mCropBar.getStartTime(getScrollXDistance()));
    }

    private void readVideo(final String str) {
        final String videoPath = str;
        mSimpleTask = new SimpleTask() {
            protected void onPreExecute() {
                showProgressDialog(R.string.loading);
            }

            protected void doInBackground() {
                String str = Storage.getFilePathByType(3) + MD5Util.encodeString(videoPath);
                String str2 = str + ".mp4";
                File file = new File(str2);
                boolean z = true;
                if (!(file.exists() && file.isFile())) {
                    z = FileUtil.INSTANCE.copy(videoPath, str2);
                }
                if (z) {
                    mVideoPath = str2;
                    startPlay();
                    extractThumbFrame(str);
                }
            }

            protected void onPostExecute() {
                dismissProgressDialog();
            }
        }.execute();
    }

    private void startPlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                if (!TextUtils.isEmpty(mVideoPath)) {
                    mVideoView.setDataSource(mVideoPath);
                    mVideoView.play();
                }
            }
        });
    }

    private void extractThumbFrame(final String str) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mVideoPath);
        long j = (long) 6000000;
        int videoDuration = NumberUtil.valueOfInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int numCount = videoDuration / 6000;
        float f = (((float) 6000) * 1.0f) / ((float) mFrameSize);
        int a = mCropBar.setup(Math.round(((float) videoDuration) / f), f, mFrameSize / 2);
        mCropBar.setSlideEnable(videoDuration > 3500);
        int i2 = 0;
        for (int i = 0; i <= numCount && !mDestroy.get(); i++) {
            long j2 = i * j;
            int frameSize = mFrameSize;
            if (videoDuration <= 30000) {
                if (a < i2 + frameSize) {
                    frameSize = a - i2;
                    if (frameSize <= 0) {
                        break;
                    }
                }
                i2 += frameSize;
            } else if (j2 + j > ((long) (videoDuration * 1000))) {
                frameSize = Math.round((((float) frameSize) * ((((float) videoDuration) * 1000.0f) - ((float) j2))) / ((float) j));
            }
            if (frameSize <= 0) {
                break;
            }
            final String str2 = str + "_" + j2 + ".jpg";
            Bitmap extractThumbnail = ThumbnailUtils.extractThumbnail(mediaMetadataRetriever.getFrameAtTime(j2, MediaMetadataRetriever.OPTION_CLOSEST_SYNC), frameSize, mFrameSize, 2);
            Storage.saveBitmapToFile(extractThumbnail, str2, null, 0, false);
            final int finalFrameSize = frameSize;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Frame frame = new Frame();
                    frame.path = str2;
                    frame.width = finalFrameSize;
                    frame.frameSize = mFrameSize;
                    mFrameAdapter.addFrame(frame);
                }
            });
        }
        mediaMetadataRetriever.release();
    }

    protected void onResume() {
        super.onResume();
        mVideoView.play();
    }

    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    protected void onStop() {
        super.onStop();
        mVideoView.removeCallbacks(mRunnable);
        mVideoView.stop();
    }

    protected void onDestroy() {
        mDestroy.set(true);
        if (mSimpleTask != null) {
            mSimpleTask.cancel();
            mSimpleTask = null;
        }
        mVideoView.release();
        super.onDestroy();
    }

    protected boolean isSupportSwipeBack() {
        return false;
    }

    private void cropVideo() {
        new CropTask().execute();
    }

    private String createTempPath() {
        return mVideoPath.replace(".mp4", "_" + Util.getCurTime() + ".mp4");
    }

    private void jumpEditPage(String str) {
        Intent intent = new Intent(this, VideoEditActivity.class);
        PublishVideo publishVideo = new PublishVideo();
        publishVideo.setVideoPath(str);
        intent.putExtra(VideoCropActivity.KEY_VIDEO, publishVideo);
        intent.putExtra("key_topic_name", mTopicName);
        startActivity(intent);
        finish();
    }

    private int getScrollXDistance() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mFrameRecycler.getLayoutManager();
        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        View findViewByPosition = linearLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
        if (findViewByPosition == null) {
            return 0;
        }
        int width = findViewByPosition.getWidth();
        int decoratedLeft = linearLayoutManager.getDecoratedLeft(findViewByPosition);
        if (decoratedLeft > 0) {
            return findFirstVisibleItemPosition * width;
        }
        return (findFirstVisibleItemPosition * width) - decoratedLeft;
    }

    public String getPageId() {
        return "10000600";
    }
}
