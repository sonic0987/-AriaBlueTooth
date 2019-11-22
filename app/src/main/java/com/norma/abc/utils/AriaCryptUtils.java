package com.norma.abc.utils;

public class AriaCryptUtils {
    //by hi
    // hex string을 byte array로 변환
    public static byte[] hexStringToByteArray(final String s) {
        if (s == null) {
            return (new byte[]{});
        }

        if (s.length() % 2 != 0 || s.length() == 0) {
            return (new byte[]{});
        }

        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            try {
                data[i / 2] = (Integer.decode("0x" + s.charAt(i) + s.charAt(i + 1))).byteValue();
            } catch (NumberFormatException e) {
                return (new byte[]{});
            }
        }
        return data;
    }
    //by hi
    //byte array를 hex string으로 변환
    public static String byteArrayToHexString(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }
}
