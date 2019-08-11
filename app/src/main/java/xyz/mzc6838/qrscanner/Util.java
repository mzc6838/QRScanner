package xyz.mzc6838.qrscanner;

import android.content.Context;
import android.graphics.Color;

import java.util.Random;

public class Util {

    /**
     * 构造随机字符串
     * @param length 随机字符串长度
     * @return String 生成的随机字符
     * */
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 十进制颜色转十六进制字符串
     * @param color 十进制颜色数值
     * @return String 十六进制颜色字符串
     * */
    public static String getColorString(int color){
        String colorHex = "";
        while (color != 0){
            switch (color % 16){
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    colorHex = (color % 16) + colorHex;
                    break;
                case 10:
                    colorHex = "A" + colorHex;
                    break;
                case 11:
                    colorHex = "B" + colorHex;
                    break;
                case 12:
                    colorHex = "C" + colorHex;
                    break;
                case 13:
                    colorHex = "D" + colorHex;
                    break;
                case 14:
                    colorHex = "E" + colorHex;
                    break;
                case 15:
                    colorHex = "F" + colorHex;
                    break;
            }
            color /= 16;
        }
        return colorHex;
    }

    /**
     * 取反色
     * @param r 原始红色
     * @param g 原始绿色
     * @param b 原始蓝色
     * @return String 取反色后的十六进制格式颜色
     * */
    public static String getInvertColorString(int r, int g, int b){
        r = r ^ 0xff;
        g = g ^ 0xff;
        b = b ^ 0xff;
        String result = "#";
        result += decToHex(r) + decToHex(g) + decToHex(b);
        return result;
    }

    /**
     * 取反色
     * @param color 原始颜色
     * @return int 取反色后的颜色
     * */
    public static int getInvertColor(int color){
        int r = (int)(Color.red(color)) ^ 0xff;
        int g = (int)(Color.green(color)) ^ 0xff;
        int b = (int)(Color.blue(color)) ^ 0xff;

        long t = Color.pack(r, g, b);

        return Color.rgb(r, g, b);
    }

    /**
     * dp转px
     * @param context
     * @param dp
     * @return
     * */
    public static int dp2px(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * 十进制转十六进制
     * @param Dec 传入的十进制
     * @return String 十进制对应的十六进制
     * */
    public static String decToHex(int Dec){
        String Hex = "";
        while (Dec != 0){
            switch(Dec % 16){
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    Hex = (Dec % 16) + Hex;
                    break;
                case 10:
                    Hex = "A" + Hex;
                    break;
                case 11:
                    Hex = "B" + Hex;
                    break;
                case 12:
                    Hex = "C" + Hex;
                    break;
                case 13:
                    Hex = "D" + Hex;
                    break;
                case 14:
                    Hex = "E" + Hex;
                    break;
                case 15:
                    Hex = "F" + Hex;
                    break;
            }
            Dec /= 16;
        }
        return Hex;
    }

}
