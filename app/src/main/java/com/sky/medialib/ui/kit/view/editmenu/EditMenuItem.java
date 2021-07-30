package com.sky.medialib.ui.kit.view.editmenu;


import com.sky.medialib.R;

public enum EditMenuItem {
    NONE(R.drawable.camera_edit_sticker_selector, R.string.sticker),
    STICKER(R.drawable.camera_edit_sticker_selector, R.string.sticker),
    FILTER(R.drawable.camera_edit_filter_selector, R.string.filter),
    MAGIC(R.drawable.camera_edit_mirror_selector, R.string.magic),
    BEAUTY(R.drawable.camera_edit_beauty_selector, R.string.beauty),
    TEXT(R.drawable.camera_edit_name_selector, R.string.text),
    TOOL(R.drawable.camera_edit_adjust_selector, R.string.adjust),
    AT(R.drawable.camera_edit_at_selector, R.string.at),
    FRAME(R.drawable.camera_edit_frame_selector, R.string.frame),
    CLIP(R.drawable.camera_edit_cut_selector, R.string.cut),
    DAUBER(R.drawable.camera_edit_mosaic_selector, R.string.mosaic);

    public int resId;
    public int stringId;

    private EditMenuItem(int i, int i2) {
        this.resId = i;
        this.stringId = i2;
    }
}
