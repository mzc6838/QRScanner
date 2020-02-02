package xyz.mzc6838.qrscanner.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import xyz.mzc6838.qrscanner.R;

public class TransferActivity extends AppCompatActivity {

    Button sendSocket;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_transfer);

        init();
        setOnClickListener();
    }

    private void init() {
        sendSocket = findViewById(R.id.sendSocket_Button);

    }

    private void setOnClickListener(){
        sendSocket.setOnClickListener((v)->
            new Thread(()->{
                try{
                    Socket socket = new Socket("192.168.31.189", 12344);
                    OutputStream os = socket.getOutputStream();
                    os.write("hello, here's android!".getBytes());
                    os.flush();
                    os.close();
                    socket.close();

                }catch (UnknownHostException uHE){
                    uHE.printStackTrace();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }

            }).start()

        );
    }
}
