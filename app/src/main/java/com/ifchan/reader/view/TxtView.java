package com.ifchan.reader.view;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by daily on 12/17/17.
 */

public class TxtView extends TextView{
    private MyTouchEvent mMyTouchEvent;

    public TxtView(Context context) {
        super(context);
    }

    public TxtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TxtView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TxtView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float max_x = getWidth();
            float x = event.getX();
            if (x < max_x / 3) {
                setText(mMyTouchEvent.loadPreviousText());
            } else if (x > max_x / 3 * 2) {
                setText(mMyTouchEvent.loadNextText());
            }
        }
        return super.onTouchEvent(event);
    }

    public void setMyTouchEvent(MyTouchEvent myTouchEvent) {
        mMyTouchEvent = myTouchEvent;
    }

    public interface MyTouchEvent {
        String loadPreviousText();

        String loadNextText();
    }
}
