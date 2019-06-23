package xyz.mzc6838.qrscanner;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

public class ScanActivity extends AppCompatActivity {

    Button rescanButton;
    Button copyButton;
    EditText scanResult;
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        this.setTitle("扫码");

        init();
        setOnClickListener();

        intentToScan();

    }

    public void init(){

        rescanButton = findViewById(R.id.rescan);
        copyButton = findViewById(R.id.copy);
        scanResult = findViewById(R.id.showText);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

    }

    public void setOnClickListener(){

        rescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }else{
                    Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 111);
                }
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(ScanActivity.this, "文本已复制", Toast.LENGTH_SHORT);
                ClipData clipData = ClipData.newPlainText(null, scanResult.getText());
                clipboardManager.setPrimaryClip(clipData);
                toast.show();
            }
        });

    }

    public void intentToScan(){

        if(ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }else{
            Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 111);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (111):{
                if(resultCode == 213){

                    Log.d(ScanActivity.class.getSimpleName(), "run here!");

                    if(data.getExtras().getString("scan_result") == null){
                        scanResult.setText("");
                        scanResult.clearFocus();
                    }else {
                        scanResult.setText(data.getExtras().getString("scan_result"));
                        scanResult.clearFocus();
                    }
                }

                else {
                    if (data != null) {
                        scanResult.setText(data.getStringExtra("codedContent"));
                        scanResult.clearFocus();
                    } else {
                        scanResult.setText("");
                        scanResult.clearFocus();
                    }
                }
                break;
            }
            default:break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "扫码需要相机使用权限", Toast.LENGTH_LONG).show();
            }else{
                Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 111);
            }
        }
    }
}
