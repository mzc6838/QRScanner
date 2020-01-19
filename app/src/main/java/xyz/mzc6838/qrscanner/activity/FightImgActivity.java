package xyz.mzc6838.qrscanner.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import xyz.mzc6838.qrscanner.adapter.ImageAdapter;
import xyz.mzc6838.qrscanner.R;
import xyz.mzc6838.qrscanner.baseClass.ResponseFromServer;

public class FightImgActivity extends AppCompatActivity {

    Button search;
    EditText editText;
    OkHttpClient okHttpClient;
    RecyclerView imageListRecyclerView;
    List<String> imageInfoList;
    ImageAdapter imageAdapter;
    InputMethodManager inputMethodManager;

    int more = 0;
    int page = 1;
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_img);
        this.setTitle("斗图");

        init();

    }

    private void init(){

        /*处理分享时错误（未知错误）*/
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        search = findViewById(R.id.searchButton);
        editText = findViewById(R.id.inputKeyWordEditText);
        okHttpClient = new OkHttpClient();
        imageListRecyclerView = findViewById(R.id.imageList);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imageInfoList = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, imageInfoList);
        imageListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //imageListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        imageListRecyclerView.setAdapter(imageAdapter);
        imageListRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY)-> {
            if(!imageListRecyclerView.canScrollVertically(1)){
                if(more == 1){

                    page++;

                    String urlH = "http://img.mzc6838.xyz:8000/search?";
                    String urlKeyWord = "keyword=" + keyword;
                    String urlPage = "&page=" + page;
                    String url = urlH + urlKeyWord + urlPage;

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @EverythingIsNonNull
                        @Override
                        public void onFailure(Call call, IOException e) {}

                        @EverythingIsNonNull
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Gson gson = new Gson();
                            ResponseFromServer responseFromServer = gson.fromJson(response.body().string(), ResponseFromServer.class);

                            more = responseFromServer.getMore();

                            imageInfoList.addAll(responseFromServer.getData());

                            FightImgActivity.this.runOnUiThread(()->imageAdapter.notifyDataSetChanged());

                        }
                    });
                }else {
                    more = 0;
                    Toast.makeText(this, "没有更多了", Toast.LENGTH_LONG).show();

                }
            }
        });

        imageAdapter.setOnItemLongClickListener(new ImageAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                String url = imageInfoList.get(position);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {

                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call call, IOException e) {}

                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        InputStream is = null;
                        byte buff[] = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "YYDoutu/doutu";
                        File file = new File(savePath);
                        File file1 = new File(savePath, getNameFromUrl(url));
                        if(!file.exists()){
                            file.mkdirs();
                        }

                        try{
                            is = response.body().byteStream();
                            fos = new FileOutputStream(file1);
                            while ((len = is.read(buff)) != -1){
                                fos.write(buff, 0, len);
                            }
                            fos.flush();
                        }catch (Exception e){

                        }finally {
                            try{
                                if(is != null){
                                    is.close();
                                }
                            }catch (Exception e){}
                            try{
                                if(fos != null)
                                    fos.close();
                            }catch (Exception e){}
                        }

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file1);
                        intent.setData(uri);
                        FightImgActivity.this.sendBroadcast(intent);

                        Looper.prepare();
                        Toast.makeText(FightImgActivity.this, "已保存", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    public String getNameFromUrl(String url){
                        return url.substring(url.lastIndexOf("/") + 1);
                    }
                });
                return true;
            }
        });
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = imageInfoList.get(position);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        InputStream is = null;
                        byte buff[] = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "YYDoutu";
                        File file = new File(savePath);
                        File file1 = new File(savePath, getNameFromUrl(url));
                        if(!file.exists()){
                            file.mkdirs();
                        }

                        try{
                            is = response.body().byteStream();
                            fos = new FileOutputStream(file1);
                            while ((len = is.read(buff)) != -1){
                                fos.write(buff, 0, len);
                            }
                            fos.flush();
                        }catch (Exception e){
                        }finally {
                            try{
                                if(is != null){
                                    is.close();
                                }
                            }catch (Exception e){

                            }
                            try{
                                if(fos != null)
                                    fos.close();
                            }catch (Exception e){

                            }
                        }

                        Uri uri = Uri.fromFile(file1);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Intent share_intent = new Intent(Intent.ACTION_SEND);
                        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
                        share_intent.setType("image/*");
                        intent.setData(uri);
                        FightImgActivity.this.sendBroadcast(intent);
                        startActivity(Intent.createChooser(share_intent, "分享到"));
                    }

                    public String getNameFromUrl(String url){
                        return url.substring(url.lastIndexOf("/") + 1);
                    }
                });
            }
        });

        search.setOnClickListener((v)-> {
            searchFromServer();
            if(inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        });

        editText.setOnEditorActionListener((textView, actionId, keyEvent)->{

            if(        actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent != null
                    && keyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                    && keyEvent.ACTION_DOWN == keyEvent.getAction()){

                Log.d("editEdit", "click ");

                if(!editText.getText().toString().equals("")){
                    if(inputMethodManager != null)
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    searchFromServer();
                }
            }

            return false;
        });
    }

    /**
     * 从服务器获取图片列表
     * */
    private void searchFromServer(){
        page = 1;
        keyword = "" + editText.getText();
        imageListRecyclerView.scrollToPosition(0);

        String urlH = "http://img.mzc6838.xyz:8000/search?";
        String urlKeyWord = "keyword=" + keyword;
        String urlPage = "&page=" + page;
        String url = urlH + urlKeyWord + urlPage;

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {}

            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                ResponseFromServer responseFromServer;
                String dataFromServer = response.body().string();
                responseFromServer = gson.fromJson(dataFromServer, ResponseFromServer.class);

                //Log.d("message", dataFromServer);

                if(responseFromServer.getStatus() == 0){
                    Looper.prepare();
                    Toast.makeText(FightImgActivity.this, "什么都没找到", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                more = responseFromServer.getMore();

                imageInfoList.clear();
                imageInfoList.addAll(responseFromServer.getData());

                //Log.d("imageInfoList", imageInfoList.get(0).getImage_url());

                FightImgActivity.this.runOnUiThread(()->imageAdapter.notifyDataSetChanged());
            }
        });
    }
}
