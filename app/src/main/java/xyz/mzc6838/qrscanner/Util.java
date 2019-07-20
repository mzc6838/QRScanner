package xyz.mzc6838.qrscanner;

import java.util.Random;

public class Util {

    /**
     * 构造随机字符串
     * @param length 随机字符串长度
     * @return 生成的随机字符
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

}
