package com.FingerprintCapture.CustomComponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by chicmic on 8/10/17.
 */

public class CustomButton extends androidx.appcompat.widget.AppCompatButton {
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

    private void initialize() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "PlayfairDisplaySC-Regular.otf");
            setTypeface(tf);
        }
    }
}
