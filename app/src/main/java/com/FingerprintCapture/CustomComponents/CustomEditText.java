package com.FingerprintCapture.CustomComponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by chicmic on 8/10/17.
 */

public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
        initialize();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Walkway_Bold.ttf");
            setTypeface(tf);
        }
    }
}
