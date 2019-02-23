package com.asha.vrlib.strategy.interactive;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;

import com.asha.vrlib.MD360Director;

import java.util.List;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class TouchStrategy extends AbsInteractiveStrategy {

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;
    private static final String TAG = "TouchStrategy";

    public TouchStrategy(List<MD360Director> directorList) {
        super(directorList);
    }

    @Override
    public void onResume(Context context) {}

    @Override
    public void onPause(Context context) {}

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        boolean handled = false;
        for (MD360Director director : getDirectorList()){
            handled |= handleTouchEventInner(director,event);
        }
        return handled;
    }

    private boolean handleTouchEventInner(MD360Director director, MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();
            float previousX = director.getPreviousX();
            float previousY = director.getPreviousY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float deltaX = (x - previousX) / sDensity * sDamping ;
                float deltaY = (y - previousY) / sDensity * sDamping ;
                director.setDeltaX(director.getDeltaX() + deltaX);
                director.setDeltaY(director.getDeltaY() + deltaY);
            }
            director.setPreviousX(x);
            director.setPreviousY(y);
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void on(Activity activity) {
        for (MD360Director director : getDirectorList()){
            director.reset();
        }
    }

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }
}
