package com.sky.medialib.ui.editvideo.segment;

import android.animation.ValueAnimator;
import android.app.Activity;
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


import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.render.SwitchRender;
import com.sky.media.kit.model.FilterExt;
import com.sky.medialib.R;
import com.sky.medialib.ui.camera.adapter.CameraFiltersAdapter;
import com.sky.medialib.ui.camera.adapter.IRecycleViewItemClickListener;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.DraftLayoutProtocol;
import com.sky.medialib.ui.editvideo.segment.proto.PublishLayoutProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.filter.OriginNormalFilter;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.WeakHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoBeautySegment extends BaseSegment<VideoEditData> {

    private CameraFiltersAdapter filtersAdapter;
    private GestureDetector gestureDetector;
    private int filterIndex;
    private AlphaAnimation alphaAnimation = new AlphaAnimation(0.9f, 1.0f);
    private WeakHandler handler = new WeakHandler();
    private int selectType = 10000;
    private SwitchRender switchRender;
    private PublishLayoutProtocol publishLayoutProtocol;
    private DraftLayoutProtocol draftLayoutProtocol;
    private int flingDirection = -1;
    private float offsetX;
    @BindView( R.id.beauty_level_group)
    RadioGroup mBeautifyGroup;
    @BindView(R.id.beauty_btn)
    TextView mBeautyBtn;
    @BindView(R.id.buffing_btn)
    TextView mBuffingTab;
    @BindView(R.id.current_filter_name)
    TextView mCurrentFilterName;
    @BindView(R.id.filter_mask)
    View mFilterShowMask;
    @BindView(R.id.filter_btn)
    TextView mFilterTab;
    @BindView(R.id.filter_gallery)
    RecyclerView mRecyclerView;
    @BindView(R.id.posted_beauty_tool)
    View mToolRootView;
    @BindView(R.id.white_btn)
    TextView mWhiteTab;
    private float downX;
    private boolean isFlinging;



    class GestureListener extends SimpleOnGestureListener {
        GestureListener() {
        }

        public boolean onDown(MotionEvent motionEvent) {
            VideoBeautySegment.this.flingDirection = -1;
            VideoBeautySegment.this.downX = motionEvent.getX();
            VideoBeautySegment.this.isFlinging = false;
            return super.onDown(motionEvent);
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if(switchRender != null){
                if (flingDirection == -1) {
                    BaseRender b;
                    BaseRender b2;
                    List cacheFilterList = ToolFilterManager.INSTANCE.getEditVideoFilterList();
                    int index;
                    if (motionEvent2.getX() - downX < 0.0f) {
                        flingDirection = 0;
                        index = filterIndex + 1;
                        if (index > cacheFilterList.size() - 1) {
                            index = 0;
                        }
                        b = getRenderByFilterId(filterIndex);
                        b2 = getRenderByFilterId(index);
                        offsetX = (float) ScreenUtils.getScreenWidth();
                    } else {
                        flingDirection = 1;
                        index = filterIndex - 1;
                        if (index < 0) {
                            index = cacheFilterList.size() - 1;
                        }
                        b = getRenderByFilterId(index);
                        b2 = getRenderByFilterId(filterIndex);
                        offsetX = 0.0f;
                    }

                    final BaseRender filter1 = b;
                    final BaseRender filter2 = b2;
                    mData.processExt.getIRenderView().queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            destroyRender(switchRender.setRenders(filter1, filter2));
                        }
                    });
                }
                offsetX = offsetX - f;
                switchRender.adjust((int) offsetX, 0, ScreenUtils.getScreenWidth());
                mData.processExt.requestRender();
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

    public VideoBeautySegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind((Object) this, (Activity) baseActivity);
        init();
    }

    private void init() {
        this.mBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView();
            }
        });
        this.mFilterShowMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideView();
            }
        });
        this.mToolRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.mCurrentFilterName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRecyclerView.setFocusable(false);
        this.filtersAdapter = new CameraFiltersAdapter(new IRecycleViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, int i, Object obj) {
                FilterExt filterExt = ToolFilterManager.INSTANCE.getEditVideoFilterList().get(i);
                selectFilterById(filterExt.getMId(),100,true);
            }
        },ToolFilterManager.INSTANCE.getEditVideoFilterList() ,(((float) this.activity.getResources().getDisplayMetrics().widthPixels) * 3.0f) / 16.0f);
        this.mRecyclerView.setAdapter(this.filtersAdapter);
        this.mBeautifyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                int i2 = 0;
                if (((RadioButton) radioGroup.findViewById(checkedId)).isChecked()) {
                    if (checkedId != R.id.level_0) {
                        if (checkedId == R.id.level_1) {
                            i2 = 1;
                        } else if (checkedId == R.id.level_2) {
                            i2 = 2;
                        } else if (checkedId == R.id.level_3) {
                            i2 = 3;
                        } else if (checkedId == R.id.level_4) {
                            i2 = 4;
                        } else if (checkedId == R.id.level_5) {
                            i2 = 5;
                        }
                    }
                    switch (selectType) {
                        case 10001:
                            adjustBeautyLevel(i2, mData.getSkinLevel(), mData.getFaceLevel(), mData.getEyesLevel());
                            return;
                        case 10002:
                            adjustBeautyLevel(mData.getWhitenLevel(), i2, mData.getFaceLevel(), mData.getEyesLevel());
                            return;
                        case 10003:
                            adjustBeautyLevel(mData.getWhitenLevel(), mData.getSkinLevel(), i2, mData.getEyesLevel());
                            return;
                        case 10004:
                            adjustBeautyLevel(mData.getWhitenLevel(), mData.getSkinLevel(), mData.getFaceLevel(), i2);
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        this.mFilterTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType = 10000;
                updateBtnStatus();
            }
        });
        this.mWhiteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType = 10001;
                updateBtnStatus();
                checkedStatus();
            }
        });
        this.mBuffingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType = 10002;
                updateBtnStatus();
                checkedStatus();
            }
        });
        this.gestureDetector = new GestureDetector(this.activity, new GestureListener());
    }

    public void setPublishLayoutProtocol(PublishLayoutProtocol publishLayoutProtocol) {
        this.publishLayoutProtocol = publishLayoutProtocol;
    }

    public void setDraftLayoutProtocol(DraftLayoutProtocol draftLayoutProtocol) {
        this.draftLayoutProtocol = draftLayoutProtocol;
    }

    private void showView() {
        if (!isShown()) {
            this.mFilterShowMask.setVisibility(View.VISIBLE);
            showBeautyView(true);
            if(publishLayoutProtocol != null){
                this.publishLayoutProtocol.hide();
            }

            this.draftLayoutProtocol.hide();
        }
    }

    public boolean hideView() {
        if (!isShown()) {
            return false;
        }
        this.mFilterShowMask.setVisibility(View.GONE);
        showBeautyView(false);
        if(publishLayoutProtocol != null){
            this.publishLayoutProtocol.show();
        }

        this.draftLayoutProtocol.show();
        return true;
    }

    private void updateBtnStatus() {
        this.mFilterTab.setSelected(false);
        this.mWhiteTab.setSelected(false);
        this.mBuffingTab.setSelected(false);
        switch (this.selectType) {
            case 10000:
                this.mFilterTab.setSelected(true);
                this.mBeautifyGroup.setVisibility(View.GONE);
                this.mRecyclerView.setVisibility(View.VISIBLE);
                return;
            case 10001:
                this.mWhiteTab.setSelected(true);
                this.mRecyclerView.setVisibility(View.GONE);
                this.mBeautifyGroup.setVisibility(View.VISIBLE);
                return;
            case 10002:
                this.mBuffingTab.setSelected(true);
                this.mRecyclerView.setVisibility(View.GONE);
                this.mBeautifyGroup.setVisibility(View.VISIBLE);
                return;
            default:
                return;
        }
    }

    private void checkedStatus() {
        int i = 0;
        switch (this.selectType) {
            case 10001:
                i = ((VideoEditData) this.mData).getWhitenLevel();
                break;
            case 10002:
                i = ((VideoEditData) this.mData).getSkinLevel();
                break;
            case 10003:
                i = ((VideoEditData) this.mData).getFaceLevel();
                break;
            case 10004:
                i = ((VideoEditData) this.mData).getEyesLevel();
                break;
        }
        switch (i) {
            case 0:
                this.mBeautifyGroup.check(R.id.level_0);
                return;
            case 1:
                this.mBeautifyGroup.check(R.id.level_1);
                return;
            case 2:
                this.mBeautifyGroup.check(R.id.level_2);
                return;
            case 3:
                this.mBeautifyGroup.check(R.id.level_3);
                return;
            case 4:
                this.mBeautifyGroup.check(R.id.level_4);
                return;
            case 5:
                this.mBeautifyGroup.check(R.id.level_5);
                return;
            default:
                return;
        }
    }

    public void initBeautyFilter() {
        adjustWhite();
        adjustSkin();
        checkedStatus();
        updateSwitchRender(filterIndex);
        mFilterTab.post(new Runnable() {
            @Override
            public void run() {
                selectFilterById(0,0,false);
            }
        });
    }

    private void updateSwitchRender(int i) {
        List filterList = ToolFilterManager.INSTANCE.getEditVideoFilterList();
        BaseRender b = getRenderByFilterId(i);
        BaseRender b2 = getRenderByFilterId(i + 1 > filterList.size() + -1 ? 0 : i + 1);
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
                mData.processExt.switchFilter(filterExt);
            } else {
                ArrayList renders = this.switchRender.setRenders(b, b2);
                if (renders != null) {
                    mData.processExt.getIRenderView().queueEvent(new Runnable() {
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

    void destroyRender(ArrayList arrayList) {
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((BaseRender) it.next()).destroy();
            }
        }
    }

    private void adjustBeautyLevel(int i, int i2, int i3, int i4) {
        if (((VideoEditData) this.mData).getWhitenLevel() != i) {
            ((VideoEditData) this.mData).setWhiteLevel(i);
            adjustWhite();
            checkedStatus();
        }
        if (((VideoEditData) this.mData).getSkinLevel() != i2) {
            ((VideoEditData) this.mData).setSkinLevel(i2);
            adjustSkin();
            checkedStatus();
        }
    }

    private void adjustWhite() {
        Filter k = ToolFilterManager.INSTANCE.getVideoWhiteningTool();
        if (((VideoEditData) this.mData).getWhitenLevel() != 0) {
            k.getAdjuster().adjust(((VideoEditData) this.mData).getWhitenLevel());
            ((VideoEditData) this.mData).processExt.addFilter(k);
            return;
        }
        ((VideoEditData) this.mData).processExt.removeFilter(k);
    }

    private void adjustSkin() {
        Filter h = ToolFilterManager.INSTANCE.getVideoBuffingTool();
        if (((VideoEditData) this.mData).getSkinLevel() != 0) {
            h.getAdjuster().adjust(((VideoEditData) this.mData).getSkinLevel());
            ((VideoEditData) this.mData).processExt.addFilter(h);
            return;
        }
        ((VideoEditData) this.mData).processExt.removeFilter(h);
    }


    private void selectFilterById(int i, int i2, boolean z) {
        if (i != -1 && i < 1000000) {
            FilterExt d = ToolFilterManager.INSTANCE.getEditVideoFilterById(i);
            if (d == null) {
                ToastUtils.INSTANCE.showToast("该滤镜无效");
            } else {
                startFilter(d, i2, z);
            }
        }
    }

    private void startFilter(FilterExt filterExt, int i, boolean z) {
        this.filterIndex = ToolFilterManager.INSTANCE.getEditVideoFilterList().indexOf(filterExt);
        ((VideoEditData) this.mData).setFilterId(filterExt.getMId());
        if (i >= 0) {
            filterExt.getAdjuster().adjust(i);
        }
        if (z) {
            setCurFilterName(filterExt.getName());
        }
        filterExt.startTool();
        if (!(filterExt instanceof OriginNormalFilter) && noFilterUse((Filter) filterExt)) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.INSTANCE.showToast("滤镜已下架");
                }
            });
        }
        updateSwitchRender(this.filterIndex);
    }

    private void setCurFilterName(String str) {
        if (this.alphaAnimation != null) {
            this.alphaAnimation.cancel();
            this.alphaAnimation.setDuration(1250);
            this.alphaAnimation.setFillAfter(false);
            this.alphaAnimation.setRepeatMode(2);
            this.alphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCurrentFilterName.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCurrentFilterName.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        this.mCurrentFilterName.setText(str);
        this.mCurrentFilterName.startAnimation(this.alphaAnimation);
    }

    private boolean noFilterUse(Filter filter) {
        return filter == null || ((filter instanceof OriginNormalFilter) && ((OriginNormalFilter) filter).undercarriage());
    }

    private void showBeautyView(boolean z) {
        if (z) {
            this.mToolRootView.setVisibility(View.VISIBLE);
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mBeautifyGroup.setVisibility(View.GONE);
            if (((VideoEditData) this.mData).processExt.getSwitchFilter() != null) {
                this.mRecyclerView.scrollToPosition(ToolFilterManager.INSTANCE.getEditVideoFilterList().indexOf(((VideoEditData) this.mData).processExt.getSwitchFilter()));
            } else {
                this.mRecyclerView.scrollToPosition(0);
            }
            updateBtnStatus();
            return;
        }
        this.mToolRootView.setVisibility(View.GONE);
    }

    private boolean isShown() {
        return this.mToolRootView.getVisibility() == View.VISIBLE;
    }

    private void animateRight() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{(int) this.offsetX, ScreenUtils.getScreenWidth()});
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                switchRender.adjust(intValue, 0, ScreenUtils.getScreenWidth());
                if (intValue == ScreenUtils.getScreenWidth() && flingDirection == 1) {
                    List<FilterExt> filterList = ToolFilterManager.INSTANCE.getEditVideoFilterList();
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
                    List<FilterExt> B = ToolFilterManager.INSTANCE.getEditVideoFilterList();
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

    private BaseRender getRenderByFilterId(int i) {
        return ((FilterExt) ToolFilterManager.INSTANCE.getEditVideoFilterList().get(i)).getAdjuster().getMRender();
    }
}
