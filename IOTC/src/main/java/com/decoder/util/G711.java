//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.decoder.util;

public class G711 {
    public static final int G711_16 = 0;
    public static final int G711_24 = 1;
    public static final int G711_32 = 2;
    public static final int G711_40 = 3;
    public static final byte FORMAT_ULAW = 0;
    public static final byte FORMAT_ALAW = 1;
    public static final byte FORMAT_LINEAR = 2;
    public static final int API_ER_ANDROID_NULL = -10000;

    static {
        try {
            System.loadLibrary("g711");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(G711)," + var1.getMessage());
        }

    }

    public G711() {
    }

    public static void test() {
        System.out.print("test ");
    }

    public static native int g711decode(byte[] var0, byte[] var1, int var2);

    public static native int g711encode(byte[] var0, byte[] var1, int var2);
}
