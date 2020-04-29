package com.FingerprintCapture.CustomComponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by chicmic on 8/10/17.
 */

public class CustomButton extends Button {
    public CustomButton(Context context) {
        super(context);
        initialize();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "PlayfairDisplaySC-Regular.otf");
            setTypeface(tf);
        }
    }
}
