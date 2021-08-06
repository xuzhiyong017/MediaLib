package com.sky.media.image.core.view;

import android.content.Context;
import android.util.AttributeSet;

import com.sky.media.image.core.pipeline.RenderPipeline;


public class ProcessSurfaceView extends GLSurfaceView implements IRenderView {
    private RenderPipeline mPipeline;

    public ProcessSurfaceView(Context context) {
        this(context, null);
        setDebugFlags(3);
        setEGLContextClientVersion(2);
    }

    public ProcessSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDebugFlags(3);
        setEGLContextClientVersion(2);
    }

    public RenderPipeline initPipeline() {
        if (this.mPipeline == null) {
            this.mPipeline = new RenderPipeline();
            setRenderer(this.mPipeline);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        return this.mPipeline;
    }
}
