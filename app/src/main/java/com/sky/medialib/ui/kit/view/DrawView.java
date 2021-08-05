package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AdaptScreenUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.sky.medialib.R;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.PixelUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class DrawView extends View {
    public final Double f9147a = Double.valueOf(57.29577951308232d);
    public final int f9148b = 2;
    private final int[] brushResList = new int[]{R.drawable.brush_huabi1, R.drawable.brush_huabi2};
    private int[] mStrokeWidthList;
    private float[][] f9151e;
    private Bitmap[] f9152f;
    private Bitmap[] mPaintBitmap;
    private Bitmap mSourceBitmap;
    private Canvas mCanvas;
    private Paint f9156j = new Paint();
    private Paint mPaint = new Paint();
    private Paint mCirclePaint = new Paint();
    private int mPaintStyle;
    private int normalWidthIndex = 2;
    private int earseWidthIndex = 2;
    private PointF mCenterPoint;
    private Canvas newCanvas;
    private onDrawChangeListener onDrawChangeListener;
    private Bitmap lastBitmap;
    private Bitmap f9166t;
    private ArrayList<Operate> mOperateList = new ArrayList();
    private Random random;
    private ReentrantLock lock = new ReentrantLock(true);
    private boolean isTouched;

    public interface onDrawChangeListener {
        void onChangeBitmap(Bitmap bitmap, Bitmap bitmap2);
        void aleadyBitmap(Bitmap bitmap, Bitmap bitmap2);
    }

    private class Operate {
        int paintStyle = -1;
        byte[] bitmapZipData;

        Operate(int i, byte[] bArr) {
            this.paintStyle = i;
            this.bitmapZipData = bArr;
        }
    }

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public DrawView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = i3 - i;
        int i6 = i4 - i2;
        if (this.f9166t == null || this.f9166t.getWidth() != i5 || this.f9166t.getHeight() != i6) {
            Bitmap createBitmap = Bitmap.createBitmap(i5, i6, Config.ARGB_4444);
            saveCurOperate(createBitmap);
            this.f9166t = createBitmap;
            if (this.onDrawChangeListener != null) {
                this.onDrawChangeListener.aleadyBitmap(createBitmap, this.mSourceBitmap);
            }
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.mCenterPoint != null) {
            canvas.drawCircle(this.mCenterPoint.x, this.mCenterPoint.y, this.mPaint.getStrokeWidth() / 2.0f, this.mCirclePaint);
        }
        super.onDraw(canvas);
    }

    private void init(Context context) {
        this.mStrokeWidthList = new int[]{PixelUtil.dip2px(6.0f), PixelUtil.dip2px(12.0f), PixelUtil.dip2px(18.0f), PixelUtil.dip2px(24.0f), PixelUtil.dip2px(30.0f)};
        this.mPaint.setDither(false);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        this.mPaint.setStrokeWidth((float) this.mStrokeWidthList[this.normalWidthIndex]);
        this.mCirclePaint.setColor(-1);
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Style.STROKE);
        this.mCirclePaint.setStrokeCap(Cap.ROUND);
        this.mCirclePaint.setStrokeJoin(Join.ROUND);
        this.mCirclePaint.setStrokeWidth((float) (this.mStrokeWidthList[this.normalWidthIndex] / 8));
        this.mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        this.f9152f = new Bitmap[this.brushResList.length];
        this.mPaintBitmap = new Bitmap[this.brushResList.length];
        this.f9151e = (float[][]) Array.newInstance(Float.TYPE, new int[]{this.brushResList.length, this.mStrokeWidthList.length});
        for (int i = 0; i < this.brushResList.length; i++) {
            Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), this.brushResList[i], null);
            this.f9152f[i] = decodeResource.extractAlpha();
            int[] iArr = new int[(decodeResource.getHeight() * decodeResource.getWidth())];
            decodeResource.getPixels(iArr, 0, decodeResource.getWidth(), 0, 0, decodeResource.getWidth(), decodeResource.getHeight());
            for (int i2 = 0; i2 < iArr.length; i2++) {
                if (iArr[i2] != 0) {
                    iArr[i2] = -1;
                }
            }
            this.mPaintBitmap[i] = Bitmap.createBitmap(iArr, decodeResource.getWidth(), decodeResource.getHeight(), Config.ARGB_8888);
            this.mPaintBitmap[i] = this.mPaintBitmap[i].extractAlpha();
            for (int i3 = 0; i3 < this.mStrokeWidthList.length; i3++) {
                this.f9151e[i][i3] = (((float) (this.mStrokeWidthList[i3] + 20)) * 1.0f) / ((float) this.mPaintBitmap[i].getHeight());
            }
        }
        this.random = new Random();
    }

    public void setBrushLevel(int i) {
        if (i > this.mStrokeWidthList.length - 1) {
            i = this.mStrokeWidthList.length - 1;
        } else if (i < 0) {
            i = 0;
        }
        if (this.mPaintStyle == -16777216) {
            this.earseWidthIndex = i;
        } else {
            this.normalWidthIndex = i;
        }
        this.mPaint.setStrokeWidth((float) this.mStrokeWidthList[i]);
        this.mCirclePaint.setStrokeWidth((float) (this.mStrokeWidthList[i] / 8));
    }

    public int getPaintSizeIndex() {
        if (this.mPaintStyle == -16777216) {
            return this.earseWidthIndex;
        }
        return this.normalWidthIndex;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mSourceBitmap = bitmap.copy(Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mSourceBitmap);
    }

    public Bitmap getDestBitmap() {
        return this.mSourceBitmap;
    }

    public void setPaintStyle(int i) {
        this.mPaintStyle = i;
        if (i == -16777216) {
            this.mPaint.setStrokeWidth((float) this.mStrokeWidthList[this.earseWidthIndex]);
            this.mCirclePaint.setStrokeWidth((float) (this.mStrokeWidthList[this.earseWidthIndex] / 8));
        } else {
            this.mPaint.setStrokeWidth((float) this.mStrokeWidthList[this.normalWidthIndex]);
            this.mCirclePaint.setStrokeWidth((float) (this.mStrokeWidthList[this.normalWidthIndex] / 8));
        }
        this.f9156j.setColor(i);
        this.mPaint.setColor(i);
    }

    public int getPaintStyle() {
        return this.mPaintStyle;
    }

    public String getBrushCodes() {
        ArrayList arrayList = new ArrayList();
        Iterator it = this.mOperateList.iterator();
        while (it.hasNext()) {
            Operate operate = (Operate) it.next();
            if (operate.paintStyle > 0) {
                arrayList.add(Integer.valueOf(operate.paintStyle));
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (arrayList.size() > 0) {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                stringBuilder.append(String.valueOf(((Integer) it2.next()) + ","));
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private Bitmap getTransformBitmap() {
        byte[] b = FileUtil.INSTANCE.unZipByteArray(((Operate) this.mOperateList.get(this.mOperateList.size() - 1)).bitmapZipData);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    private void saveCurOperate(final Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            new Thread() {
                public void run() {
                    OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] toByteArray = ((ByteArrayOutputStream) byteArrayOutputStream).toByteArray();
                    try {
                        byteArrayOutputStream.close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    byte[] a = FileUtil.INSTANCE.zipBitmapToByteArray(toByteArray);
                    DrawView.this.lock.lock();
                    try {
                        DrawView.this.mOperateList.add(new Operate(DrawView.this.mPaintStyle, a));
                    } finally {
                        DrawView.this.lock.unlock();
                        DrawView.this.isTouched = false;
                    }
                }
            }.start();
        }
    }

    public void undoLast() {
        if (this.mOperateList.size() > 1) {
            this.mOperateList.remove(this.mOperateList.size() - 1);
            this.lastBitmap = null;
            if (this.onDrawChangeListener != null) {
                this.onDrawChangeListener.onChangeBitmap(getTransformBitmap(), this.mSourceBitmap);
            }
        }
    }

    public void cancelAll() {
        while (this.mOperateList.size() > 1) {
            this.mOperateList.remove(this.mOperateList.size() - 1);
        }
        this.lastBitmap = null;
        if (this.onDrawChangeListener != null) {
            this.onDrawChangeListener.onChangeBitmap(getTransformBitmap(), this.mSourceBitmap);
        }
    }

    public void saveLastFrame() {
        if (this.mOperateList.size() > 1) {
            Operate operate = (Operate) this.mOperateList.get(this.mOperateList.size() - 1);
            this.mOperateList.clear();
            this.mOperateList.add(operate);
            this.lastBitmap = null;
        }
    }

    public boolean isEnable() {
        return this.mOperateList.size() > 1 || this.isTouched;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.isTouched = true;
                this.mCenterPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                if (this.mOperateList.isEmpty()) {
                    this.lastBitmap = this.f9166t.copy(Config.ARGB_8888, true);
                } else if (this.lastBitmap == null || this.lastBitmap.isRecycled()) {
                    this.lastBitmap = getTransformBitmap().copy(Config.ARGB_8888, true);
                }
                this.newCanvas = new Canvas(this.lastBitmap);
                break;
            case 1:
            case 3:
                if (!(this.lastBitmap == null || this.lastBitmap.isRecycled())) {
                    saveCurOperate(this.lastBitmap.copy(Config.ARGB_8888, true));
                }
                this.mCenterPoint = null;
                invalidate();
                break;
            case 2:
                if (this.mPaintStyle == -16777165 || this.mPaintStyle == -16777080) {
                    int i = 0;
                    if (this.mPaintStyle == -16777080) {
                        i = 1;
                    }
                    float atan = (float) (Math.atan((double) ((motionEvent.getY() - this.mCenterPoint.y) / (motionEvent.getX() - this.mCenterPoint.x))) * this.f9147a.doubleValue());
                    Matrix matrix = new Matrix();
                    matrix.postScale(this.f9151e[i][this.normalWidthIndex], this.f9151e[i][this.normalWidthIndex]);
                    matrix.postTranslate(motionEvent.getX() - ((((float) this.mPaintBitmap[i].getWidth()) * this.f9151e[i][this.normalWidthIndex]) / 2.0f), motionEvent.getY() - ((((float) this.mPaintBitmap[i].getHeight()) * this.f9151e[i][this.normalWidthIndex]) / 2.0f));
                    matrix.postRotate(atan, motionEvent.getX(), motionEvent.getY());
                    this.f9156j.setColor(getPixelValueByPoint(new PointF(motionEvent.getX(), motionEvent.getY())));
                    this.mCanvas.drawBitmap(this.f9152f[i], matrix, this.f9156j);
                    this.newCanvas.drawBitmap(this.mPaintBitmap[i], matrix, this.mPaint);
                    if (this.onDrawChangeListener != null) {
                        this.onDrawChangeListener.onChangeBitmap(this.lastBitmap, this.mSourceBitmap);
                    }
                } else {
                    this.newCanvas.drawLine(this.mCenterPoint.x, this.mCenterPoint.y, motionEvent.getX(), motionEvent.getY(), this.mPaint);
                    if (this.onDrawChangeListener != null) {
                        this.onDrawChangeListener.onChangeBitmap(this.lastBitmap, this.mSourceBitmap);
                    }
                }
                this.mCenterPoint.set(motionEvent.getX(), motionEvent.getY());
                invalidate();
                break;
        }
        return true;
    }

    private int getPixelValueByPoint(PointF pointF) {
        int i = 1;
        int nextInt = ((int) pointF.x) + (this.random.nextInt(11) - 5);
        int nextInt2 = ((int) pointF.y) + (this.random.nextInt(11) - 5);
        if (nextInt < 0) {
            nextInt = 1;
        } else if (nextInt > this.mSourceBitmap.getWidth() - 1) {
            nextInt = this.mSourceBitmap.getWidth() - 1;
        }
        if (nextInt2 >= 0) {
            if (nextInt2 > this.mSourceBitmap.getHeight() - 1) {
                i = this.mSourceBitmap.getHeight() - 1;
            } else {
                i = nextInt2;
            }
        }
        if (this.mSourceBitmap == null || this.mSourceBitmap.isRecycled()) {
            return 0;
        }
        return this.mSourceBitmap.getPixel(nextInt, i) & 1610612735;
    }

    public void setDrawListener(onDrawChangeListener onDrawChangeListener) {
        this.onDrawChangeListener = onDrawChangeListener;
    }
}
