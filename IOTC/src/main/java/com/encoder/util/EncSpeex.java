//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.encoder.util;

public class EncSpeex {
    static {
        try {
            System.loadLibrary("SpeexAndroid");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(SpeexAndroid)," + var1.getMessage());
        }

    }

    public EncSpeex() {
    }

    public static native int InitEncoder(int var0);

    public static native int UninitEncoder();

    public static native int Encode(short[] var0, int var1, byte[] var2);
}
