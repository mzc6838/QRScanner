package xyz.mzc6838.qrscanner.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.TransactionTooLargeException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xyz.mzc6838.qrscanner.R;
import xyz.mzc6838.qrscanner.util.ImageUtil;
import xyz.mzc6838.qrscanner.adapter.InnerFragmentAdapter;

public class ConvertWithBase64 extends AppCompatActivity {

    TabLayout mainTabLayout;
    ViewPager mainVP;
    InnerFragmentAdapter innerFragmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_convert_with_base64);
        this.setTitle("图片与Base64转换");
        init();
    }

    void init(){
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainVP = findViewById(R.id.mainVP);

        mainTabLayout.addTab(mainTabLayout.newTab().setText("Base64转图片"));
        mainTabLayout.addTab(mainTabLayout.newTab().setText("图片转Base64"));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ImageToBase64Class());
        fragments.add(new Base64ToImageClass());

        String[] titles = {"Base64转图片", "图片转Base64"};

        innerFragmentAdapter = new InnerFragmentAdapter(getSupportFragmentManager());
        innerFragmentAdapter.addFragments(titles, fragments);

        mainVP.setAdapter(innerFragmentAdapter);
        mainTabLayout.setupWithViewPager(mainVP);

    }

    public static class ImageToBase64Class extends Fragment {
        /**
         * 图片转Base64格式的Fragment
         * */

        Context mContext;

        View view;
        Button selectImage, copyTheResult;
        ImageView imageToShow;
        TextView resultText;

        ClipboardManager clipboardManager;

        public static String convertResult = "";

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            mContext = getContext();

            view = inflater.inflate(R.layout.fragment_image_to_base64, null);

            selectImage = view.findViewById(R.id.selectImageToBase64_Button);
            copyTheResult = view.findViewById(R.id.copyTheResult_Button);
            imageToShow = view.findViewById(R.id.theImageThatConvertToBase64_Image);
            resultText = view.findViewById(R.id.base64FromImage_TextView);

            clipboardManager = (ClipboardManager)mContext.getSystemService(CLIPBOARD_SERVICE);


            setOnClickListener();

            return view;
        }

        private void setOnClickListener(){

            selectImage.setOnClickListener((v)->{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Log.d("intent size", intent.toString());
                Intent wrapperIntent = Intent.createChooser(intent, "选择图片");


                startActivityForResult(wrapperIntent, 456);
            });


            copyTheResult.setOnClickListener((v)->{

                if(clipToClipboard() == -1){
                    Toast.makeText(mContext, "字符串过长无法复制x", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "Base64编码图片已复制", Toast.LENGTH_SHORT).show();
                }

            });
        }

        /**
         * 判断字符串是否可以被粘贴到剪贴板上
         * @return  1 可以
         *         -1 不可以(过长)
         * */
        private int clipToClipboard(){
            ClipData clipData = ClipData.newPlainText(null, convertResult);
            try {
                clipboardManager.setPrimaryClip(clipData);
            }catch (Exception e){
                return -1;
            }
            return 1;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            Log.d("request code", ""+requestCode);

            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 456){
                if(data != null){
                    Uri uri = data.getData();

                    Log.d("Uri", uri.toString());

                    Glide.with(this).load(uri).into(imageToShow);
                    String[] address = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(uri, address, null,null,null);
                    if(cursor.moveToFirst()){
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        final String path = cursor.getString(columnIndex);
                        new Thread(()->{
                            selectImage.setText("转换中...");
                            selectImage.setClickable(false);
                            convertResult = ImageUtil.imageToBase64(path);
                            resultText.setText(convertResult);
                            selectImage.setText("选择图片");
                            selectImage.setClickable(true);
                        }).start();
                    }
                }
            }
        }
    }

    public static class Base64ToImageClass extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_base64_to_image, null);
        }
    }
}
