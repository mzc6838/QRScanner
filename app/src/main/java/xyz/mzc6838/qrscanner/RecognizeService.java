package xyz.mzc6838.qrscanner;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by mzc6838 on 2019/5/10.
 */

public class RecognizeService {

    private String jsonFromServer;

    public interface ServiceListener{
        public void onResult(String result);
    }

    public void handleImage(Context context, String imagePath, final ServiceListener listener){

        //String img64 = ImageUtil.imageToBase64(imagePath);

        GeneralBasicParams params = new GeneralBasicParams();
        params.setImageFile(new File(imagePath));

        //Log.d("img64", img64);

        OCR.getInstance(context).recognizeGeneralBasic(params, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult generalResult) {

                jsonFromServer = generalResult.getJsonRes();

                //listener.onResult();

            }

            @Override
            public void onError(OCRError ocrError) {
            }
        });
    }

    public String getJsonFromServer(){
        return this.jsonFromServer;
    }
}
