package com.sky.medialib.ui.kit.view;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


import com.sky.media.image.core.util.LogUtils;
import com.sky.media.kit.player.IMediaPlayer;
import com.sky.media.image.core.filter.Filter;
import com.sky.medialib.ui.kit.view.SequenceSeekBar.*;

import java.util.Timer;
import java.util.TimerTask;

public class SequenceSeekBarHelper {

    private static int UPDATE_TIME = 10;
    private SequenceSeekBar sequenceSeekBar;
    private SequenceExt sequenceExt;
    private OnSequenceSeekListener listener;
    private Timer timer;
    private IMediaPlayer mediaPlayer;
    private boolean isReverse;

    public interface OnSequenceSeekListener {
        void onStart(SequenceSeekBar sequenceSeekBar);

        void onProgressChanged(SequenceSeekBar sequenceSeekBar, int i, boolean z);

        void onStop(SequenceSeekBar sequenceSeekBar);
    }

    class OnSeekBarChangeListenerEx implements OnSeekBarChangeListener {
        OnSeekBarChangeListenerEx() {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (z && SequenceSeekBarHelper.this.mediaPlayer != null) {
                if (SequenceSeekBarHelper.this.isReverse) {
                    SequenceSeekBarHelper.this.mediaPlayer.seekTo((SequenceSeekBarHelper.this.sequenceSeekBar.getMax() - i) / 1000);
                } else {
                    SequenceSeekBarHelper.this.mediaPlayer.seekTo(i / 1000);
                }
            }
            SequenceSeekBarHelper.this.updateSequenceEndTime();
            if (SequenceSeekBarHelper.this.listener != null) {
                SequenceSeekBarHelper.this.listener.onProgressChanged(SequenceSeekBarHelper.this.sequenceSeekBar, i, z);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (SequenceSeekBarHelper.this.listener != null) {
                SequenceSeekBarHelper.this.listener.onStart(SequenceSeekBarHelper.this.sequenceSeekBar);
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (SequenceSeekBarHelper.this.listener != null) {
                SequenceSeekBarHelper.this.listener.onStop(SequenceSeekBarHelper.this.sequenceSeekBar);
            }
        }
    }

    class TimerTaskExt extends TimerTask {
        TimerTaskExt() {
        }

        public void run() {
            SequenceSeekBarHelper.this.updateProcess();
        }
    }

    public SequenceSeekBarHelper(SequenceSeekBar sequenceSeekBar, OnSequenceSeekListener onSequenceSeekListener) {
        this.sequenceSeekBar = sequenceSeekBar;
        this.listener = onSequenceSeekListener;
        this.sequenceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerEx());
    }

    public void setReversePlay(boolean z, int i) {
        this.isReverse = z;
        this.sequenceSeekBar.setReversePlay(z, i);
    }

    public boolean isReverse() {
        return this.isReverse;
    }

    public SequenceExt addFilter(Filter filter, int color) {
        if (this.mediaPlayer == null) {
            return null;
        }
        SequenceExt sequenceExt = new SequenceExt();
        sequenceExt.isReverse = this.isReverse;
        if (this.isReverse) {
            sequenceExt.start = (long) (this.sequenceSeekBar.getMax() - this.sequenceSeekBar.getProgress());
            sequenceExt.end = sequenceExt.start + ((long) UPDATE_TIME);
        } else {
            sequenceExt.start = (long) this.sequenceSeekBar.getProgress();
            sequenceExt.end = sequenceExt.start + ((long) UPDATE_TIME);
        }
        sequenceExt.total = (long) (this.mediaPlayer.getDuration() * 1000);
        sequenceExt.color = color;
        sequenceExt.filter.add(filter);
        this.sequenceSeekBar.push(sequenceExt);
        this.sequenceExt = sequenceExt;
        return sequenceExt;
    }

    public void endSequence() {
        updateSequenceEndTime();
        this.sequenceExt = null;
    }

    public boolean hasSequenceExt() {
        return this.sequenceExt != null;
    }

    public SequenceExt getTopSequence() {
        return this.sequenceSeekBar.pop();
    }

    public void setMediaPlayer(IMediaPlayer iMediaPlayer) {
        this.mediaPlayer = iMediaPlayer;
        this.sequenceSeekBar.setDuration(this.mediaPlayer.getDuration() * 1000);
    }

    private void updateSequenceEndTime() {
        if (this.mediaPlayer != null && this.sequenceExt != null) {
            if (this.isReverse) {
                this.sequenceExt.end = (long) ((this.sequenceSeekBar.getMax() - this.sequenceSeekBar.getProgress()) + UPDATE_TIME);
                return;
            }
            this.sequenceExt.end = (long) (this.sequenceSeekBar.getProgress() + UPDATE_TIME);
        }
    }

    int lastPos = 0;

    private void updateProcess() {
        if (this.mediaPlayer == null) {
            return;
        }
        if (this.isReverse) {
            this.sequenceSeekBar.setProgress((this.mediaPlayer.getDuration() - this.mediaPlayer.getCurrentPosition()) * 1000);
        } else {
            int cur = mediaPlayer.getCurrentPosition() * 1000;
            this.sequenceSeekBar.setProgress(cur);
            LogUtils.logd("UpdateProcess","process="+(lastPos - cur));
            lastPos = cur;        }
    }

    public void startTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer();
        this.timer.schedule(new TimerTaskExt(), 0, (long) UPDATE_TIME);
    }

    public void destroy() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = null;
    }
}
