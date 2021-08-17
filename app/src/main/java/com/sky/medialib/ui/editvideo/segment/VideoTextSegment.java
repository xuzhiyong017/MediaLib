package com.sky.medialib.ui.editvideo.segment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoDraftText;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.TextWatermarksProtocol;
import com.sky.medialib.ui.editvideo.segment.proto.ToolbarProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.media.Watermark;
import com.sky.medialib.ui.kit.view.StickerView;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoTextSegment extends BaseSegment<VideoEditData> implements TextWatermarksProtocol {
    private StickerView curStickerView;
    private ToolbarProtocol toolbarProtocol;
    @BindView(R.id.text_btn)
    TextView mTextBtn;
    @BindView(R.id.text_container_layout)
    RelativeLayout mTextContainer;

    public VideoTextSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind( this,baseActivity);
        init();
    }

    private void init() {
        this.mTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curStickerView = null;
                toolbarProtocol.hideBar();
                mData.jumpInputActivity = true;
                //TODO
//                activity.startActivityForResult(new Intent(activity, TextInputActivity.class), 2);
            }
        });
    }

    public void initText() {
        List<VideoDraftText> h = ((VideoEditData) this.mData).getWaterMarkList();
        if (h != null) {
            for (VideoDraftText videoDraftText : h) {
                Matrix matrix = new Matrix();
                matrix.setValues(videoDraftText.matrix);
                StickerView a = createSticker(videoDraftText.text, videoDraftText.color);
                a.setStickerMatrix(matrix);
                a.setTag(videoDraftText);
                a.setShowDrawController(false);
            }
        }
    }

    public void setToolbarProtocol(ToolbarProtocol toolbarProtocol) {
        this.toolbarProtocol = toolbarProtocol;
    }

    public void onResume() {
        super.onResume();
        ((VideoEditData) this.mData).jumpInputActivity = false;
    }

    public List<Watermark> getWaterMarkList() {
        List<Watermark> arrayList = new ArrayList();
        int childCount = this.mTextContainer.getChildCount();
        if (childCount > 0) {
            float a = (((float) ((VideoEditData) this.mData).processExt.getVideoWidth()) * 1.0f) / ((float) this.mTextContainer.getWidth());
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mTextContainer.getChildAt(i);
                if (childAt instanceof StickerView) {
                    StickerView stickerView = (StickerView) childAt;
                    Point[] vertexCoordinate = stickerView.getVertexCoordinate();
                    String a2 = Storage.storageToTempPngPath(Bitmap.createBitmap(stickerView.getBitmap(), vertexCoordinate[0].x, vertexCoordinate[0].y, vertexCoordinate[1].x - vertexCoordinate[0].x, vertexCoordinate[1].y - vertexCoordinate[0].y));
                    if (FileUtil.INSTANCE.exists(a2)) {
                        Watermark watermark = new Watermark();
                        watermark.path = a2;
                        watermark.x = (int) (((float) vertexCoordinate[0].x) * a);
                        watermark.y = (int) (((float) vertexCoordinate[0].y) * a);
                        watermark.scale = a;
                        arrayList.add(watermark);
                    }
                }
            }
        }
        return arrayList;
    }

    private StickerView createSticker(String str, int i) {
        hideController();
        final StickerView stickerView = new StickerView(this.activity);
        stickerView.setText(str, 2);
        stickerView.setTextColor(i);
        stickerView.setOnStickerClickListener(new StickerView.OnStickerClickListener() {
            @Override
            public void onClick() {
                curStickerView = stickerView;
                toolbarProtocol.hideBar();
                mData.jumpInputActivity = true;
                //TODO
//                Intent intent = new Intent(activity, TextInputActivity.class);
//                intent.putExtra("KEY_WORDS", stickerView.getText());
//                intent.putExtra("KEY_COLOR", stickerView.getTextColor());
//                activity.startActivityForResult(intent, 2);
            }
        });
        stickerView.setOnStickerDeleteListener(new StickerView.OnStickerDeleteListener() {
            @Override
            public void onClick(@Nullable Object tag) {
                if (tag instanceof VideoDraftText) {
                    List h = mData.getWaterMarkList();
                    if (h != null && h.contains(tag)) {
                        h.remove(tag);
                    }
                    mData.setWaterMarkList(h);
                }
            }

        });
        stickerView.setOnStickerChangedListener(new StickerView.OnStickerChangedListener() {
            @Override
            public void onChange() {
                Object tag = stickerView.getTag();
                if (tag instanceof VideoDraftText) {
                    VideoDraftText videoDraftText = (VideoDraftText) tag;
                    List<VideoDraftText> h = mData.getWaterMarkList();
                    if (h != null && h.contains(videoDraftText)) {
                        h.remove(videoDraftText);
                        videoDraftText.text = stickerView.getText();
                        videoDraftText.color = stickerView.getTextColor();
                        float[] fArr = new float[9];
                        stickerView.getStickerMatrix().getValues(fArr);
                        videoDraftText.matrix = fArr;
                        h.add(videoDraftText);
                    }
                    mData.setWaterMarkList(h);
                }
            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(8, R.id.processing_view);
        layoutParams.addRule(6, R.id.processing_view);
        this.mTextContainer.addView(stickerView, layoutParams);
        return stickerView;
    }


    private void hideController() {
        int childCount = this.mTextContainer.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mTextContainer.getChildAt(i);
                if (childAt instanceof StickerView) {
                    StickerView stickerView = (StickerView) childAt;
                    stickerView.setShowDrawController(false);
                    stickerView.invalidate();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != 0) {
            switch (requestCode) {
                case 2:
                    this.toolbarProtocol.showBar();
                    if (intent != null) {
                        String stringExtra = intent.getStringExtra("KEY_WORDS");
                        int intExtra = intent.getIntExtra("KEY_COLOR", -1);
                        if (this.curStickerView != null) {
                            this.curStickerView.setText(stringExtra, 2);
                            this.curStickerView.setTextColor(intExtra);
                            return;
                        } else if (!TextUtils.isEmpty(stringExtra)) {
                            List arrayList;
                            StickerView a = createSticker(stringExtra, intExtra);
                            Matrix matrix = new Matrix();
                            PointF textOriginalSize = a.getTextOriginalSize();
                            int previewWidth = ((VideoEditData) this.mData).processExt.getMContainerView().getPreviewWidth() / 2;
                            int previewHeight = ((VideoEditData) this.mData).processExt.getMContainerView().getPreviewHeight() / 2;
                            if (textOriginalSize != null) {
                                previewWidth = (int) (((float) previewWidth) - (textOriginalSize.x / 2.0f));
                                previewHeight = (int) (((float) previewHeight) - (textOriginalSize.y / 2.0f));
                            }
                            matrix.postTranslate((float) previewWidth, (float) previewHeight);
                            a.setStickerMatrix(matrix);
                            VideoDraftText videoDraftText = new VideoDraftText();
                            videoDraftText.text = stringExtra;
                            videoDraftText.color = intExtra;
                            float[] fArr = new float[9];
                            matrix.getValues(fArr);
                            videoDraftText.matrix = fArr;
                            List h = ((VideoEditData) this.mData).getWaterMarkList();
                            if (h == null) {
                                arrayList = new ArrayList();
                            } else {
                                arrayList = h;
                            }
                            arrayList.add(videoDraftText);
                            ((VideoEditData) this.mData).setWaterMarkList(arrayList);
                            a.setTag(videoDraftText);
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
