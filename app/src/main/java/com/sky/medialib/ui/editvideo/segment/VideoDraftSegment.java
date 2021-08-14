package com.sky.medialib.ui.editvideo.segment;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.sky.media.kit.base.BaseActivity;
import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoDraft;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.event.SaveDraftEvent;
import com.sky.medialib.ui.editvideo.segment.proto.DraftLayoutProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.util.EventBusHelper;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.task.SimpleTask;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoDraftSegment extends BaseSegment<VideoEditData> implements DraftLayoutProtocol {

    private boolean isSaveing;
    @BindView(R.id.save_draft)
    LinearLayout mSaveDraft;

    public VideoDraftSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind((Object) this, (Activity) baseActivity);
        this.mSaveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDraft(true);
            }
        });
    }

    public void saveDraft(final boolean z) {
        if (!this.isSaveing) {
            this.isSaveing = true;
            new SimpleTask() {

                protected void doInBackground() {
                    long currentTimeMillis = System.currentTimeMillis();
                    File file = new File(Storage.getFilePathByType(6) + currentTimeMillis);
                    if (!(file.exists() && file.isDirectory())) {
                        file.mkdirs();
                    }
                    VideoDraft r = ((VideoEditData) VideoDraftSegment.this.mData).getVideoDraft();
                    String str = file.getPath() + "/" + VideoDraftSegment.this.getSimpleName(r.videoPath);
                    FileUtil.INSTANCE.copy(r.videoPath, str);
                    r.videoPath = str;
                    if (!TextUtils.isEmpty(r.musicPath)) {
                        str = file.getPath() + "/" + VideoDraftSegment.this.getSimpleName(r.musicPath);
                        FileUtil.INSTANCE.copy(r.musicPath, str);
                        r.musicPath = str;
                    }
                    if (!TextUtils.isEmpty(r.videoReversePath)) {
                        String str2 = file.getPath() + "/" + VideoDraftSegment.this.getSimpleName(r.videoReversePath);
                        FileUtil.INSTANCE.copy(r.videoReversePath, str2);
                        r.videoReversePath = str2;
                    }
                    r.updateTime = currentTimeMillis;
                    EventBusHelper.post(new SaveDraftEvent(r));
                }

                protected void onPostExecute() {
                    super.onPostExecute();
                    if (z) {
                        ToastUtils.INSTANCE.showToast(R.string.had_saved_draft);
                        EventBusHelper.post("event_weibo_close_activity");
                    }
                    VideoDraftSegment.this.isSaveing = false;
                }
            }.execute();
        }
    }

    private String getSimpleName(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.substring(str.lastIndexOf("/") + 1, str.length());
    }

    public void hide() {
        this.mSaveDraft.setVisibility(View.GONE);
    }

    public void show() {
        this.mSaveDraft.setVisibility(View.VISIBLE);
    }
}
