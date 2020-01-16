package xyz.mzc6838.qrscanner.activity;

import android.content.Intent;
import android.database.Cursor;
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

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xyz.mzc6838.qrscanner.R;
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

        View view;
        Button selectImage, convertToBase64;
        ImageView imageToShow;
        EditText resultText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment_image_to_base64, null);

            selectImage = view.findViewById(R.id.selectImageToBase64_Button);
            convertToBase64 = view.findViewById(R.id.convertToBase64_Button);
            imageToShow = view.findViewById(R.id.theImageThatConvertToBase64_Image);

            setOnClickListener();

            return view;
        }

        private void setOnClickListener(){

            selectImage.setOnClickListener((v)->{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent wrapperIntent = Intent.createChooser(intent, "选择图片");
                startActivityForResult(wrapperIntent, 456);
            });

            convertToBase64.setOnClickListener((v)->{
                //TODO 转换为Base64方法实现
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 456){
                if(data != null){
                    Glide.with(this).load(data.getData()).into(imageToShow);
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
