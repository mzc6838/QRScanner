package xyz.mzc6838.qrscanner.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mzc6838 on 2019/5/10.
 */

public class ImageUtil {

    /**
     * 图片转base64字符串
     * @param  path String
     * @return base64 String
     */
    public static String imageToBase64(String path) {
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = Base64.encodeToString(data, Base64.DEFAULT);

        String ext;
        int dot = path.lastIndexOf('.');
        ext = path.substring(dot + 1);

        return "data:image/" + ext + ";base64," + result.replace("\n", "");


    }

    public static File createBase64(Context context, String img64){
        String filesDir = context.getFilesDir().getAbsolutePath();
        try {
            File file = new File(filesDir + "image64");
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(img64.getBytes());
            fo.flush();
            fo.close();

            return file;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BitMatrix转Bitmap
     * @param bitMatrix BitMatrix
     * @return bitmap Bitmap
     */
    public static Bitmap bitMatrixToBitmap(BitMatrix bitMatrix){
        int height = bitMatrix.getHeight();
        int width = bitMatrix.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    /**
     * 将byte数组转为Bitmap
     * @param in 输入的byte数组
     * @return Bitmap 获得的Bitmap
     *         null   输入的byte不合法
     */
    public static Bitmap bytesToBitmap(byte[] in){
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
        }catch (Exception e){
            return null;
        }
        return bitmap;
    }

}
