package com.sky.medialib.ui.camera.helper;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.out.BitmapOutput;
import com.sky.media.image.core.render.SwitchRender;
import com.sky.media.kit.model.FilterExt;
import com.sky.medialib.R;
import com.sky.medialib.ui.camera.adapter.CameraFiltersAdapter;
import com.sky.medialib.ui.camera.adapter.IRecycleViewItemClickListener;
import com.sky.medialib.ui.camera.process.CameraProcessExt;
import com.sky.medialib.ui.kit.filter.OriginNormalFilter;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.WeakHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraFilterBeautyHelper {
    private boolean isFlinging;
    private Activity activity;
    private OnFilterBeautyListener onFilterBeautyListener;
    private CameraProcessExt processExt;
    private GestureDetector gestureDetector;
    private int filterIndex;
    private View camera_beauty_tool;
    private RadioGroup beauty_level_group;
    private TextView current_filter_name;
    private RecyclerView filter_gallery;
    private CameraFiltersAdapter filtersAdapter;
    private TextView filter_btn;
    private TextView white_btn;
    private TextView buffing_btn;
    private AlphaAnimation updateNameAnimator = new AlphaAnimation(0.9f, 1.0f);
    private WeakHandler weakHandler = new WeakHandler();
    private int white_level = 0;
    private int buffing_level = 3;
    private int eye_level = 3;
    private int face_level = 2;
    private int mSelectType = 10000;
    private SwitchRender switchRender;
    private int flingDirection = -1;
    private float offsetX;
    private float downX;

    public interface OnFilterBeautyListener {
        CameraProcessExt getCameraProcess();
        void onSelectFilterId(int i);
    }


    class SimpleOnGestureListenerExt extends SimpleOnGestureListener {
        SimpleOnGestureListenerExt() {
        }

        public boolean onDown(MotionEvent motionEvent) {
            flingDirection = -1;
            downX = motionEvent.getX();
            isFlinging = false;
            return super.onDown(motionEvent);
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (switchRender != null) {
                if (flingDirection == -1) {
                    BaseRender b;
                    BaseRender b2;
                    List cacheFilterList = ToolFilterManager.INSTANCE.getCacheFilterList();
                    int e;
                    if (motionEvent2.getX() - downX < 0.0f) {
                        flingDirection = 0;
                        e = filterIndex + 1;
                        if (e > cacheFilterList.size() - 1) {
                            e = 0;
                        }
                        b = getRenderByIndex(filterIndex);
                        b2 = getRenderByIndex(e);
                        offsetX = (float) ScreenUtils.getScreenWidth();
                    } else {
                        flingDirection = 1;
                        e = filterIndex - 1;
                        if (e < 0) {
                            e = cacheFilterList.size() - 1;
                        }
                        b = getRenderByIndex(e);
                        b2 = getRenderByIndex(filterIndex);
                        offsetX = 0.0f;
                    }

                    final BaseRender left = b;
                    final BaseRender right = b2;
                    processExt.getIRenderView().queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            destroyRender(switchRender.setRenders(left, right));
                        }
                    });
                }
                offsetX = offsetX - f;
                switchRender.adjust((int) offsetX, 0, ScreenUtils.getScreenWidth());
                processExt.requestRender();
            }
            return super.onScroll(motionEvent, motionEvent2, f, f2);
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (Math.abs(f2) < Math.abs(f)) {
                isFlinging = true;
                if (f > 2000.0f) {
                    flingDirection = 1;
                } else if (f < -2000.0f) {
                    flingDirection = 0;
                }
            }
            return super.onFling(motionEvent, motionEvent2, f, f2);
        }
    }

    void destroyRender(ArrayList arrayList) {
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((BaseRender) it.next()).destroy();
            }
        }
    }

    public interface OnTakePhotoListener {
        void onCapturePhoto(Bitmap bitmap, Bitmap bitmap2, int orientation);
    }

    public CameraFilterBeautyHelper(Activity activity, OnFilterBeautyListener onFilterBeautyListener) {
        this.activity = activity;
        this.onFilterBeautyListener = onFilterBeautyListener;
        this.processExt = this.onFilterBeautyListener.getCameraProcess();
        init();
    }

    public int getWhiteLevel() {
        return this.white_level;
    }

    public int getBuffingLevel() {
        return this.buffing_level;
    }

    public int getEyeLevel() {
        return this.eye_level;
    }

    public int getFaceLevel() {
        return this.face_level;
    }

    private void init() {
        this.white_level = SPStaticUtils.getInt("key_camera_white_level", this.white_level);
        this.buffing_level = SPStaticUtils.getInt("key_camera_buffing_level", this.buffing_level);
        this.face_level = SPStaticUtils.getInt("key_camera_face_level", this.face_level);
        this.eye_level = SPStaticUtils.getInt("key_camera_eye_level", this.eye_level);
        this.camera_beauty_tool = this.activity.findViewById(R.id.camera_beauty_tool);
        this.camera_beauty_tool.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.current_filter_name = (TextView) this.activity.findViewById(R.id.current_filter_name);
        this.current_filter_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        this.filter_gallery = (RecyclerView) this.activity.findViewById(R.id.filter_gallery);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.filter_gallery.setLayoutManager(linearLayoutManager);
        this.filter_gallery.setFocusable(false);
        this.filtersAdapter = new CameraFiltersAdapter(new IRecycleViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, int i, Object obj) {
                FilterExt filterExt = ToolFilterManager.INSTANCE.getCacheFilterList().get(i);
                onCreateAndInitFilter(filterExt.getMId(),100,true);
            }
        }, (((float) this.activity.getResources().getDisplayMetrics().widthPixels) * 3.0f) / 16.0f);
        this.filter_gallery.setAdapter(this.filtersAdapter);
        this.filter_btn = (TextView) this.activity.findViewById(R.id.filter_btn);
        this.white_btn = (TextView) this.activity.findViewById(R.id.white_btn);
        this.buffing_btn = (TextView) this.activity.findViewById(R.id.buffing_btn);
        this.beauty_level_group = (RadioGroup) this.activity.findViewById(R.id.beauty_level_group);
        this.beauty_level_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int i2;
                if (((RadioButton) beauty_level_group.findViewById(checkedId)).isChecked()) {
                    if (checkedId == R.id.level_0) {
                        i2 = 0;
                    } else if (checkedId == R.id.level_1) {
                        i2 = 1;
                    } else if (checkedId == R.id.level_2) {
                        i2 = 2;
                    } else if (checkedId == R.id.level_3) {
                        i2 = 3;
                    } else if (checkedId == R.id.level_4) {
                        i2 = 4;
                    } else {
                        i2 = checkedId == R.id.level_5 ? 5 : 0;
                    }
                    switch (mSelectType) {
                        case 10001:
                            updateBeautylevel(i2, buffing_level, face_level,eye_level);
                            return;
                        case 10002:
                            updateBeautylevel(white_level, i2, face_level,eye_level);
                            return;
                        case 10003:
                            updateBeautylevel(white_level, buffing_level, i2,eye_level);
                            return;
                        case 10004:
                            updateBeautylevel(white_level, buffing_level, face_level, i2);
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        this.filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectType = 10000;
                updateBtnStatus();
            }
        });
        this.white_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectType = 10001;
                updateBtnStatus();
                updateGroupViewStatus();
            }
        });
        this.buffing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectType = 10002;
                updateBtnStatus();
                updateGroupViewStatus();
            }
        });

        this.gestureDetector = new GestureDetector(this.activity, new SimpleOnGestureListenerExt());
    }

    private void updateBtnStatus() {
        this.filter_btn.setSelected(false);
        this.white_btn.setSelected(false);
        this.buffing_btn.setSelected(false);
        switch (this.mSelectType) {
            case 10000:
                this.filter_btn.setSelected(true);
                this.beauty_level_group.setVisibility(View.GONE);
                this.filter_gallery.setVisibility(View.VISIBLE);
                return;
            case 10001:
                this.white_btn.setSelected(true);
                this.filter_gallery.setVisibility(View.GONE);
                this.beauty_level_group.setVisibility(View.VISIBLE);
                return;
            case 10002:
                this.buffing_btn.setSelected(true);
                this.filter_gallery.setVisibility(View.GONE);
                this.beauty_level_group.setVisibility(View.VISIBLE);
                return;
            default:
                return;
        }
    }

    private void updateGroupViewStatus() {
        int i = 0;
        switch (this.mSelectType) {
            case 10001:
                i = this.white_level;
                break;
            case 10002:
                i = this.buffing_level;
                break;
            case 10003:
                i = this.face_level;
                break;
            case 10004:
                i = this.eye_level;
                break;
        }
        switch (i) {
            case 0:
                this.beauty_level_group.check(R.id.level_0);
                return;
            case 1:
                this.beauty_level_group.check(R.id.level_1);
                return;
            case 2:
                this.beauty_level_group.check(R.id.level_2);
                return;
            case 3:
                this.beauty_level_group.check(R.id.level_3);
                return;
            case 4:
                this.beauty_level_group.check(R.id.level_4);
                return;
            case 5:
                this.beauty_level_group.check(R.id.level_5);
                return;
            default:
                return;
        }
    }

    public void initBeautyFilter() {
        adjustWhiteLevel();
        adjustBuffingLevel();
        updateGroupViewStatus();
        switchFilter(this.filterIndex);
    }

    private void switchFilter(int i) {
        List B = ToolFilterManager.INSTANCE.getCacheFilterList();
        BaseRender b = getRenderByIndex(i);
        BaseRender b2 = getRenderByIndex(i + 1 > B.size() + -1 ? 0 : i + 1);
        if (b != b2) {
            if (this.switchRender == null) {
                FilterExt filterExt = new FilterExt();
                this.switchRender = new SwitchRender();
                this.switchRender.setRenders(b, b2);
                Adjuster adjuster = new Adjuster(this.switchRender);
                adjuster.setStart(0);
                adjuster.setEnd(ScreenUtils.getScreenWidth());
                adjuster.setInitProgress(ScreenUtils.getScreenWidth());
                filterExt.setAdjuster(adjuster);
                this.processExt.switchFilter(filterExt);
            } else {
                ArrayList renders = this.switchRender.setRenders(b, b2);
                if (renders != null) {
                    this.processExt.getIRenderView().queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            destroyRender(renders);
                        }
                    });
                }
            }
            this.filtersAdapter.setSelectPosition(i);
        }
    }

    private void updateBeautylevel(int white_level, int buffing_level, int face_level, int eye_level) {
        if (this.white_level != white_level) {
            this.white_level = white_level;
            SPStaticUtils.put("key_camera_white_level", this.white_level);
            adjustWhiteLevel();
            updateGroupViewStatus();
        }
        if (this.buffing_level != buffing_level) {
            this.buffing_level = buffing_level;
            SPStaticUtils.put("key_camera_buffing_level", this.buffing_level);
            adjustBuffingLevel();
            updateGroupViewStatus();
        }
    }

    private void adjustWhiteLevel() {
        Filter x = ToolFilterManager.INSTANCE.getCameraWhiteningTool();
        if (this.white_level != 0) {
            x.getAdjuster().adjust(this.white_level);
            this.processExt.addFilter(x);
            return;
        }
        this.processExt.removeFilter(x);
    }

    private void adjustBuffingLevel() {
        Filter u = ToolFilterManager.INSTANCE.getCameraBuffingTool();
        if (this.buffing_level != 0) {
            u.getAdjuster().adjust(this.buffing_level);
            this.processExt.addFilter(u);
            return;
        }
        this.processExt.removeFilter(u);
    }

    public void onCreateAndInitFilter(int filterId, int filterProgress, boolean z) {
        if (filterId != -1 && filterId < 1000000) {
            FilterExt g = ToolFilterManager.INSTANCE.getCacheFilterById(filterId);
            if (g == null) {
                ToastUtils.INSTANCE.show("该滤镜无效");
            } else {
                initAndStart(g, filterProgress, z);
            }
        }
    }

    private void initAndStart(FilterExt filterExt, int progress, boolean hasFilter) {
        this.filterIndex = ToolFilterManager.INSTANCE.getCacheFilterList().indexOf(filterExt);
        if (progress >= 0) {
            filterExt.getAdjuster().adjust(progress);
        }
        if (hasFilter) {
            setCurFilterName(filterExt.getName());
            this.onFilterBeautyListener.onSelectFilterId(filterExt.getMId());
        }
        filterExt.startTool();
        if (!(filterExt instanceof OriginNormalFilter) && noFilterUse((Filter) filterExt)) {
            this.weakHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.INSTANCE.show("该滤镜已经下架");
                }
            });
        }
        switchFilter(this.filterIndex);
    }

    public void setCurFilterName(String str) {
        if (this.updateNameAnimator != null) {
            this.updateNameAnimator.cancel();
            this.updateNameAnimator.setDuration(1250);
            this.updateNameAnimator.setFillAfter(false);
            this.updateNameAnimator.setRepeatMode(2);
            this.updateNameAnimator.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    current_filter_name.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    current_filter_name.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        this.current_filter_name.setText(str);
        this.current_filter_name.startAnimation(this.updateNameAnimator);
    }

    private boolean noFilterUse(Filter filter) {
        return filter == null || ((filter instanceof OriginNormalFilter) && ((OriginNormalFilter) filter).undercarriage());
    }

    public void showBeautyView(boolean z) {
        if (z) {
            this.camera_beauty_tool.setVisibility(View.VISIBLE);
            this.filter_gallery.setVisibility(View.VISIBLE);
            this.beauty_level_group.setVisibility(View.GONE);
            if (this.processExt.getSwitchFilter() != null) {
                this.filter_gallery.scrollToPosition(ToolFilterManager.INSTANCE.getCacheFilterList().indexOf(this.processExt.getSwitchFilter()));
            } else {
                this.filter_gallery.scrollToPosition(0);
            }
            updateBtnStatus();
        }else{
            this.camera_beauty_tool.setVisibility(View.GONE);
        }
    }

    public boolean isShown() {
        return this.camera_beauty_tool.getVisibility() == View.VISIBLE;
    }

    public void takePhoto(final Camera camera, final Parameters parameters, String str, final int i, final float ratio, final boolean z, final OnTakePhotoListener onTakePhotoListener) {
        long j;
        if ("on".equals(str)) {
            parameters.setFlashMode("torch");
            j = 300;
        } else {
            j = 0;
        }
        try {
            camera.setParameters(parameters);
        } catch (Throwable e) {
           e.printStackTrace();
        }
        this.weakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Camera.Size previewSize = parameters.getPreviewSize();
                camera.stopPreview();
                processExt.takePhoto(new BitmapOutput.BitmapOutputCallback(){
                    @Override
                    public void bitmapOutput(final Bitmap bitmap) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("torch".equals(parameters.getFlashMode())) {
                                    parameters.setFlashMode("on");
                                    try {
                                        camera.setParameters(parameters);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        Bitmap a;
                        if (bitmap != null && (a = scaleBitmap(bitmap, ratio)) != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onTakePhotoListener.onCapturePhoto(bitmap, bitmap.copy(Bitmap.Config.ARGB_8888, true), i);
                                }
                            });
                        }
                    }
                },previewSize.height, previewSize.width, z);
            }
        }, j);
    }


    private Bitmap scaleBitmap(Bitmap bitmap, float f) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        int min = Math.min(width, height);
        return Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap, 0, max - Math.round((((float) min) * 16.0f) / 9.0f), min, Math.round((((float) min) * 16.0f) / 9.0f)), Math.max(750, min), Math.max(1333, Math.round((((float) min) * 16.0f) / 9.0f)), true);
    }


    private void animateRight() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{(int) this.offsetX, ScreenUtils.getScreenWidth()});
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                switchRender.adjust(intValue, 0, ScreenUtils.getScreenWidth());
                if (intValue == ScreenUtils.getScreenWidth() && flingDirection == 1) {
                    List<FilterExt> filterList = ToolFilterManager.INSTANCE.getCacheFilterList();
                    filterIndex--;
                    if (filterIndex < 0) {
                        filterIndex = filterList.size() - 1;
                    }
                    setCurFilterName(filterList.get(filterIndex).getName());
                    filtersAdapter.setSelectPosition(filterIndex);
                }
                offsetX = (float) intValue;
            }
        });
        ofInt.setDuration(250);
        ofInt.start();
    }

    private void animateLeft() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{(int) this.offsetX, 0});
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                switchRender.adjust(intValue, 0, ScreenUtils.getScreenWidth());
                if (intValue == 0 && flingDirection == 0) {
                    List<FilterExt> B = ToolFilterManager.INSTANCE.getCacheFilterList();
                    filterIndex++;
                    if (filterIndex >= B.size()) {
                        filterIndex = 0;
                    }
                    setCurFilterName(B.get(filterIndex).getName());
                    filtersAdapter.setSelectPosition(filterIndex);
                }
                offsetX = (float) intValue;
            }
        });
        ofInt.setDuration(250);
        ofInt.start();
    }

    public void onTouch(MotionEvent motionEvent) {
        if (!this.gestureDetector.onTouchEvent(motionEvent) && motionEvent.getAction() == 1) {
            if (this.isFlinging) {
                if (this.flingDirection == 1) {
                    animateRight();
                } else {
                    animateLeft();
                }
            } else if (this.flingDirection == -1) {
            } else {
                if (this.offsetX > ((float) (ScreenUtils.getScreenWidth() / 2))) {
                    animateRight();
                } else {
                    animateLeft();
                }
            }
        }
    }

    public int getCurFilterIndex() {
        return this.filterIndex;
    }

    private BaseRender getRenderByIndex(int i) {
        List filterList = ToolFilterManager.INSTANCE.getCacheFilterList();
        BaseRender render = ((FilterExt) filterList.get(i)).getAdjuster().getMRender();
        if (render == null) {
            return ((FilterExt) filterList.get(0)).getAdjuster().getMRender();
        }
        return render;
    }
}
