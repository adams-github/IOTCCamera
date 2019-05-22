//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.decoder.util;

public class PCMA extends G711Base {
    public PCMA() {
    }

    public static void alaw2linear(byte[] alaw, short[] lin, int frames) {
        for(int i = 0; i < frames; ++i) {
            lin[i] = a[alaw[i] & 255];
        }

    }

    public static byte[] alaw2linear(byte[] alaw, int frames) {
        byte[] retArr = new byte[frames * 2];
        int retArrPos = 0;

        for(int i = 0; i < frames; ++i) {
            short linTmp = a[alaw[i] & 255];
            retArrPos = i * 2;
            retArr[retArrPos] = (byte)(linTmp & 255);
            retArr[retArrPos + 1] = (byte)(linTmp >> 8 & 255);
        }

        return retArr;
    }

    public static void alaw2linear(byte[] alaw, short[] lin, int frames, int mu) {
        for(int i = 0; i < frames; ++i) {
            lin[i] = a[alaw[i / mu] & 255];
        }

    }

    public static void linear2alaw(short[] lin, int offset, byte[] alaw, int frames) {
        for(int i = 0; i < frames; ++i) {
            alaw[i] = c[lin[i + offset] & '\uffff'];
        }

    }
}
