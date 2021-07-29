package com.sky.media.image.core.view;

import android.content.Context;
import android.util.AttributeSet;

import com.sky.media.image.core.pipeline.RenderPipeline;

public class ProcessTextureView extends GLTextureView implements IRenderView {
    private RenderPipeline mPipeline;

    public ProcessTextureView(Context context) {
        super(context);
        setDebugFlags(3);
        setEGLContextClientVersion(2);
    }

    public ProcessTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDebugFlags(3);
        setEGLContextClientVersion(2);
    }

    public RenderPipeline initPipeline() {
        if (this.mPipeline == null) {
            this.mPipeline = new RenderPipeline();
            setRenderer(this.mPipeline);
            setRenderMode(0);
        }
        return this.mPipeline;
    }
}
