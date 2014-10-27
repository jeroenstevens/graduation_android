package com.example.jeroenstevens.graduation_android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.droidparts.widget.ClearableEditText;

public class InfiniteClearableEditText extends ClearableEditText {
    private Context mContext;

    public InfiniteClearableEditText(Context context) {
        super(context);
        init(context);
    }

    public InfiniteClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfiniteClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setClearIconVisible(true);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // Keep showing when out of focus
        super.onFocusChange(v, true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Request focus when tapped from the outside and show keyboard
        v.requestFocus();

        InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(this, 0);

        return super.onTouch(v, event);
    }
}
