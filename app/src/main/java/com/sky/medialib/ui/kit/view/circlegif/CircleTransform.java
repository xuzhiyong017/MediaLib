package com.sky.medialib.ui.kit.view.circlegif;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

import pl.droidsonroids.gif.transforms.Transform;


/**
 * @author: xuzhiyong
 * @date: 2021/8/16  下午4:35
 * @Email: 18971269648@163.com
 * @description:
 */
public class CircleTransform implements Transform {

    private Rect rect;
    private Rect dstRect;
    private BitmapShader bitmapShader;
    private Matrix matrix;

    public CircleTransform(int intrinsicWidth, int intrinsicHeight) {
        rect = new Rect(0,0,intrinsicWidth,intrinsicHeight);
    }

    @java.lang.Override
    public void onBoundsChange(Rect rect) {
        dstRect = rect;
        float f;
        float f2;
        float f3 = 0.0f;
        this.matrix = new Matrix();
        int width = this.rect.width();
        int height = this.rect.height();
        int width2 = rect.width();
        int height2 = rect.height();
        if (width * height2 > width2 * height) {
            f = ((float) height2) / ((float) height);
            f2 = (((float) width2) - (((float) width) * f)) * 0.5f;
        } else {
            f = ((float) width2) / ((float) width);
            f2 = 0.0f;
            f3 = (((float) height2) - (((float) height) * f)) * 0.5f;
        }
        this.matrix.setScale(f, f);
        this.matrix.postTranslate((float) ((int) (f2 + 0.5f)), (float) ((int) (f3 + 0.5f)));
        if (this.bitmapShader != null) {
            this.bitmapShader.setLocalMatrix(this.matrix);
        }
    }

    @java.lang.Override
    public void onDraw(Canvas canvas, Paint paint, Bitmap bitmap) {
        this.bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (this.matrix != null) {
            this.bitmapShader.setLocalMatrix(this.matrix);
        }
        paint.setShader(this.bitmapShader);
        canvas.drawCircle((float) (dstRect.width() / 2), (float) (dstRect.height() / 2), (float) (dstRect.width() / 2),paint);
    }
}
