//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.decoder.util;

public class DecH264 {
    static {
        try {
            System.loadLibrary("H264Android");
        } catch (UnsatisfiedLinkError var1) {
            System.out.println("loadLibrary(H264Android)," + var1.getMessage());
        }

    }

    public DecH264() {
    }

    public static native int InitDecoder();

    public static native int UninitDecoder();

    public static native int DecoderNal(byte[] var0, int var1, int[] var2, byte[] var3, boolean var4);
}
