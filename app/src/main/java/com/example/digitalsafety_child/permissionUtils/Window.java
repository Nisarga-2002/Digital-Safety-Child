package com.example.digitalsafety_child.permissionUtils;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.digitalsafety_child.R;

public class Window {
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;

    public Window(Context context) {
        this.context = context;

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );

        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.window_camerax, null);

        View closeButton = mView.findViewById(R.id.window_close);
        if (closeButton != null) {
            closeButton.setOnClickListener(view -> close());
        }
    }

    public void open() {
        try {
            if (mView.getParent() == null) {
                mWindowManager.addView(mView, mParams);
            }
        } catch (Exception e) {
            Log.e("Window", "Error adding view to window: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (mView.getParent() != null) {
                mWindowManager.removeView(mView);
            }
        } catch (Exception e) {
            Log.e("Window", "Error removing view from window: " + e.getMessage());
        }
    }
}