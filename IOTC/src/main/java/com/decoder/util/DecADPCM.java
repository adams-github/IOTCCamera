//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.decoder.util;

public class DecADPCM {
    static {
        try {
            System.loadLibrary("ADPCMAndroid");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(ADPCMAndroid)," + var1.getMessage());
        }

    }

    public DecADPCM() {
    }

    public static native int ResetDecoder();

    public static native int Decode(byte[] var0, int var1, byte[] var2);
}
