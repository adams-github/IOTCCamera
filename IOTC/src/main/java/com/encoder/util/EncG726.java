//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.encoder.util;

public class EncG726 {
    public static final int G726_16 = 0;
    public static final int G726_24 = 1;
    public static final int G726_32 = 2;
    public static final int G726_40 = 3;
    public static final byte FORMAT_ULAW = 0;
    public static final byte FORMAT_ALAW = 1;
    public static final byte FORMAT_LINEAR = 2;
    public static final int API_ER_ANDROID_NULL = -10000;

    static {
        try {
            System.loadLibrary("G726Android");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(G726Android)," + var1.getMessage());
        }

    }

    public EncG726() {
    }

    public static native int g726_enc_state_create(byte var0, byte var1);

    public static native void g726_enc_state_destroy();

    public static native int g726_encode(byte[] var0, long var1, byte[] var3, long[] var4);
}
