//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.encoder.util;

public class EncADPCM {
    static {
        try {
            System.loadLibrary("ADPCMAndroid");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(ADPCMAndroid)," + var1.getMessage());
        }

    }

    public EncADPCM() {
    }

    public static native int ResetEncoder();

    public static native int Encode(byte[] var0, int var1, byte[] var2);
}
