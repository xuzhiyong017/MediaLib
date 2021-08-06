package com.sky.medialib.ui.kit.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.sky.media.image.core.extra.FpsTest;

public class PointsView extends View {

    private PointF[] drawPoints;
    private Rect showRect;
    private Paint paint = new Paint();
    private int curFps = 0;
    private boolean isShow;
    private FpsTest.FpsGetListener fpsListener = new FpsTest.FpsGetListener() {
        @Override
        public void onFpsGet(int i) {
            curFps = i;
            postInvalidate();
        }
    };

    public PointsView(Context context) {
        super(context);
        init();
    }

    public PointsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public PointsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
    }

    public void setPoints(PointF[] pointFArr) {
        this.drawPoints = pointFArr;
        postInvalidate();
    }

    public void needShowFps(boolean z) {
        isShow = z;
        if (isShow) {
            FpsTest.getInstance().addFpsListener(fpsListener);
        } else {
            FpsTest.getInstance().removeFpsListener(fpsListener);
        }
    }

    public void setRect(Rect rect) {
        showRect = rect;
        postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(-16711936);
        paint.setTextSize(40.0f);
        if (isShow) {
            canvas.drawText("Fps :" + curFps, 100.0f, 200.0f, paint);
        }
        if (drawPoints != null && drawPoints.length > 0) {
            paint.setStyle(Style.FILL);
            paint.setTextSize(28.0f);
            for (int i = 0; i < drawPoints.length; i++) {
                paint.setColor(-16711936);
                canvas.drawCircle(drawPoints[i].x, drawPoints[i].y, 3.0f, paint);
                paint.setColor(-65536);
                canvas.drawText("" + i, drawPoints[i].x + 3.0f, drawPoints[i].y, paint);
            }
        }
        if (showRect != null) {
            paint.setStyle(Style.STROKE);
            canvas.drawRect(showRect, paint);
        }
    }
}
