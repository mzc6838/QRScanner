package xyz.mzc6838.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;

public class GetTextActivity extends AppCompatActivity {

    private Button reSelectButton;
    private Button copy_getText_Button;
    private EditText showText_getText_EditText;
    private ClipboardManager clipboardManager;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent innerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择图片");
        startActivityForResult(wrapperIntent, 333);

        this.setContentView(R.layout.activity_get_text);
        this.setTitle("识别文字");

        new Thread(()->{
            init();
            setOnClickListener();
        }).run();

    }

    public void init(){
        reSelectButton = findViewById(R.id.reSelect);
        copy_getText_Button = findViewById(R.id.copy_getText);
        showText_getText_EditText = findViewById(R.id.showText_getText);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mContext = this;

        new Thread(()->OCR.getInstance(mContext).initAccessToken(new OnResultListener<AccessToken>() {
                @Override
                public void onResult(AccessToken accessToken) {
                    String token = accessToken.getAccessToken();
                }

                @Override
                public void onError(OCRError ocrError) {}
            }, getApplicationContext())).run();

    }

    public void setOnClickListener(){
        reSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent innerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择图片");
                startActivityForResult(wrapperIntent, 333);
            }
        });
        copy_getText_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(GetTextActivity.this, "文本已复制", Toast.LENGTH_SHORT);
                ClipData clipData = ClipData.newPlainText(null, showText_getText_EditText.getText());
                clipboardManager.setPrimaryClip(clipData);
                toast.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 333){
            String[] address = {MediaStore.Images.Media.DATA};
            if (data == null) {
                showText_getText_EditText.setText("");
                showText_getText_EditText.clearFocus();
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
                            showText_getText_EditText.setText(sb.toString());
                        }

                        @Override
                        public void onError(OCRError ocrError) {
                        }
                    });
                }
            }
        }
    }
}
