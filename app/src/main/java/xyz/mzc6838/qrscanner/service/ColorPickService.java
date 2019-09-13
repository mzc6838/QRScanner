package xyz.mzc6838.qrscanner.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;

import xyz.mzc6838.qrscanner.R;
import xyz.mzc6838.qrscanner.util.Util;

/**
 * Created by mzc6838 on 2019/8/9.
 */

public class ColorPickService extends Service {

    private WindowManager windowManager;
    private View floatLayer;
    NotificationRec notificationRec;

    MediaProjection mediaProjection = null;
    ImageReader imageReader = null;
    ImageView imageView, aimLineImage;
    Bitmap bitmap, bigBitmap, aimLineBitmap;
    TextView colorText, colorTextHex;
    RelativeLayout relativeLayout;

    int resultCode;
    int width;
    int height;
    int dpi;
    Intent data;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultCode = intent.getIntExtra("resultCode", -1);
        data = intent.getParcelableExtra("data");
        width = intent.getIntExtra("width", 0);
        height = intent.getIntExtra("height", 0);
        dpi = intent.getIntExtra("dpi", 0);

        mediaProjection = ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(resultCode, data);
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);

        mediaProjection.createVirtualDisplay("mediaProjection", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,null);


        SystemClock.sleep(1000);
        Image image = imageReader.acquireLatestImage();
        Image.Plane[] plane = image.getPlanes();
        ByteBuffer byteBuffer = plane[0].getBuffer();
        int pixelStride = plane[0].getPixelStride();
        int rowStride = plane[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();
        bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(byteBuffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());

        imageView.setImageBitmap(bitmap);
        image.close();

        return Service.START_NOT_STICKY;
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
        colorText = floatLayer.findViewById(R.id.colorRange);
        colorTextHex = floatLayer.findViewById(R.id.colorRangeHex);
        relativeLayout = floatLayer.findViewById(R.id.colorPickerBackground);
        aimLineImage = floatLayer.findViewById(R.id.aimLine);

        relativeLayout.setVisibility(View.VISIBLE);

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
        params.x = 100;
        params.y = 100;
        params.height = Util.dp2px(getBaseContext(), 150);
        params.width = Util.dp2px(getBaseContext(), 150);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if(windowManager != null)
            windowManager.addView(floatLayer, params);

        setOnTouchListener(params);

        aimLineBitmap = Bitmap.createBitmap(Util.dp2px(getBaseContext(), 150), Util.dp2px(getBaseContext(), 150), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(aimLineBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2.5f);
        canvas.drawLine(0, aimLineBitmap.getHeight() / 2, aimLineBitmap.getWidth(), aimLineBitmap.getHeight() / 2, paint);
        canvas.drawLine(aimLineBitmap.getWidth() / 2, 0, aimLineBitmap.getWidth() / 2, aimLineBitmap.getHeight(), paint);

        aimLineImage.setImageBitmap(aimLineBitmap);

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

                        params.height = Util.dp2px(getBaseContext(), 150);
                        params.width = Util.dp2px(getBaseContext(), 150);

                        Image image = imageReader.acquireLatestImage();
                        Image.Plane[] plane = image.getPlanes();
                        ByteBuffer byteBuffer = plane[0].getBuffer();
                        int pixelStride = plane[0].getPixelStride();
                        int rowStride = plane[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * image.getWidth();
                        bitmap = Bitmap.createBitmap((image.getWidth() + rowPadding / pixelStride),
                                image.getHeight(),
                                Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(byteBuffer);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());
                        bitmap = Util.drawBg4Bitmap(100, Color.BLACK, bitmap);

                        imageView.setImageBitmap(bitmap);
                        image.close();

                        aimLineImage.setImageBitmap(aimLineBitmap);

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = startX + (int) (event.getRawX() - startTouchX);
                        params.y = startY + (int) (event.getRawY() - startTouchY);

                        bigBitmap = Bitmap.createBitmap(bitmap, (int)event.getRawX(), (int)event.getRawY(), 50, 50);
                        imageView.setImageBitmap(bigBitmap);
                        int colorDec = bigBitmap.getPixel(bigBitmap.getWidth() / 2, bigBitmap.getHeight() / 2);

                        relativeLayout.setBackgroundColor(colorDec);
                        colorText.setText("r:" + Color.red(colorDec) + ", g:" + Color.green(colorDec) + ", b:" + Color.blue(colorDec));
                        colorTextHex.setText(Util.getColorString(Color.red(colorDec), Color.green(colorDec), Color.blue(colorDec)));
                        colorTextHex.setTextColor(Util.getInvertColor(colorDec));
                        colorText.setTextColor(Util.getInvertColor(colorDec));

                        params.height = Util.dp2px(getBaseContext(), 150);
                        params.width = Util.dp2px(getBaseContext(), 150);
                        aimLineImage.setZ(999);
                        aimLineImage.setImageBitmap(aimLineBitmap);
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

