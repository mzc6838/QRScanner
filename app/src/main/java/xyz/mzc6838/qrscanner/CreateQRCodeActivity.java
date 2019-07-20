package xyz.mzc6838.qrscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class CreateQRCodeActivity extends AppCompatActivity {

    Button QRCodeCreate;
    EditText editInfo;
    ImageView QRCode;
    Button cleanInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("生成二维码");

        this.setContentView(R.layout.activity_create_qrcode);

        init();
        setOnClickListener();

    }

    private void init(){
        QRCodeCreate = findViewById(R.id.QRCodeCreate);
        editInfo = findViewById(R.id.editInfo);
        QRCode = findViewById(R.id.QRCode);
        cleanInput = findViewById(R.id.cleanInput);
    }

    private void setOnClickListener(){
        QRCodeCreate.setOnClickListener((v)->{
            String contents = "";
            contents = editInfo.getText() + "";
            Bitmap bitmap = null;
            if(contents.length() == 0){
                Toast.makeText(this, "请输入要转换的文本", Toast.LENGTH_SHORT).show();
            }else {

                bitmap = ImageUtil.bitmatrixToBitmap(createQRCodeByString(contents));

                try{
                    Glide.with(this).load(bitmap).into(QRCode);
                }catch (Exception e){
                    e.printStackTrace();
                }

                //bitmap.recycle(); There's no need to call this method after Android version 2.3.3.
            }
        });
        cleanInput.setOnClickListener((v)->{
            editInfo.setText("");
        });
        QRCode.setOnLongClickListener((v)->{
            BitmapDrawable bitmapDrawable = (BitmapDrawable)QRCode.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            String fileName = Util.getRandomString(8) + ".png";

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "YYDoutu/" + fileName;

            try{
                OutputStream os = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(path));
            intent.setData(uri);
            CreateQRCodeActivity.this.sendBroadcast(intent);

            return true;
        });
    }

    private BitMatrix createQRCodeByString(String contents){
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = null;

        try{
            bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, 300, 300, hints);
        }catch (WriterException e){
            e.printStackTrace();
        }

        return bitMatrix;
    }
}
