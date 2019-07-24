package xyz.mzc6838.qrscanner;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by mzc6838 on 2019/7/24.
 */

public class ColorPickService extends Service {

    private static int BROADCAST_SENT = 0;

    private  WindowManager windowManager;
    private View floatLayer;
    Button close;
    NotificationRec notificationRec;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();

    }

    private void init(){

        floatLayer = LayoutInflater.from(this).inflate(R.layout.service_color_picker, null);

        notificationRec = new NotificationRec();
        IntentFilter intentFilter = new IntentFilter("xyz.mzc6838.QRScanner.action.CLOSE_COLOR_PICKER");
        this.registerReceiver(notificationRec, intentFilter);

        int TYPE_FLAG = 0;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            TYPE_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                TYPE_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 10;
        params.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if(windowManager != null)
            windowManager.addView(floatLayer, params);

        setOnTouchListener(params);

        close = floatLayer.findViewById(R.id.qwe);
        close.setOnClickListener((v)->stopSelf());
    }

    private void setOnTouchListener(final WindowManager.LayoutParams params){
        floatLayer.findViewById(R.id.qwe).setOnTouchListener(new View.OnTouchListener() {

            private int startX;
            private int startY;
            private float startTouchX;
            private float startTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        startTouchX = event.getRawX();
                        startTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = startX + (int) (event.getRawX() - startTouchX);
                        params.y = startY + (int) (event.getRawY() - startTouchY);

                        windowManager.updateViewLayout(floatLayer, params);

                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatLayer != null)
            windowManager.removeView(floatLayer);
    }

    public class NotificationRec extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == "xyz.mzc6838.QRScanner.action.CLOSE_COLOR_PICKER"){
                stopSelf();
            }
        }
    }
}


