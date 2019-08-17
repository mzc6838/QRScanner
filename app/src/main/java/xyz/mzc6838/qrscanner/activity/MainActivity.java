package xyz.mzc6838.qrscanner.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import xyz.mzc6838.qrscanner.R;
import xyz.mzc6838.qrscanner.service.ColorPickService;

public class MainActivity extends AppCompatActivity {

    Button scan;
    Button getWord;
    Button fightImg;
    Button createQRCode;
    Button colorPicker;

    MediaProjectionManager mMediaProjectionManager;
    //static MediaProjection mMediaProjection;

    SurfaceView mSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        scan = findViewById(R.id.scan);
        getWord = findViewById(R.id.getWord);
        fightImg = findViewById(R.id.fightImgButton);
        createQRCode = findViewById(R.id.createQRCode);
        colorPicker = findViewById(R.id.colorPickerButton);

        //mSurfaceView = findViewById(R.id.mSurface);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }else{
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivity(intent);
                }
            }
        });
        getWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(MainActivity.this,GetTextActivity.class);
                startActivity(innerIntent);
            }
        });
        fightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FightImgActivity.class);
                startActivity(intent);
            }
        });

        createQRCode.setOnClickListener((v)->{
            Intent intent = new Intent(MainActivity.this, CreateQRCodeActivity.class);
            startActivity(intent);
        });

        colorPicker.setOnClickListener((v)->{

            Notification notification;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel notificationChannel = new NotificationChannel("mzc6838", "close_color_picker", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(false);
                notificationChannel.setShowBadge(true);
                if(notificationManager != null)
                    notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent intent1 = new Intent("xyz.mzc6838.QRScanner.action.CLOSE_COLOR_PICKER");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notification = new Notification.Builder(this, "mzc6838")
                    .setContentTitle("QRScanner")
                    .setContentText("点击这里关闭取色器")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_my_round)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher_my_round)
                    .setContentIntent(pendingIntent)
                    .build();
            }else{
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("QRScanner")
                        .setContentText("点击这里关闭取色器")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher_my_round)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .build();
            }

            if(notificationManager != null)
                notificationManager.notify(255, notification);

            mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 5);
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case (5):{
                if(resultCode == RESULT_OK){
                    //mMediaProjection = mMediaProjectionManager.getMediaProjection(requestCode, data);
                    Intent intent = new Intent(MainActivity.this, ColorPickService.class);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    intent.putExtra("width", displayMetrics.widthPixels);
                    intent.putExtra("height", displayMetrics.heightPixels);
                    intent.putExtra("dpi", displayMetrics.densityDpi);

                    intent.putExtra("resultCode", resultCode);
                    intent.putExtra("data", data);

                    this.startService(intent);
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "扫码需要相机使用权限", Toast.LENGTH_LONG).show();
            }else{
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        }
    }
}
