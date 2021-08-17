package com.sky.medialib.ui.editvideo.effect.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sky.medialib.R;
import com.sky.medialib.ui.kit.model.VideoEffect;
import com.sky.medialib.ui.kit.view.VideoEffectIconView;
import com.sky.medialib.ui.kit.view.circlegif.CircleGifDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoEffectAdapter extends RecyclerView.Adapter<VideoEffectAdapter.VideoEffectHolder> {
    private Context mContext;
    private List<VideoEffect> mEffectList = new ArrayList();
    private HashMap<VideoEffect, CircleGifDrawable> mGifCache = new HashMap();
    private int mSelectedPosition = -1;

    public class VideoEffectHolder  extends RecyclerView.ViewHolder {
        @BindView(R.id.effect_icon)
        VideoEffectIconView mEffectIcon;
        @BindView(R.id.effect_loading)
        ProgressBar mEffectLoading;
        @BindView(R.id.effect_name)
        TextView mEffectName;
        @BindView( R.id.effect_reload)
        ImageView mEffectReload;

        VideoEffectHolder(View view) {
            super(view);
            ButterKnife.bind( this, view);
        }
    }

    public VideoEffectAdapter(Context context) {
        this.mContext = context;
    }

    public VideoEffect getVideoEffect(int i) {
        return (VideoEffect) this.mEffectList.get(i);
    }

    public void setVideoEffectList(List<VideoEffect> list) {
        this.mEffectList.clear();
        this.mEffectList.addAll(list);
        for (VideoEffect videoEffect : list) {
            if (((CircleGifDrawable) this.mGifCache.get(videoEffect)) == null) {
                try {
                    CircleGifDrawable circleGifDrawable = new CircleGifDrawable(this.mContext.getResources(), videoEffect.getFilter().getIconResource());
                    circleGifDrawable.getPaint().setAntiAlias(true);
                    circleGifDrawable.setLoopCount(32767);
                    this.mGifCache.put(videoEffect, circleGifDrawable);
                } catch (java.lang.Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int i) {
        this.mSelectedPosition = i;
        notifyDataSetChanged();
    }

    @Override
    public VideoEffectHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new VideoEffectHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_video_effect, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(VideoEffectHolder videoEffectHolder, int i) {
        VideoEffect videoEffect = (VideoEffect) this.mEffectList.get(i);
        videoEffectHolder.mEffectIcon.setVideoEffect(videoEffect);
        CircleGifDrawable circleGifDrawable = (CircleGifDrawable) this.mGifCache.get(videoEffect);
        videoEffectHolder.mEffectIcon.setImageDrawable(circleGifDrawable);
        videoEffectHolder.mEffectIcon.setDrawMaskBitmap(true);
        videoEffectHolder.mEffectLoading.setVisibility(View.GONE);
        videoEffectHolder.mEffectReload.setVisibility(View.GONE);
        if (this.mSelectedPosition == -1) {
            videoEffectHolder.mEffectIcon.setHighlight(false);
            if (circleGifDrawable != null) {
                circleGifDrawable.setAlpha(127);
            }
            videoEffectHolder.mEffectName.setAlpha(0.5f);
        } else if (this.mSelectedPosition == i) {
            if (videoEffect.isReverse()) {
                switch (videoEffect.getReverseState()) {
                    case 1:
                        videoEffectHolder.mEffectIcon.setDrawMaskBitmap(false);
                        videoEffectHolder.mEffectLoading.setVisibility(View.VISIBLE);
                        videoEffectHolder.mEffectReload.setVisibility(View.GONE);
                        break;
                    case 2:
                        videoEffectHolder.mEffectIcon.setDrawMaskBitmap(false);
                        videoEffectHolder.mEffectLoading.setVisibility(View.GONE);
                        videoEffectHolder.mEffectReload.setVisibility(View.VISIBLE);
                        break;
                }
            }
            videoEffectHolder.mEffectIcon.setHighlight(true);
            if (circleGifDrawable != null) {
                circleGifDrawable.setAlpha(255);
            }
            videoEffectHolder.mEffectName.setAlpha(1.0f);
        } else {
            videoEffectHolder.mEffectIcon.setHighlight(false);
            if (circleGifDrawable != null) {
                circleGifDrawable.setAlpha(127);
            }
            videoEffectHolder.mEffectName.setAlpha(0.5f);
        }
        videoEffectHolder.mEffectName.setText(videoEffect.getFilter().getName());
    }

    public int getItemCount() {
        return this.mEffectList.size();
    }
}
