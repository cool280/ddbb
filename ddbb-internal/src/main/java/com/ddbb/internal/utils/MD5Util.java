/**
 * TODO
 * @author jxl
 * @date 2014-6-21  ����12:00:57
 */
package com.ddbb.internal.utils;

import java.security.MessageDigest;

public class MD5Util {

	public final static String encode(String s,boolean upperCase) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        String ret="";
        try {
            byte[] btInput = s.getBytes();

            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            mdInst.update(btInput);

            byte[] md = mdInst.digest();

            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            ret = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(upperCase){
			return ret.toUpperCase();
		}else{
			return ret.toLowerCase();
		}
    }

	public final static String encode(String s){
		return encode(s,false);
	}
    public static void main(String[] args) {
        System.out.println(MD5Util.encode("20121221"));
        System.out.println(MD5Util.encode("水电费"));
        System.out.println(MD5Util.encode("haoda1122"));
    }
}
