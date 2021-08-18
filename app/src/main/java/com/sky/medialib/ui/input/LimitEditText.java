package com.sky.medialib.ui.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.sky.medialib.R;
import com.sky.medialib.util.ToastUtils;


@SuppressLint({"AppCompatCustomView"})
public class LimitEditText extends EditText {

    public final int f9182a = 140;
    private int limitSize;
    private InputFilter[] inputFilters;

    private class FormatFilter implements InputFilter {

        public int maxLength;

        public FormatFilter(int i) {
            this.maxLength = i;
        }

        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            float f;
            float a;
            if (spanned == null || TextUtils.isEmpty(spanned)) {
                f = 0.0f;
            } else {
                a = LimitEditText.getLength(spanned.toString());
                if (a >= ((float) this.maxLength)) {
                    ToastUtils.INSTANCE.showToast(String.format(LimitEditText.this.getContext().getString(R.string.text_input_most), new Object[]{this.maxLength + ""}));
                    return "";
                }
                f = a;
            }
            if (charSequence == null || TextUtils.isEmpty(charSequence)) {
                a = 0.0f;
            } else {
                a = LimitEditText.getLength(charSequence.toString());
            }
            if (a <= 0.0f) {
                return "";
            }
            if (f + a <= ((float) this.maxLength)) {
                return charSequence;
            }
            charSequence = LimitEditText.getLength(charSequence.toString(), Math.min(((float) this.maxLength) - f, a));
            ToastUtils.INSTANCE.showToast(String.format(LimitEditText.this.getContext().getString(R.string.text_input_most), new Object[]{this.maxLength + ""}));
            return charSequence;
        }
    }

    public LimitEditText(Context context) {
        super(context);
        init(null);
    }

    public LimitEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public LimitEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.LimitEditText);
        this.limitSize = obtainStyledAttributes.getInteger(R.styleable.LimitEditText_limitSize, 140);
        obtainStyledAttributes.recycle();
        this.inputFilters = new InputFilter[]{new FormatFilter(this.limitSize)};
        setFilters(this.inputFilters);
    }

    public float getValidLength() {
        return ((float) this.limitSize) - LimitEditText.getLength(getText().toString());
    }

    public static float getLength(String str) {
        char[] toCharArray = str.toCharArray();
        float f = 0.0f;
        for (char c : toCharArray) {
            if (c >= 913 && c <= 65509) {
                f += 1.0f;
            } else if (c <= 255) {
                f += 0.5f;
            }
        }
        return f;
    }

    public static String getLength(String str, float f) {
        char[] toCharArray = str.toCharArray();
        float f2 = 0.0f;
        for (int i = 0; i < toCharArray.length; i++) {
            char c = toCharArray[i];
            if (c >= 913 && c <= 65509) {
                f2 += 1.0f;
            } else if (c <= 255) {
                f2 += 0.5f;
            }
            if (f2 >= f) {
                return str.substring(0, i + 1);
            }
        }
        return str;
    }
}
