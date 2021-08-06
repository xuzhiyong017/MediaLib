package com.sky.medialib.ui.kit.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.sky.medialib.util.PixelUtil;

import java.util.ArrayList;
import java.util.Iterator;

public class RecordProgressView extends View {

    private final float MAX_TIME = 15200.0f;
    private final float MIN_TIME = 3200.0f;
    private Paint f9329c;
    private Paint mThresholdPaint;
    private Paint mBackgroundPaint;
    private Paint f9332f;
    private int mScreenWidth;
    private int mProgressHeight;
    private int mThresholdWidth;
    private int padding;
    private float mPerSecWidth;
    private float processWidth;
    private int threshold = 0;
    private float speed = 1.0f;
    private ArrayList<VideoSegment> mSegmentList = new ArrayList();
    private volatile long mCurrentTime;
    private volatile long mLastTime;
    private volatile State state = State.INIT;
    private OnRecordListener onRecordListener;
    private boolean isLongPress = false;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            RecordProgressView.this.invalidate();
            if (RecordProgressView.this.state == State.RECORDING) {
                RecordProgressView.this.mMainHandler.postDelayed(this, 40);
            } else {
                RecordProgressView.this.mMainHandler.removeCallbacks(RecordProgressView.this.drawRunnable);
            }
        }
    };

    public interface OnRecordListener {
        void disableVideo();

        void onIsRecordMin(boolean z);

        void onRecordEnd();

        void onRecordIdle();

        void onRecordPause();

        void onRecordStart();
    }

    private class VideoSegment {
        private long duration;
        private float speed;

        private VideoSegment() {
            this.speed = 1.0f;
        }

        VideoSegment(RecordProgressView recordProgressView) {
            this();
        }

        float getRealTime() {
            return ((float) this.duration) * this.speed;
        }
    }

    public enum State {
        INIT,
        RECORDING,
        PAUSE,
        CONCAT
    }

    public RecordProgressView(Context context) {
        super(context);
        init();
    }

    public RecordProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RecordProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        this.mPerSecWidth = (((float) this.mScreenWidth) * 1.0f) / MAX_TIME;
        this.f9329c = new Paint();
        this.f9329c.setStyle(Style.FILL);
        this.f9329c.setColor(Color.parseColor("#ffff5f5f"));
        this.mThresholdPaint = new Paint();
        this.mThresholdPaint.setStyle(Style.FILL);
        this.mThresholdPaint.setColor(Color.parseColor("#ffffff43"));
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setStyle(Style.FILL);
        this.mBackgroundPaint.setColor(Color.parseColor("#66000000"));
        this.f9332f = new Paint();
        this.f9332f.setStyle(Style.FILL);
        this.f9332f.setColor(Color.parseColor("#ffffffff"));
        this.mProgressHeight = PixelUtil.dip2px(6.0f);
        this.mThresholdWidth = PixelUtil.dip2px(5.0f);
        this.padding = PixelUtil.dip2px(2.0f);
        this.threshold = (int) ((((float) this.mScreenWidth) * MIN_TIME) / MAX_TIME);
    }

    public void setRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public void startRecording() {
        if (this.state == State.PAUSE || this.state == State.INIT) {
            this.state = State.RECORDING;
            this.onRecordListener.onRecordStart();
            this.mCurrentTime = System.currentTimeMillis();
            this.mMainHandler.post(this.drawRunnable);
        } else if (this.state == State.CONCAT) {
            this.onRecordListener.onRecordEnd();
        }
    }

    public void pauseRecord() {
        VideoSegment videoSegment = new VideoSegment(this);
        videoSegment.duration = mLastTime - mCurrentTime;
        videoSegment.speed = speed;
        mSegmentList.add(videoSegment);
        state = State.PAUSE;
        onRecordListener.onRecordPause();
        invalidate();
        if (videoSegment.getRealTime() < 350.0f) {
            onRecordListener.disableVideo();
        }
    }
    
    public void mo17906c() {
        state = State.PAUSE;
        invalidate();
    }

    public void reset() {
        this.state = State.INIT;
        this.mSegmentList.clear();
        invalidate();
    }

    public long removeLastSegment() {
        int size = this.mSegmentList.size();
        VideoSegment videoSegment = null;
        if (size > 0) {
            videoSegment = (VideoSegment) this.mSegmentList.remove(size - 1);
            invalidate();
            this.state = State.PAUSE;
        }
        if (this.mSegmentList.size() == 0) {
            this.onRecordListener.onRecordIdle();
            this.state = State.INIT;
        }
        if (videoSegment != null) {
            return videoSegment.duration;
        }
        return 0;
    }

    public void setSpeed(float f) {
        this.speed = f;
    }

    public State getState() {
        return this.state;
    }

    /* renamed from: f */
    public boolean canSave() {
        float time = 0.0f;
        if(mSegmentList != null && mSegmentList.size() > 0){
            for (int i = 0; i < mSegmentList.size(); i++) {
                time += mSegmentList.get(i).getRealTime();
            }
        }
        return time >= MIN_TIME;
    }

    public void setLongPressed(boolean z) {
        this.isLongPress = z;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (state) {
            case INIT:
                drawInitStatus(canvas);
                return;
            case RECORDING:
            case CONCAT:
                drawRecordingStatus(canvas);
                return;
            case PAUSE:
                drawPauseStatus(canvas);
                return;
            default:
                return;
        }
    }

    private void drawInitStatus(Canvas canvas) {
        processWidth = 0.0f;
        canvas.drawRect(0.0f, 0.0f, (float) mScreenWidth, (float) mProgressHeight, mBackgroundPaint);
        canvas.drawRect((float) threshold, 0.0f, (float) (threshold + mThresholdWidth), (float) mProgressHeight, mThresholdPaint);
    }

    private void drawRecordingStatus(Canvas canvas) {
        VideoSegment videoSegment;
        processWidth = 0.0f;
        if (mSegmentList.size() == 0) {
            processWidth = 0.0f;
        } else {
            Iterator it = mSegmentList.iterator();
            while (it.hasNext()) {
                videoSegment = (VideoSegment) it.next();
                processWidth = videoSegment.getRealTime() + processWidth;
            }
        }
        mLastTime = mCurrentTime;
        if (state == State.RECORDING || state == State.CONCAT) {
            mLastTime = System.currentTimeMillis();
        }
        long round = (long) Math.round(((float) (this.mLastTime - this.mCurrentTime)) * this.speed);
        int round2 = Math.round((this.processWidth + ((float) round)) * this.mPerSecWidth);
        if (round2 < this.mScreenWidth) {
            canvas.drawRect((float) round2, 0.0f, (float) this.mScreenWidth, (float) this.mProgressHeight, this.mBackgroundPaint);
        }
        canvas.drawRect((float) this.threshold, 0.0f, (float) (this.threshold + this.mThresholdWidth), (float) this.mProgressHeight, this.mThresholdPaint);
        if (round2 < this.mScreenWidth) {
            canvas.drawRect(0.0f, 0.0f, (float) round2, (float) this.mProgressHeight, this.f9329c);
        } else {
            canvas.drawRect(0.0f, 0.0f, (float) this.mScreenWidth, (float) this.mProgressHeight, this.f9329c);
        }
        int size = this.mSegmentList.size();
        if (size > 0) {
            round2 = 0;
            float f = 0.0f;
            while (round2 < size && (round2 != size - 1 || this.state != State.CONCAT)) {
                float a = f + ((VideoSegment) this.mSegmentList.get(round2)).getRealTime();
                float f2 = this.mPerSecWidth * a;
                canvas.drawRect(f2 - ((float) (this.padding / 2)), 0.0f, ((float) (this.padding / 2)) + f2, (float) this.mProgressHeight, this.f9332f);
                round2++;
                f = a;
            }
        }
        if (this.state == State.RECORDING && isMaxDurtion((float) round)) {
            this.state = State.CONCAT;
            if (!this.isLongPress) {
                videoSegment = new VideoSegment(this);
                videoSegment.duration = round;
                videoSegment.speed = this.speed;
                this.mSegmentList.add(videoSegment);
            }
            this.onRecordListener.onRecordEnd();
        }
        if (isMinDurtion((float) round)) {
            this.onRecordListener.onIsRecordMin(true);
        } else {
            this.onRecordListener.onIsRecordMin(false);
        }
    }

    private void drawPauseStatus(Canvas canvas) {
        this.processWidth = 0.0f;
        if (this.mSegmentList.size() == 0) {
            this.processWidth = 0.0f;
        } else {
            Iterator it = this.mSegmentList.iterator();
            while (it.hasNext()) {
                VideoSegment videoSegment = (VideoSegment) it.next();
                this.processWidth = videoSegment.getRealTime() + this.processWidth;
            }
        }
        int round = Math.round(this.processWidth * this.mPerSecWidth);
        if (round < this.mScreenWidth) {
            canvas.drawRect((float) round, 0.0f, (float) this.mScreenWidth, (float) this.mProgressHeight, this.mBackgroundPaint);
        }
        canvas.drawRect((float) this.threshold, 0.0f, (float) (this.threshold + this.mThresholdWidth), (float) this.mProgressHeight, this.mThresholdPaint);
        if (round < this.mScreenWidth) {
            canvas.drawRect(0.0f, 0.0f, (float) round, (float) this.mProgressHeight, this.f9329c);
        } else {
            canvas.drawRect(0.0f, 0.0f, (float) this.mScreenWidth, (float) this.mProgressHeight, this.f9329c);
        }
        int size = this.mSegmentList.size();
        if (size > 0) {
            round = 0;
            float f = 0.0f;
            while (round < size - 1) {
                float a = f + ((VideoSegment) this.mSegmentList.get(round)).getRealTime();
                float f2 = this.mPerSecWidth * a;
                canvas.drawRect(f2 - ((float) (this.padding / 2)), 0.0f, ((float) (this.padding / 2)) + f2, (float) this.mProgressHeight, this.f9332f);
                round++;
                f = a;
            }
        }
    }

    public long getDuration() {
        long time = 0;
        if(mSegmentList != null && mSegmentList.size() > 0){
            for (int i = 0; i < mSegmentList.size() ; i++) {
                time += mSegmentList.get(i).getRealTime();
            }
        }
        return time;
    }

    public boolean isMaxDurtion(float f) {
        Iterator it = this.mSegmentList.iterator();
        while (it.hasNext()) {
            f += ((VideoSegment) it.next()).getRealTime();
        }
        return f >= MAX_TIME;
    }

    private boolean isMinDurtion(float f) {
        Iterator it = this.mSegmentList.iterator();
        while (it.hasNext()) {
            f += ((VideoSegment) it.next()).getRealTime();
        }
        return f >= MIN_TIME;
    }
}
