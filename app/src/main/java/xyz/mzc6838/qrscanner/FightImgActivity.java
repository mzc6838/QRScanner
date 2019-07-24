package xyz.mzc6838.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

public class FightImgActivity extends AppCompatActivity {

    Button search;
    EditText editText;
    OkHttpClient okHttpClient;
    RecyclerView imageListRecyclerView;
    List<ImgInfo> imageInfoList;
    ImageAdapter imageAdapter;

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

        imageInfoList = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, imageInfoList);
        imageListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //imageListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        imageListRecyclerView.setAdapter(imageAdapter);

        imageAdapter.setOnItemLongClickListener(new ImageAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                String url = imageInfoList.get(position).getImage_url();

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
                String url = imageInfoList.get(position).getImage_url();

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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder()
                        .url("https://www.doutula.com/api/search?keyword=" + editText.getText() + "&mime=0&page=1")
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
                        responseFromServer = gson.fromJson(response.body().string(), ResponseFromServer.class);
                        if(responseFromServer.getStatus() == 0){
                            Looper.prepare();
                            Toast.makeText(FightImgActivity.this, "什么都没找到", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(inputMethodManager != null)
                            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        imageInfoList.clear();
                        imageInfoList.addAll(responseFromServer.data.getList());

                        //Log.d("imageInfoList", imageInfoList.get(0).getImage_url());

                        FightImgActivity.this.runOnUiThread(()->imageAdapter.notifyDataSetChanged());
                    }
                });
            }
        });
    }

    /**
     * 可能return空值
     */
    static public List<ImgInfo> arrayListToList(ImgInfo[] input){
        List<ImgInfo> result = new List<ImgInfo>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<ImgInfo> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean add(ImgInfo imgInfo) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends ImgInfo> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends ImgInfo> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public ImgInfo get(int i) {
                return null;
            }

            @Override
            public ImgInfo set(int i, ImgInfo imgInfo) {
                return null;
            }

            @Override
            public void add(int i, ImgInfo imgInfo) {

            }

            @Override
            public ImgInfo remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<ImgInfo> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<ImgInfo> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<ImgInfo> subList(int i, int i1) {
                return null;
            }
        };
        //result.addAll(input);
        for(int i = 0; i < input.length; i++){
            result.add(input[i]);
        }
        return result;
    }
}
