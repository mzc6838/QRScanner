package xyz.mzc6838.qrscanner.activity;

import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import xyz.mzc6838.qrscanner.R;
import xyz.mzc6838.qrscanner.service.ColorPickService;

public class MainActivity extends Activity {

    Button scan;
    Button getWord;
    Button fightImg;
    Button createQRCode;
    Button colorPicker;
    Toolbar mainToolbar;

    MediaProjectionManager mMediaProjectionManager;


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
        mainToolbar = findViewById(R.id.mainToolbar);

        scan.setOnClickListener((v)->{
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }else{
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        getWord.setOnClickListener((v)->{

            Intent innerIntent = new Intent(MainActivity.this,GetTextActivity.class);
            startActivity(innerIntent);
        });

        fightImg.setOnClickListener((v)->{

                //Toast.makeText(MainActivity.this, "斗图功能已停用！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, FightImgActivity.class);
            startActivity(intent);
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
            if(mMediaProjectionManager != null){
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 5);
            }

        });

        mainToolbar.inflateMenu(R.menu.toolbar_manu);
        mainToolbar.setOnMenuItemClickListener((item)->{
            switch (item.getItemId()){
                case(R.id.share):
                    generateQQShare();
                break;
//                case (R.id.changeTheme):
//                    //TODO 切换夜间模式
//                    break;
                default:
                    break;
            }
            return false;
        });


    }

    void generateQQShare(){
        Tencent t = Tencent.createInstance("1109890979", this);
        final Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "QRScanner");
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "自用app，实现二维码扫描、图片文字提取、表情包搜索功能");
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://github.com/mzc6838/QRScanner");
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "QRScanner_");
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://toothless.mzc6838.xyz/blog/wp-content/uploads/2019/10/3.jpg");
        t.shareToQQ(this, bundle, new IUiListener() {
            @Override
            public void onComplete(Object o) {
            }

            @Override
            public void onError(UiError uiError) {
                Log.d("onError:", uiError.errorMessage);
            }

            @Override
            public void onCancel() {

            }
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
            default:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_manu, menu);
        return true;
    }
}