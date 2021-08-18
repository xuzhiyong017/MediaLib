package com.sky.medialib.ui.input;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.common.view.colorpick.ColorSeekView;
import com.sky.medialib.util.InputMethodUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextInputActivity extends AppActivity {
    public static final String KEY_COLOR = "KEY_COLOR";
    public static final String KEY_WORDS = "KEY_WORDS";
    @BindView(R.id.color_seek_bar)
    ColorSeekView mColorSeek;
    @BindView(R.id.edit_text)
    LimitEditText mEdit;
    private int mExistedColor;
    private String mExistedText;
    @BindView(R.id.text_input_root)
    RelativeLayout mRootView;

    protected boolean isSupportSwipeBack() {
        return false;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_text_input);
        ButterKnife.bind( this);
        initData();
        initView();
    }

    public void finish() {
        Intent intent = new Intent();
        if (this.mEdit.getText() != null) {
            intent.putExtra("KEY_WORDS", this.mEdit.getText().toString());
        } else {
            intent.putExtra("KEY_WORDS", "");
        }
        intent.putExtra("KEY_COLOR", this.mEdit.getCurrentTextColor());
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onResume() {
        super.onResume();
        this.mEdit.requestFocus();
    }

    protected void onDestroy() {
        InputMethodUtil.hideSoftInput((Activity) this);
        super.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        this.mExistedText = intent.getStringExtra("KEY_WORDS");
        this.mExistedColor = intent.getIntExtra("KEY_COLOR", -1);
    }

    private void initView() {
        this.mColorSeek.setIChangeTextColor(color -> mEdit.setTextColor(color));
        this.mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() != 66) {
                    return false;
                }
                InputMethodUtil.hideSoftInput( TextInputActivity.this);
                return true;
            }
        });
        this.mEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEdit.requestFocus();
                return false;
            }
        });
        this.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodUtil.hideSoftInput(TextInputActivity.this);
                finish();
            }
        });
        this.mRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && bottom > oldBottom) {
                    finish();
                }
            }
        });
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(mExistedText)) {
                    mEdit.setText(mExistedText);
                    mEdit.setTextColor(mExistedColor);
                    mEdit.setSelection(mExistedText.length());
                    mColorSeek.setSeekBarPositionByColor(mExistedColor);
                }
            }
        });
    }

}
