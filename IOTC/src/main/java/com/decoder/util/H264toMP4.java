//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.decoder.util;

public class H264toMP4 {
    static {
        System.loadLibrary("mp4");
    }

    public H264toMP4() {
    }

    public static native int mp4init(String var0, int var1, int var2);

    public static native void mp4packvideo(byte[] var0, int var1, int var2, int var3);

    public static native void mp4close();
}
