package xyz.mzc6838.qrscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FightImgActivity extends AppCompatActivity {

    Button search;
    EditText editText;
    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_img);
        this.setTitle("斗图");

        init();

    }

    private void init(){
        search = findViewById(R.id.searchButton);
        editText = findViewById(R.id.inputKeyWordEditText);
        okHttpClient = new OkHttpClient();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder()
                        .url("https://www.doutula.com/api/search?keyword=" + editText.getText() + "&mime=0&page=1")
                        .build();

                Call call = okHttpClient.newCall(request);

                Log.d("button push", "onClick: ");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Response response = call.execute();
                            Log.i("from doutula", response.body().string());

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
}
