//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import android.graphics.Bitmap;

public interface IRegisterIOTCListener {
    void receiveFrameData(Camera var1, int var2, Bitmap var3);

    void receiveFrameInfo(Camera var1, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount);

    void receiveSessionInfo(Camera var1, int resultCode);

    void receiveChannelInfo(Camera var1, int sessionChannel, int resultCode);

    void receiveExtraInfo(Camera var1, int var2, int var3, int var4, int var5);

    void receiveIOCtrlData(Camera var1, int sessionChannel, int avIOCtrlMsgType, byte[] var4);
}
