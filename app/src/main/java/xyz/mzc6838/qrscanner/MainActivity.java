package xyz.mzc6838.qrscanner;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends Activity {

    Button scan;
    Button copy;
    Button album;
    Button getWord;
    EditText showText;
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        scan = findViewById(R.id.scan);
        showText = findViewById(R.id.showText);
        copy = findViewById(R.id.copy);
        album = findViewById(R.id.album);
        getWord = findViewById(R.id.getWord);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        showText.clearFocus();

        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError ocrError) {}
        }, getApplicationContext());

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 111);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MainActivity.this, "文本已复制", Toast.LENGTH_SHORT);
                ClipData clipData = ClipData.newPlainText(null, showText.getText());
                clipboardManager.setPrimaryClip(clipData);
                toast.show();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent innerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, 222);
            }
        });
        getWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择图片");
                startActivityForResult(wrapperIntent, 333);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111) {
            if (data != null) {
                showText.setText(data.getStringExtra("codedContent"));
                showText.clearFocus();
            } else {
                showText.setText("");
                showText.clearFocus();
            }
        } else if (requestCode == 222) {
            String[] address = {MediaStore.Images.Media.DATA};
            if (data == null) {
                showText.setText("");
                showText.clearFocus();
            } else {
                Cursor cursor = this.getContentResolver().query(data.getData(), address, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String photoPath = cursor.getString(columnIndex);
                    //Log.d("photoPath: ", photoPath);
                    String result = parseQRCode(photoPath);
                    if (result != null) {
                        showText.setText(result);
                        showText.clearFocus();
                    } else {
                        showText.setText("");
                        showText.clearFocus();
                    }
                }
            }
        } else if (requestCode == 333) {
            String[] address = {MediaStore.Images.Media.DATA};
            if (data == null) {
                showText.setText("");
                showText.clearFocus();
            } else {
                Cursor cursor = this.getContentResolver().query(data.getData(), address, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    final String photoPath = cursor.getString(columnIndex);
                    Log.d("photoPath: ", photoPath);

                    GeneralBasicParams params = new GeneralBasicParams();
                    params.setImageFile(new File(photoPath));

                    final StringBuffer sb = new StringBuffer();

                    OCR.getInstance(this).recognizeGeneralBasic(params, new OnResultListener<GeneralResult>() {
                        @Override
                        public void onResult(GeneralResult generalResult) {
                            for (WordSimple wordSimple : generalResult.getWordList()) {
                                sb.append(wordSimple.getWords());
                                sb.append("\n");
                            }
                            //Log.i("StringBuffer", sb.toString());
                            showText.setText(sb.toString());
                        }

                        @Override
                        public void onError(OCRError ocrError) {

                        }
                    });

                }
            }
        }
    }

    public  String parseQRCode(String bitmapPath){
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, null);
        String result=parseQRCode(bitmap);
        return result;
    }

    public  String parseQRCode(Bitmap bmp){
        //bmp=comp(bmp);//bitmap压缩  如果不压缩的话在低配置的手机上解码很慢

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        QRCodeReader reader = new QRCodeReader();
        Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);//优化精度
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");//解码设置编码方式为：utf-8
        try {
            Result result = reader.decode(new BinaryBitmap(
                    new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))), hints);
            return result.getText();
        } catch (NotFoundException e) {
            Toast.makeText(this, "未发现二维码", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
