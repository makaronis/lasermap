package com.makaroni.lasermap.utils;

import androidx.annotation.NonNull;

public class ByteUtils {

    private ByteUtils(){}

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static int byteToUnsignedInt(byte b) {
        return b & 0xff;
    }

    public static int parseTwoBytesInt(@NonNull byte[] bytes) {
        return ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
    }

    public static String bytesToHex(@NonNull byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    public static byte[] hexToBytes(@NonNull String hex) {
        int len = hex.length();
        byte[] data = new byte[len/2];
        int count = hex.length() / 2;
        for(int i = 0; i < count; i++){
            data[i] = (byte) ((Character.digit(hex.charAt(i*2), 16) << 4) + Character.digit(hex.charAt(i*2+1), 16));
        }
        return data;
    }
}
