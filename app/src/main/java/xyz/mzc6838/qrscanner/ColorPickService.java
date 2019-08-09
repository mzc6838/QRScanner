package xyz.mzc6838.qrscanner;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.nio.ByteBuffer;

/**
 * Created by mzc6838 on 2019/8/9.
 */

public class ColorPickService extends Service {

    private WindowManager windowManager;
    private View floatLayer;
    //Button close;
    NotificationRec notificationRec;

    SurfaceView mSurface;

    Button mButton;

    MediaProjectionManager mediaProjectionManager;
    MediaProjection mediaProjection = null;
    VirtualDisplay virtualDisplay;
    ImageReader imageReader = null;
    ImageView imageView;

    int resultCode;
    Intent data;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultCode = intent.getIntExtra("resultCode", -1);
        data = intent.getParcelableExtra("data");

        Log.d("", "onStartCommand: 1");


        mediaProjection = ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(resultCode, data);
        imageReader = ImageReader.newInstance(500, 500, PixelFormat.RGBA_8888, 2);

        Log.d("resultCode", resultCode + "\n");
        Log.d("data", data.toString());

        mediaProjection.createVirtualDisplay("mediaProjection", 500, 500, 300,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,null);


        SystemClock.sleep(1000);
        Image image = imageReader.acquireNextImage();
        Image.Plane[] plane = image.getPlanes();
        ByteBuffer byteBuffer = plane[0].getBuffer();
        int pixelStride = plane[0].getPixelStride();
        int rowStride = plane[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(byteBuffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());

        imageView.setImageBitmap(bitmap);
        image.close();


        return Service.START_STICKY;
    }

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

        imageView = floatLayer.findViewById(R.id.serviceImage);
        mButton = floatLayer.findViewById(R.id.reTake);

        notificationRec = new NotificationRec();

        IntentFilter intentFilter = new IntentFilter("xyz.mzc6838.QRScanner.action.CLOSE_COLOR_PICKER");
        this.registerReceiver(notificationRec, intentFilter);

        int TYPE_FLAG;
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

        //close = floatLayer.findViewById(R.id.qwe);
        //close.setOnClickListener((v)->stopSelf());

        mButton.setOnClickListener((v)->{
            Image image = imageReader.acquireNextImage();
            Image.Plane[] plane = image.getPlanes();
            ByteBuffer byteBuffer = plane[0].getBuffer();
            int pixelStride = plane[0].getPixelStride();
            int rowStride = plane[0].getRowStride();
            int rowPadding = rowStride - pixelStride * image.getWidth();
            Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(byteBuffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());

            imageView.setImageBitmap(bitmap);
            image.close();
        });
    }

    private void setOnTouchListener(final WindowManager.LayoutParams params){
        floatLayer.findViewById(R.id.serviceImage).setOnTouchListener(new View.OnTouchListener() {

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
        if(notificationRec != null)
            unregisterReceiver(notificationRec);
    }

    public class NotificationRec extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == "xyz.mzc6838.QRScanner.action.CLOSE_COLOR_PICKER"){
                stopSelf();
            }
        }
    }
}

