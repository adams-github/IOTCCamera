//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import android.graphics.Bitmap;

public interface IRegisterIOTCListener {
    void receiveFrameData(Camera var1, int var2, Bitmap var3);

    void receiveFrameInfo(Camera var1, int var2, long var3, int var5, int var6, int var7, int var8);

    void receiveSessionInfo(Camera var1, int var2);

    void receiveChannelInfo(Camera var1, int var2, int var3);

    void receiveExtraInfo(Camera var1, int var2, int var3, int var4, int var5);

    void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4);
}
