//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.AudioRecord;
import android.media.AudioTrack;

import com.decoder.util.DecADPCM;
import com.decoder.util.DecG726;
import com.decoder.util.DecH264;
import com.decoder.util.DecMp3;
import com.decoder.util.DecMpeg4;
import com.decoder.util.DecSpeex;
import com.decoder.util.H264toMP4;
import com.decoder.util.PCMA;
import com.encoder.util.EncADPCM;
import com.encoder.util.EncG726;
import com.encoder.util.EncSpeex;
import com.tutk.IOTC.AVIOCTRLDEFs.SFrameInfo;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlAVStream;
import com.tutk.IOTC.RDCTRLDEFs.RDTCommand;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Camera {
    private static volatile int isCameraInit = 0;
    private static int maxCameraLimit = 8;
    public static final int DEFAULT_AV_CHANNEL = 0;
    public static final int DEFAULT_FRAMECOUNT = 30;
    public static final int CONNECTION_STATE_NONE = 0;
    public static final int CONNECTION_STATE_CONNECTING = 1;
    public static final int CONNECTION_STATE_CONNECTED = 2;
    public static final int CONNECTION_STATE_DISCONNECTED = 3;
    public static final int CONNECTION_STATE_UNKNOWN_DEVICE = 4;
    public static final int CONNECTION_STATE_WRONG_PASSWORD = 5;
    public static final int CONNECTION_STATE_TIMEOUT = 6;
    public static final int CONNECTION_STATE_UNSUPPORTED = 7;
    public static final int CONNECTION_STATE_CONNECT_FAILED = 8;
    public static final int CONNECTION_STATE_CLIENT_NOSUPPORT = 9;
    public static final int RECONNECT_TIMES = 3;
    public static final int EXTRA_EVENT_RDSENDER = 14;
    public static final int RDSENDER_STATE_START = 4113;
    public static final int RDSENDER_STATE_STOP = 4114;
    public static final int RDSENDER_STATE_SENDING = 4115;
    private final Object c = new Object();
    private Camera.c d = null;
    private Camera.b e = null;
    private Camera.j f = null;
    private Camera.f g = null;
    private volatile int nIOTCSessionID = -1;
    private volatile int sessionMode = -1;
    private volatile int sid;
    private boolean k = false;
    private AudioTrack audioTrack = null;//音频播放器
    private int m = 0;
    private boolean n;
    private String o;
    private int frameRate;
    private int q = 0;
    private int r = 0;
    private String uid = "";
    private String t = "";
    private List<IRegisterIOTCListener> iRegisterIOTCListenerList = Collections.synchronizedList(new Vector());
    private List<Camera.ChannelInfo> channelInfoList = Collections.synchronizedList(new Vector());
    private static boolean w = true;
    private static boolean x = true;
    public static int nFlow_total_FPS_count = 0;
    public static int nFlow_total_FPS_count_noClear = 0;

    public static String getVersion() {
        return "0.2.2.4";
    }

    public Camera() {
    }

    public int getSessionMode() {
        return this.sessionMode;
    }

    public long getChannelServiceType(int avChannel) {
        long ret = 0L;
        List var4 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var6 = this.channelInfoList.iterator();

            while(var6.hasNext()) {
                Camera.ChannelInfo channelInfo = (Camera.ChannelInfo)var6.next();
                if (channelInfo.getChannel() == avChannel) {
                    ret = channelInfo.getServiceType();
                    break;
                }
            }

            return ret;
        }
    }

    public boolean registerIOTCListener(IRegisterIOTCListener listener) {
        boolean result = false;
        if (!this.iRegisterIOTCListenerList.contains(listener)) {
            MLog.i("IOTCamera", "register IOTC listener");
            this.iRegisterIOTCListenerList.add(listener);
            result = true;
        }

        return result;
    }

    public boolean unregisterIOTCListener(IRegisterIOTCListener listener) {
        boolean result = false;
        if (this.iRegisterIOTCListenerList.contains(listener)) {
            MLog.i("IOTCamera", "unregister IOTC listener");
            this.iRegisterIOTCListenerList.remove(listener);
            result = true;
        }

        return result;
    }

    public static synchronized st_LanSearchInfo[] SearchLAN() {
        int[] num = new int[1];
        st_LanSearchInfo[] result = null;
        result = IOTCAPIs.IOTC_Lan_Search(num, 2000);
        return result;
    }

    public static void setMaxCameraLimit(int limit) {
        maxCameraLimit = limit;
    }

    public static synchronized int init() {
        int nRet = 0;
        if (isCameraInit == 0) {
            int port = (int)(10000L + System.currentTimeMillis() % 10000L);
            nRet = IOTCAPIs.IOTC_Initialize2(port);
            MLog.i("IOTCamera", "IOTC_Initialize2() returns " + nRet);
            if (nRet < 0) {
                return nRet;
            }

            if (x) {
                RDTAPIs.RDT_Initialize();
            }

            nRet = AVAPIs.avInitialize(maxCameraLimit * 16);
            MLog.i("IOTCamera", "avInitialize() = " + nRet);
            if (nRet < 0) {
                return nRet;
            }
        }

        ++isCameraInit;
        return nRet;
    }

    public static synchronized int uninit() {
        int nRet = 0;
        if (isCameraInit > 0) {
            --isCameraInit;
            if (isCameraInit == 0) {
                nRet = AVAPIs.avDeInitialize();
                MLog.i("IOTCamera", "avDeInitialize() returns " + nRet);
                nRet = IOTCAPIs.IOTC_DeInitialize();
                MLog.i("IOTCamera", "IOTC_DeInitialize() returns " + nRet);
                if (x) {
                    RDTAPIs.RDT_DeInitialize();
                }
            }
        }

        return nRet;
    }

    public boolean isSessionConnected() {
        return this.nIOTCSessionID >= 0;
    }

    public boolean isChannelConnected(int avChannel) {
        boolean result = false;
        List var3 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var5 = this.channelInfoList.iterator();

            while(var5.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var5.next();
                if (avChannel == ch.getChannel()) {
                    result = this.nIOTCSessionID >= 0 && ch.getAvIndex() >= 0;
                    break;
                }
            }

            return result;
        }
    }

    public void sendIOCtrl(int avChannel, int type, byte[] data) {
        List var4 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var6 = this.channelInfoList.iterator();

            while(var6.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var6.next();
                if (avChannel == ch.getChannel()) {
                    ch.ioCtrlQueue.addData(type, data);
                }
            }

        }
    }

    public void connect(String uid) {
        this.uid = uid;
        MLog.i("==checklive==", "enterthis  connect");
        if (this.d == null) {
            MLog.i("==checklive==", "enterthis create connect");
            this.d = new Camera.c(0);
            this.d.start();
        } else if (!this.d.c) {
            this.d.run();
        }

        if (this.e == null) {
            MLog.i("==checklive==", "enterthis create connect");
            this.e = new Camera.b();
            this.e.start();
        }

        MLog.i("==checklive==", "exit this create connect");
    }

    public void connect(String uid, String pwd) {
        this.uid = uid;
        this.t = pwd;
        if (this.d == null) {
            this.d = new Camera.c(1);
            this.d.start();
        }

        if (this.e == null) {
            this.e = new Camera.b();
            this.e.start();
        }

    }

    public void disconnect() {
        List var1 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var3 = this.channelInfoList.iterator();

            while(var3.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var3.next();
                this.stopSpeaking(ch.getChannel());
                if (ch.i != null) {
                    ch.i.a();
                }

                if (ch.o != null) {
                    ch.o.a();
                }

                if (ch.n != null) {
                    ch.n.a();
                }

                if (ch.m != null) {
                    ch.m.a();
                }

                if (ch.l != null) {
                    ch.l.a();
                }

                if (ch.j != null) {
                    ch.j.a();
                }

                if (ch.k != null) {
                    ch.k.a();
                }

                if (ch.l != null) {
                    try {
                        ch.l.interrupt();
                        ch.l.join();
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }

                    ch.l = null;
                }

                if (ch.m != null) {
                    try {
                        ch.m.interrupt();
                        ch.m.join();
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }

                    ch.m = null;
                }

                if (ch.o != null) {
                    try {
                        ch.o.interrupt();
                        ch.o.join();
                    } catch (InterruptedException var12) {
                        var12.printStackTrace();
                    }

                    ch.o = null;
                }

                if (ch.n != null) {
                    try {
                        ch.n.interrupt();
                        ch.n.join();
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }

                    ch.n = null;
                }

                if (ch.j != null) {
                    try {
                        ch.j.interrupt();
                        ch.j.join();
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    ch.j = null;
                }

                if (ch.k != null) {
                    try {
                        ch.k.interrupt();
                        ch.k.join();
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    ch.k = null;
                }

                if (ch.i != null && ch.i.isAlive()) {
                    try {
                        ch.i.interrupt();
                        ch.i.join();
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                }

                ch.i = null;
                ch.audioAVFrameContainer.clear();
                ch.audioAVFrameContainer = null;
                ch.videoAVFrameContainer.clear();
                ch.videoAVFrameContainer = null;
                ch.ioCtrlQueue.clear();
                ch.ioCtrlQueue = null;
                if (ch.getAvIndex() >= 0) {
                    AVAPIs.avClientStop(ch.getAvIndex());
                    MLog.i("IOTCamera", "avClientStop(avIndex = " + ch.getAvIndex() + ")");
                }
            }
        }

        this.channelInfoList.clear();
        Object var16 = this.c;
        synchronized(this.c) {
            this.c.notify();
        }

        if (this.e != null) {
            this.e.a();
        }

        if (this.d != null) {
            this.d.a();
        }

        if (this.e != null) {
            try {
                this.e.interrupt();
                this.e.join();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.e = null;
        }

        if (this.d != null && this.d.isAlive()) {
            try {
                this.d.interrupt();
                this.d.join();
            } catch (InterruptedException var5) {
                var5.printStackTrace();
            }
        }

        this.d = null;
        if (this.nIOTCSessionID >= 0) {
            IOTCAPIs.IOTC_Session_Close(this.nIOTCSessionID);
            MLog.i("IOTCamera", "IOTC_Session_Close(nSID = " + this.nIOTCSessionID + ")");
            this.nIOTCSessionID = -1;
        }

        this.sessionMode = -1;
    }

    public void start(int avChannel, String viewAccount, String viewPasswd) {
        Camera.ChannelInfo session = null;
        List var5 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var7 = this.channelInfoList.iterator();

            while(var7.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var7.next();
                if (ch.getChannel() == avChannel) {
                    session = ch;
                    break;
                }
            }
        }

        if (session == null) {
            Camera.ChannelInfo ch = new Camera.ChannelInfo(avChannel, viewAccount, viewPasswd);
            this.channelInfoList.add(ch);
            ch.i = new Camera.l(ch);
            ch.i.start();
            ch.j = new Camera.h(ch);
            ch.j.start();
            ch.k = new Camera.k(ch);
            ch.k.start();
        } else {
            if (session.i == null) {
                session.i = new Camera.l(session);
                session.i.start();
            }

            if (session.j == null) {
                session.j = new Camera.h(session);
                session.j.start();
            }

            if (session.k == null) {
                session.k = new Camera.k(session);
                session.k.start();
            }
        }

    }

    public void stop(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            int idx = -1;

            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (ch.getChannel() == avChannel) {
                    idx = i;
                    this.stopSpeaking(ch.getChannel());
                    if (ch.i != null) {
                        ch.i.a();
                    }

                    if (ch.o != null) {
                        ch.o.a();
                    }

                    if (ch.n != null) {
                        ch.n.a();
                    }

                    if (ch.m != null) {
                        ch.m.a();
                    }

                    if (ch.l != null) {
                        ch.l.a();
                    }

                    if (ch.j != null) {
                        ch.j.a();
                    }

                    if (ch.k != null) {
                        ch.k.a();
                    }

                    if (ch.l != null) {
                        try {
                            ch.l.interrupt();
                            ch.l.join();
                        } catch (InterruptedException var13) {
                            var13.printStackTrace();
                        }

                        ch.l = null;
                    }

                    if (ch.m != null) {
                        try {
                            ch.m.interrupt();
                            ch.m.join();
                        } catch (InterruptedException var12) {
                            var12.printStackTrace();
                        }

                        ch.m = null;
                    }

                    if (ch.o != null) {
                        try {
                            ch.o.interrupt();
                            ch.o.join();
                        } catch (InterruptedException var11) {
                            var11.printStackTrace();
                        }

                        ch.o = null;
                    }

                    if (ch.n != null) {
                        try {
                            ch.n.interrupt();
                            ch.n.join();
                        } catch (InterruptedException var10) {
                            var10.printStackTrace();
                        }

                        ch.n = null;
                    }

                    if (ch.j != null) {
                        try {
                            ch.j.interrupt();
                            ch.j.join();
                        } catch (InterruptedException var9) {
                            var9.printStackTrace();
                        }

                        ch.j = null;
                    }

                    if (ch.k != null) {
                        try {
                            ch.k.interrupt();
                            ch.k.join();
                        } catch (InterruptedException var8) {
                            var8.printStackTrace();
                        }

                        ch.k = null;
                    }

                    if (ch.i != null && ch.i.isAlive()) {
                        try {
                            ch.i.interrupt();
                            ch.i.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }
                    }

                    ch.i = null;
                    ch.audioAVFrameContainer.clear();
                    ch.audioAVFrameContainer = null;
                    ch.videoAVFrameContainer.clear();
                    ch.videoAVFrameContainer = null;
                    ch.ioCtrlQueue.clear();
                    ch.ioCtrlQueue = null;
                    if (ch.getAvIndex() >= 0) {
                        AVAPIs.avClientStop(ch.getAvIndex());
                        MLog.i("IOTCamera", "avClientStop(avIndex = " + ch.getAvIndex() + ")");
                    }
                    break;
                }
            }

            if (idx >= 0) {
                this.channelInfoList.remove(idx);
            }

        }
    }

    public void startShow(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (ch.getChannel() == avChannel) {
                    ch.videoAVFrameContainer.clear();
                    if (ch.l == null) {
                        ch.l = new Camera.i(ch);
                        ch.l.start();
                    }

                    if (ch.n == null) {
                        ch.n = new Camera.e(ch);
                        ch.n.start();
                    }
                    break;
                }
            }

        }
    }

    public void stopShow(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (ch.getChannel() == avChannel) {
                    if (ch.l != null) {
                        ch.l.a();

                        try {
                            ch.l.interrupt();
                            ch.l.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }

                        ch.l = null;
                    }

                    if (ch.n != null) {
                        ch.n.a();

                        try {
                            ch.n.interrupt();
                            ch.n.join();
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        ch.n = null;
                    }

                    ch.videoAVFrameContainer.clear();
                    break;
                }
            }

        }
    }

    public void startSpeaking(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (ch.getChannel() == avChannel) {
                    ch.audioAVFrameContainer.clear();
                    if (this.f == null) {
                        this.f = new Camera.j(ch);
                        this.f.start();
                    }
                    break;
                }
            }

        }
    }

    public void stopSpeaking(int avChannel) {
        if (this.f != null) {
            this.f.a();

            try {
                this.f.interrupt();
                this.f.join();
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }

            this.f = null;
        }

    }

    public void startListening(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (avChannel == ch.getChannel()) {
                    ch.audioAVFrameContainer.clear();
                    if (ch.m == null) {
                        ch.m = new Camera.g(ch);
                        ch.m.start();
                    }
                    break;
                }
            }

        }
    }

    public void stopListening(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (avChannel == ch.getChannel()) {
                    if (ch.m != null) {
                        ch.m.a();

                        try {
                            ch.m.interrupt();
                            ch.m.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }

                        ch.m = null;
                    }

                    if (ch.o != null) {
                        ch.o.a();

                        try {
                            ch.o.interrupt();
                            ch.o.join();
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        ch.o = null;
                    }

                    ch.audioAVFrameContainer.clear();
                    break;
                }
            }

        }
    }

    public Bitmap Snapshot(int avChannel) {
        Bitmap result = null;
        List var3 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (avChannel == ch.getChannel()) {
                    result = ch.shotBmp;
                    break;
                }
            }

            return result;
        }
    }

    public Bitmap QuitSnapshot(int avChannel) {
        Bitmap result = null;
        List var3 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (avChannel == ch.getChannel()) {
                    if (ch.quickShotBmp != null) {
                        result = ch.quickShotBmp;
                    }
                    break;
                }
            }

            return result;
        }
    }

    public void StartRecordvideo(String mp4Name) {
        this.n = true;
        this.o = mp4Name;
    }

    public void StopRecordvideo() {
        this.n = false;
    }

    public int getDispFrmPreSec() {
        int tmp = this.q;
        return tmp;
    }

    public int getRecvFrmPreSec() {
        int tmp = this.r;
        return tmp;
    }

    private synchronized boolean a(int sampleRateInHz, int channel, int dataBit, int codec_id) {
        if (!this.k) {
//            int channelConfig = true;
//            int audioFormat = true;
//            int mMinBufSize = false;
            int channelConfig = channel == 1 ? 3 : 2;
            int audioFormat = dataBit == 1 ? 2 : 3;
            int mMinBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            if (mMinBufSize != -2 && mMinBufSize != -1) {
                try {
                    this.audioTrack = new AudioTrack(3, sampleRateInHz, channelConfig, audioFormat, mMinBufSize, 1);
                    MLog.i("IOTCamera", "init AudioTrack with SampleRate:" + sampleRateInHz + " " + (dataBit == 1 ? String.valueOf(16) : String.valueOf(8)) + "bit " + (channel == 1 ? "Stereo" : "Mono"));
                } catch (IllegalArgumentException var9) {
                    var9.printStackTrace();
                    return false;
                }

                if (codec_id == 141) {
                    DecSpeex.InitDecoder(sampleRateInHz);
                } else if (codec_id == 142) {
                    int bit = dataBit == 1 ? 16 : 8;
                    DecMp3.InitDecoder(sampleRateInHz, bit);
                } else if (codec_id != 139 && codec_id != 140) {
                    if (codec_id == 143) {
                        DecG726.g726_dec_state_create((byte)0, (byte)2);
                    }
                } else {
                    DecADPCM.ResetDecoder();
                }

                this.audioTrack.setStereoVolume(1.0F, 1.0F);
                this.audioTrack.play();
                this.k = true;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private synchronized void a(int codec_id) {
        if (this.k) {
            if (this.audioTrack != null) {
                this.audioTrack.stop();
                this.audioTrack.release();
                this.audioTrack = null;
            }

            if (codec_id == 141) {
                DecSpeex.UninitDecoder();
            } else if (codec_id == 142) {
                DecMp3.UninitDecoder();
            } else if (codec_id == 143) {
                DecG726.g726_dec_state_destroy();
            }

            this.k = false;
        }

    }

    static String a(byte[] raw, int size) {
        if (raw == null) {
            return null;
        } else {
            StringBuilder hex = new StringBuilder(2 * raw.length);
            int len = 0;
            byte[] var7 = raw;
            int var6 = raw.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                byte b = var7[var5];
                hex.append("0123456789ABCDEF".charAt((b & 240) >> 4)).append("0123456789ABCDEF".charAt(b & 15)).append(" ");
                ++len;
                if (len >= size) {
                    break;
                }
            }

            return hex.toString();
        }
    }

    public Bitmap optimizeBitmap(byte[] resource, int maxWidth, int maxHeight) {
        Bitmap result = null;
        int length = resource.length;
        Options options = new Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        result = BitmapFactory.decodeByteArray(resource, 0, length, options);
        return result;
    }

    public static Bitmap scaleThumBitmap(Bitmap src, int srcWidth, int srcHeight, int maxWidth, int maxHeight) {
        Matrix matrix = new Matrix();
        matrix.postScale((float)maxWidth, (float)maxHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);
        return resizeBitmap;
    }

    public void setSendIOIntval(int sendintval, int recvintval) {
        List var3 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var5 = this.channelInfoList.iterator();

            while(var5.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var5.next();
                if (ch.j != null) {
                    ch.j.a(recvintval);
                }

                if (ch.k != null) {
                    ch.k.a(sendintval);
                }
            }
        }

        Object var8 = this.c;
        synchronized(this.c) {
            this.c.notify();
        }
    }

    public void disconnect1() {
        List var1 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var3 = this.channelInfoList.iterator();

            while(var3.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var3.next();
                this.stopSpeaking(ch.getChannel());
                if (ch.o != null) {
                    ch.o.a();
                }

                if (ch.n != null) {
                    ch.n.a();
                }

                if (ch.m != null) {
                    ch.m.a();
                }

                if (ch.l != null) {
                    ch.l.a();
                }

                if (ch.j != null) {
                    ch.j.a();
                }

                if (ch.k != null) {
                    ch.k.a();
                }

                if (ch.l != null) {
                    try {
                        ch.l.interrupt();
                        ch.l.join();
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }

                    ch.l = null;
                }

                if (ch.m != null) {
                    try {
                        ch.m.interrupt();
                        ch.m.join();
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }

                    ch.m = null;
                }

                if (ch.o != null) {
                    try {
                        ch.o.interrupt();
                        ch.o.join();
                    } catch (InterruptedException var12) {
                        var12.printStackTrace();
                    }

                    ch.o = null;
                }

                if (ch.n != null) {
                    try {
                        ch.n.interrupt();
                        ch.n.join();
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }

                    ch.n = null;
                }

                if (ch.j != null) {
                    try {
                        ch.j.interrupt();
                        ch.j.join();
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    ch.j = null;
                }

                if (ch.k != null) {
                    try {
                        ch.k.interrupt();
                        ch.k.join();
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    ch.k = null;
                }

                if (ch.i != null && ch.i.isAlive()) {
                    try {
                        ch.i.interrupt();
                        ch.i.join();
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                }

                ch.i = null;
                ch.audioAVFrameContainer.clear();
                ch.audioAVFrameContainer = null;
                ch.videoAVFrameContainer.clear();
                ch.videoAVFrameContainer = null;
                ch.ioCtrlQueue.clear();
                ch.ioCtrlQueue = null;
                if (ch.getAvIndex() >= 0) {
                    AVAPIs.avClientStop(ch.getAvIndex());
                }
            }
        }

        this.channelInfoList.clear();
        Object var16 = this.c;
        synchronized(this.c) {
            this.c.notify();
        }

        if (this.e != null) {
            this.e.a();
        }

        if (this.d != null) {
            this.d.a();
        }

        if (this.e != null) {
            try {
                this.e.interrupt();
                this.e.join();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.e = null;
        }

        if (this.d != null && this.d.isAlive()) {
            try {
                this.d.interrupt();
                this.d.join();
            } catch (InterruptedException var5) {
                var5.printStackTrace();
            }
        }

        this.d = null;
        if (this.nIOTCSessionID >= 0) {
            IOTCAPIs.IOTC_Session_Close(this.nIOTCSessionID);
            this.nIOTCSessionID = -1;
        }

        this.sessionMode = -1;
    }

    public void startRDDownLoad(int avChannel, String LocalPath, String RemotePaht) {
        List var4 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            if (x) {
                for(int i = 0; i < this.channelInfoList.size(); ++i) {
                    Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                    if (ch.getChannel() == avChannel) {
                        if (this.g == null) {
                            this.g = new Camera.f(ch, LocalPath, RemotePaht);
                            this.g.start();
                        }
                        break;
                    }
                }

            }
        }
    }

    public void stopRDTServ(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            if (x) {
                if (this.g != null) {
                    this.g.a();

                    try {
                        this.g.interrupt();
                        this.g.join();
                    } catch (InterruptedException var4) {
                        var4.printStackTrace();
                    }

                    this.g = null;
                }

            }
        }
    }

    public static String getString(byte[] data) {
        StringBuilder sBuilder = new StringBuilder();

        for(int i = 0; i < data.length && data[i] != 0; ++i) {
            sBuilder.append((char)data[i]);
        }

        return sBuilder.toString();
    }

    private class ChannelInfo {
        private volatile int channel = -1;
        private volatile int avIndex = -1;
        private long serviceType = -1L;
        private String view_acc;
        private String view_pwd;
        private int v;
        public Camera.IOCtrlQueue ioCtrlQueue;
        public AVFrameContainer videoAVFrameContainer;
        public AVFrameContainer audioAVFrameContainer;
        public Bitmap shotBmp;
        public Bitmap quickShotBmp;
        public int frameRate;
        public int outBufSize;
        public int h;
        public Camera.l i = null;
        public Camera.h j = null;
        public Camera.k k = null;
        public Camera.i l = null;
        public Camera.g m = null;
        public Camera.e n = null;
        public Camera.d o = null;

        public ChannelInfo(int channel, String view_acc, String view_pwd) {
            this.channel = channel;
            this.view_acc = view_acc;
            this.view_pwd = view_pwd;
            this.serviceType = -1L;
            this.frameRate = this.outBufSize = this.h = 0;
            this.shotBmp = null;
            this.quickShotBmp = null;
            this.ioCtrlQueue = Camera.this.new IOCtrlQueue();
            this.videoAVFrameContainer = new AVFrameContainer();
            this.audioAVFrameContainer = new AVFrameContainer();
        }

        public int getChannel() {
            return this.channel;
        }

        public synchronized int getAvIndex() {
            return this.avIndex;
        }

        public synchronized void setAvIndex(int idx) {
            this.avIndex = idx;
        }

        public synchronized long getServiceType() {
            return this.serviceType;
        }

        public synchronized int d() {
            return this.v;
        }

        public synchronized void b(int codec) {
            this.v = codec;
        }

        public synchronized void setServiceType(long serviceType) {
            this.serviceType = serviceType;
            this.v = (serviceType & 4096L) == 0L ? 138 : 138;
        }

        public String getView_acc() {
            return this.view_acc;
        }

        public String getView_pwd() {
            return this.view_pwd;
        }
    }

    private class IOCtrlQueue {
        LinkedList<IOCtrlSet> IOCtrlList;

        private IOCtrlQueue() {
            this.IOCtrlList = new LinkedList();
        }

        public synchronized boolean isEmpty() {
            return this.IOCtrlList.isEmpty();
        }

        public synchronized void addData(int type, byte[] data) {
            if (this.IOCtrlList.size() < 5) {
                this.IOCtrlList.addLast(new Camera.IOCtrlQueue.IOCtrlSet(type, data));
            } else {
                this.clear();
                this.IOCtrlList.addLast(new Camera.IOCtrlQueue.IOCtrlSet(type, data));
            }

        }

        public synchronized void addData(int avIndex, int type, byte[] data) {
            this.IOCtrlList.addLast(new Camera.IOCtrlQueue.IOCtrlSet(avIndex, type, data));
        }

        public synchronized Camera.IOCtrlQueue.IOCtrlSet getIOCtrlSet() {
            return this.IOCtrlList.isEmpty() ? null : (Camera.IOCtrlQueue.IOCtrlSet)this.IOCtrlList.removeFirst();
        }

        public synchronized void clear() {
            if (!this.IOCtrlList.isEmpty()) {
                this.IOCtrlList.clear();
            }

        }

        public class IOCtrlSet {
            public int IOCtrlType;
            public byte[] IOCtrlBuf;

            public IOCtrlSet(int avIndex, int type, byte[] buf) {
                this.IOCtrlType = type;
                this.IOCtrlBuf = buf;
            }

            public IOCtrlSet(int type, byte[] buf) {
                this.IOCtrlType = type;
                this.IOCtrlBuf = buf;
            }
        }
    }

    private class b extends Thread {
        private boolean b;
        private Object c;
        private int d;

        private b() {
            this.b = false;
            this.c = new Object();
            this.d = 0;
        }

        public void a() {
            this.b = false;
            Object var1 = this.c;
            synchronized(this.c) {
                try {
                    this.c.notify();
                } catch (Throwable var3) {
                    var3.printStackTrace();
                }

            }
        }

        public void run() {
            super.run();
            this.b = true;
            St_SInfo stSInfo = new St_SInfo();
            boolean var2 = false;

            while(this.b && Camera.this.nIOTCSessionID < 0) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(1000L);
                    }
                } catch (Exception var11) {
                    var11.printStackTrace();
                }
            }

            while(true) {
                while(this.b) {
                    Object object;
                    if (Camera.this.nIOTCSessionID >= 0) {
                        int ret = IOTCAPIs.IOTC_Session_Check(Camera.this.nIOTCSessionID, stSInfo);
                        if (ret >= 0) {
                            if (Camera.this.sessionMode == stSInfo.Mode) {
                                object = this.c;
                                synchronized(object) {
                                    try {
                                        this.c.wait(5000L);
                                    } catch (InterruptedException var8) {
                                        var8.printStackTrace();
                                    }
                                    continue;
                                }
                            }

                            Camera.this.sessionMode = stSInfo.Mode;
                        } else {
                            IRegisterIOTCListener listener;
                            int i;
                            if (ret != -23 && ret != -13) {
                                MLog.i("IOTCamera", "IOTC_Session_Check(" + Camera.this.nIOTCSessionID + ") Failed return " + ret);

                                for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                                    listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                                    listener.receiveSessionInfo(Camera.this, 8);
                                }
                            } else {
                                for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                                    listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                                    listener.receiveSessionInfo(Camera.this, 6);
                                }
                            }
                        }
                    }

                    object = this.c;
                    synchronized(this.c) {
                        try {
                            this.c.wait(5000L);
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }
                    }
                }

                MLog.i("IOTCamera", "===ThreadCheckDevStatus exit===");
                return;
            }
        }
    }

    private class c extends Thread {
        private int b = -1;
        private boolean c = false;
        private Object d = new Object();

        public c(int connType) {
            this.b = connType;
        }

        public void a() {
            this.c = false;
            if (Camera.this.sid >= 0) {
                IOTCAPIs.IOTC_Connect_Stop_BySID(Camera.this.sid);
            }

            Object var1 = this.d;
            synchronized(this.d) {
                this.d.notify();
            }
        }

        public void run() {
            int nRetryForIOTC_Conn = 0;
            this.c = true;

            label136:
            while(this.c && Camera.this.nIOTCSessionID < 0) {
                int i;
                IRegisterIOTCListener listener;
                for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                    listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                    listener.receiveSessionInfo(Camera.this, 1);
                }

                if (this.b == 0) {
                    Camera.this.sid = IOTCAPIs.IOTC_Get_SessionID();
                    if (Camera.this.sid >= 0) {
                        Camera.this.nIOTCSessionID = IOTCAPIs.IOTC_Connect_ByUID_Parallel(Camera.this.uid, Camera.this.sid);
                    }

                    Camera.this.sid = -1;
                } else {
                    if (this.b != 1) {
                        return;
                    }

                    Camera.this.sid = IOTCAPIs.IOTC_Get_SessionID();
                    if (Camera.this.sid >= 0) {
                        Camera.this.nIOTCSessionID = IOTCAPIs.IOTC_Connect_ByUID_Parallel(Camera.this.uid, Camera.this.sid);
                    }

                    Camera.this.sid = -1;
                }

                if (Camera.this.nIOTCSessionID >= 0) {
                    for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                        listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                        listener.receiveSessionInfo(Camera.this, 2);
                    }

                    synchronized(Camera.this.c) {
                        Camera.this.c.notify();
                    }
                } else if (Camera.this.nIOTCSessionID == -20) {
                    try {
                        Object var11 = this.d;
                        synchronized(this.d) {
                            this.d.wait(1000L);
                        }
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                } else {
                    if (Camera.this.nIOTCSessionID != -15 && Camera.this.nIOTCSessionID != -10 && Camera.this.nIOTCSessionID != -19 && Camera.this.nIOTCSessionID != -13) {
                        if (Camera.this.nIOTCSessionID == -36 || Camera.this.nIOTCSessionID == -37) {
                            i = 0;

                            while(true) {
                                if (i >= Camera.this.iRegisterIOTCListenerList.size()) {
                                    break label136;
                                }

                                listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                                listener.receiveSessionInfo(Camera.this, 7);
                                ++i;
                            }
                        }

                        i = 0;

                        while(true) {
                            if (i >= Camera.this.iRegisterIOTCListenerList.size()) {
                                break label136;
                            }

                            listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                            listener.receiveSessionInfo(Camera.this, 8);
                            ++i;
                        }
                    }

                    if (Camera.this.nIOTCSessionID != -13) {
                        for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                            listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                            listener.receiveSessionInfo(Camera.this, 4);
                        }
                    }

                    ++nRetryForIOTC_Conn;

                    try {
                        long sleepTime = (long)(nRetryForIOTC_Conn > 3 ? 300000 : nRetryForIOTC_Conn * 5000);
                        Object var4 = this.d;
                        synchronized(this.d) {
                            this.d.wait(sleepTime);
                        }
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                }
            }

            this.c = false;
            MLog.i("IOTCamera", "===ThreadConnectDev exit===");
        }
    }

    private class d extends Thread {
        private boolean b;
        private Camera.ChannelInfo c;

        public void a() {
            this.b = false;
        }

        public void run() {
            byte[] mp3OutBuf = new byte['\uffff'];
            short[] speexOutBuf = new short[160];
            byte[] adpcmOutBuf = new byte[640];
            byte[] G726OutBuf = new byte[2048];
            long[] G726OutBufLen = new long[1];
            boolean bFirst = true;
            boolean bInitAudio = false;
            int nCodecId = -1;
            int nSamplerate = -1;
            int nDatabits = -1;
            int nChannel = -1;
            int nFPS = 0;
            long firstTimeStampFromDevice = 0L;
            long firstTimeStampFromLocal = 0L;
            long sleepTime = 0L;
            this.b = true;

            while(this.b) {
                if (this.c.audioAVFrameContainer.getAVFrameNums() <= 0) {
                    try {
                        Thread.sleep(4L);
                    } catch (InterruptedException var21) {
                        var21.printStackTrace();
                    }
                } else {
                    AVFrame frame = this.c.audioAVFrameContainer.getFirstAVFrame();
                    nCodecId = frame.getCodecId();
                    if (bFirst && !this.a.k && (nCodecId == 142 || nCodecId == 141 || nCodecId == 139 || nCodecId == 140 || nCodecId == 143)) {
                        bFirst = false;
                        nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                        nDatabits = frame.getFlags() & 2;
                        nDatabits = nDatabits == 2 ? 1 : 0;
                        nChannel = frame.getFlags() & 1;
                        bInitAudio = this.a.a(nSamplerate, nChannel, nDatabits, nCodecId);
                        if (!bInitAudio) {
                            break;
                        }
                    }

                    if (nCodecId == 141) {
                        DecSpeex.Decode(frame.frmData, frame.getFrmSize(), speexOutBuf);
                        this.a.l.write(speexOutBuf, 0, 160);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 160;
                    } else if (nCodecId == 142) {
                        int len = DecMp3.Decode(frame.frmData, frame.getFrmSize(), mp3OutBuf);
                        this.a.l.write(mp3OutBuf, 0, len);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / len;
                    } else if (nCodecId == 139) {
                        DecADPCM.Decode(frame.frmData, frame.getFrmSize(), adpcmOutBuf);
                        this.a.l.write(adpcmOutBuf, 0, 640);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 640;
                    } else if (nCodecId == 140) {
                        this.a.l.write(frame.frmData, 0, frame.getFrmSize());
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / frame.getFrmSize();
                    } else if (nCodecId == 143) {
                        DecG726.g726_decode(frame.frmData, (long)frame.getFrmSize(), G726OutBuf, G726OutBufLen);
                        MLog.i("IOTCamera", "G726 decode size:" + G726OutBufLen[0]);
                        this.a.l.write(G726OutBuf, 0, (int)G726OutBufLen[0]);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / (int)G726OutBufLen[0];
                    }

                    try {
                        Thread.sleep((long)(1000 / nFPS));
                    } catch (InterruptedException var22) {
                        var22.printStackTrace();
                    }
                }
            }

            if (bInitAudio) {
                this.a.a(nCodecId);
            }

            MLog.i("IOTCamera", "===ThreadDecodeAudio exit===");
        }
    }

    private class e extends Thread {
        private boolean b = false;
        private Camera.ChannelInfo channelInfo;

        public e(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.b = false;
        }

        public void run() {
            System.gc();
            int avFrameSize = -1;
            AVFrame avFrame = null;
            int videoWidth = 0;
            int videoHeight = 0;
            long firstTimeStampFromDevice = 0L;
            long firstTimeStampFromLocal = 0L;
            long sleepTime = 0L;
            long t1 = 0L;
            long t2 = 0L;
            long lastFrameTimeStamp = 0L;
            long delayTime = 0L;
            int[] framePara = new int[4];
            byte[] bufOut = new byte[3686400];
            byte[] bmpBuff = null;
            ByteBuffer bytBuffer = null;
            Bitmap bmp = null;
            int[] out_width = new int[1];
            int[] out_height = new int[1];
            int[] out_size = new int[1];
            boolean bInitH264 = false;
            boolean bInitMpeg4 = false;
            boolean bInitMp4Record = false;
            int isKeyframex = -1;
            boolean mp4StartFlag = false;
            this.channelInfo.frameRate = 0;
            this.b = true;
            int[] decoFd = new int[1];
            boolean isWaitIframe = false;
            System.gc();
            boolean bSkipThisRound = false;
            boolean mEnableDither = true;
            long lastUpdateDispFrmPreSec = 0L;

            while(true) {
                label218:
                while(true) {
                    int w;
                    byte skipTime;
                    AVFrame tmp;
                    do {
                        while(true) {
                            do {
                                do {
                                    label152:
                                    do {
                                        while(this.b) {
                                            avFrame = null;
                                            if (this.channelInfo.videoAVFrameContainer.getAVFrameNums() > 0) {
                                                avFrame = this.channelInfo.videoAVFrameContainer.getFirstAVFrame();
                                                continue label152;
                                            }

                                            try {
                                                Thread.sleep(4L);
                                            } catch (InterruptedException var42) {
                                                var42.printStackTrace();
                                            }
                                        }

                                        return;
                                    } while(avFrame == null);
                                } while(avFrame == null);
                            } while(isWaitIframe && !avFrame.isIFrame());

                            isWaitIframe = false;
                            int avFrameSizex = avFrame.getFrmSize();
                            if (this.channelInfo.videoAVFrameContainer.getAVFrameNums() > 0 && delayTime > 2000L) {
                                bSkipThisRound = true;
                                skipTime = 0;
                                this.channelInfo.videoAVFrameContainer.setIsDrop(true);
                                tmp = this.channelInfo.videoAVFrameContainer.getFirstAVFrame();
                                break;
                            }

                            if (avFrameSizex > 0) {
                                out_size[0] = 0;
                                out_width[0] = 0;
                                out_height[0] = 0;
                                bSkipThisRound = false;
                                MLog.i("IOTCamera", "decode frame: " + (avFrame.isIFrame() ? "I" : "P") + "delay[" + delayTime + "]");
                                int i;
                                if (avFrame.getCodecId() == 78) {
                                    if (!bInitH264) {
                                        DecH264.InitDecoder();
                                        bInitH264 = true;
                                    }

                                    DecH264.DecoderNal(avFrame.frmData, avFrameSizex, framePara, bufOut, mEnableDither);
                                    if (Camera.this.n) {
                                        if (mp4StartFlag && bInitMp4Record) {
                                            int isKeyframe = avFrame.isIFrame() ? 1 : 0;
                                            H264toMP4.mp4packvideo(avFrame.frmData, avFrameSizex, isKeyframe, Camera.this.frameRate);
                                        } else if (avFrame.isIFrame() && !bInitMp4Record && Camera.this.frameRate > 0 && Camera.this.o.length() > 0) {
                                            if (H264toMP4.mp4init(Camera.this.o, videoWidth, videoHeight) == 1) {
                                                MLog.i(" mp4 video", "mp4 filename[" + Camera.this.o + "]" + "width[" + videoWidth + "]height" + videoHeight);
                                                bInitMp4Record = true;
                                                H264toMP4.mp4packvideo(avFrame.frmData, avFrameSizex, 1, Camera.this.frameRate);
                                                mp4StartFlag = true;
                                            } else {
                                                bInitMp4Record = false;
                                                MLog.i("Tutk mp4 video", "mp4 error filename[" + Camera.this.o + "]" + "width[" + videoWidth + "]height" + videoHeight);
                                            }
                                        }
                                    }

                                    if (bInitMp4Record && !Camera.this.n) {
                                        H264toMP4.mp4close();
                                        bInitMp4Record = false;
                                        mp4StartFlag = false;
                                    }
                                } else if (avFrame.getCodecId() == 76) {
                                    if (!bInitMpeg4) {
                                        w = (avFrame.frmData[23] & 15) << 9 | (avFrame.frmData[24] & 255) << 1 | (avFrame.frmData[25] & 128) >> 7;
                                        i = (avFrame.frmData[25] & 63) << 7 | (avFrame.frmData[26] & 254) >> 1;
                                        DecMpeg4.InitDecoder(w, i);
                                        bInitMpeg4 = true;
                                    }

                                    DecMpeg4.Decode(avFrame.frmData, avFrameSizex, bufOut, out_size, out_width, out_height);
                                }

                                if (avFrame.getCodecId() == 78) {
                                    out_width[0] = framePara[2];
                                    out_height[0] = framePara[3];
                                    out_size[0] = out_width[0] * out_height[0] * 2;
                                }

                                if (out_size[0] > 0 && out_width[0] > 0 && out_height[0] > 0) {
                                    videoWidth = out_width[0];
                                    videoHeight = out_height[0];
                                    if (mEnableDither && avFrame.getCodecId() != 76) {
                                        bmp = Bitmap.createBitmap(videoWidth, videoHeight, Config.ARGB_8888);
                                    } else {
                                        bmp = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565);
                                    }

                                    bytBuffer = ByteBuffer.wrap(bufOut);
                                    bmp.copyPixelsFromBuffer(bytBuffer);
                                    long now;
                                    if (avFrame != null && firstTimeStampFromDevice != 0L && firstTimeStampFromLocal != 0L) {
                                        now = System.currentTimeMillis();
                                        long var10000 = now - t1;
                                        sleepTime = firstTimeStampFromLocal + ((long)avFrame.getTimeStamp() - firstTimeStampFromDevice) - now;
                                        delayTime = sleepTime * -1L;
                                        if (sleepTime >= 0L) {
                                            if ((long)avFrame.getTimeStamp() - lastFrameTimeStamp > 500L) {
                                                firstTimeStampFromDevice = (long)avFrame.getTimeStamp();
                                                firstTimeStampFromLocal = now;
                                                MLog.i("IOTCamera", "RESET base timestamp");
                                                if (sleepTime > 1000L) {
                                                    sleepTime = 33L;
                                                }
                                            }

                                            if (sleepTime > 1000L) {
                                                sleepTime = 1000L;
                                            }

                                            try {
                                                Thread.sleep(sleepTime);
                                            } catch (Exception var41) {
                                                ;
                                            }
                                        }

                                        lastFrameTimeStamp = (long)avFrame.getTimeStamp();
                                    }

                                    if (firstTimeStampFromDevice == 0L || firstTimeStampFromLocal == 0L) {
                                        firstTimeStampFromDevice = lastFrameTimeStamp = (long)avFrame.getTimeStamp();
                                        firstTimeStampFromLocal = System.currentTimeMillis();
                                    }

                                    ++this.channelInfo.frameRate;
                                    ++Camera.nFlow_total_FPS_count;
                                    ++Camera.nFlow_total_FPS_count_noClear;
                                    Camera.this.q = Camera.this.q + 1;
                                    synchronized(Camera.this.iRegisterIOTCListenerList) {
                                        i = 0;

                                        while(true) {
                                            if (i >= Camera.this.iRegisterIOTCListenerList.size()) {
                                                break;
                                            }

                                            IRegisterIOTCListener listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                                            listener.receiveFrameData(Camera.this, this.channelInfo.getAvIndex(), bmp);
                                            ++i;
                                        }
                                    }

                                    this.channelInfo.shotBmp = bmp;
                                    now = System.currentTimeMillis();
                                    if (now - lastUpdateDispFrmPreSec > 60000L) {
                                        Camera.this.q = 0;
                                        Camera.this.r = 0;
                                        lastUpdateDispFrmPreSec = now;
                                    }
                                }
                            }

                            if (avFrame != null) {
                                avFrame.frmData = null;
                                avFrame = null;
                            }
                        }
                    } while(tmp == null);

                    w = (int)((long)skipTime + ((long)tmp.getTimeStamp() - lastFrameTimeStamp));
                    MLog.i("IOTCamera", "low decode performance, drop " + (tmp.isIFrame() ? "I" : "P") + " frame, skip time: " + ((long)tmp.getTimeStamp() - lastFrameTimeStamp) + ", total skip: " + w);
                    lastFrameTimeStamp = (long)tmp.getTimeStamp();

                    while(!this.channelInfo.videoAVFrameContainer.isEmpty()) {
                        tmp = this.channelInfo.videoAVFrameContainer.getFirstAVFrame();
                        if (tmp == null) {
                            this.channelInfo.videoAVFrameContainer.setIsDrop(false);
                            continue label218;
                        }

                        w = (int)((long)w + ((long)tmp.getTimeStamp() - lastFrameTimeStamp));
                        MLog.i("IOTCamera_de", "low decode performance, drop " + (tmp.isIFrame() ? "I" : "P") + " frame, skip time: " + ((long)tmp.getTimeStamp() - lastFrameTimeStamp) + ", total skip: " + w + "index[" + tmp.getFrmNo() + "]");
                        lastFrameTimeStamp = (long)tmp.getTimeStamp();
                        delayTime -= (long)w;
                        avFrameSize = -1;
                        isWaitIframe = true;
                        MLog.i("IOTCamera", "delayTime: " + delayTime);
                    }

                    MLog.i("IOTCamera_de", "low decode performance, drop " + (tmp.isIFrame() ? "I" : "P") + " frame, skip time: " + ((long)tmp.getTimeStamp() - lastFrameTimeStamp) + ", total skip: " + w + "index[" + tmp.getFrmNo() + "]");
                    this.channelInfo.videoAVFrameContainer.setIsDrop(false);
                }
            }
        }
    }

    private class f extends Thread {
        private boolean e = false;
        private boolean f = false;
        private Camera.ChannelInfo channelInfo;
        private String h;
        private String i;
        byte[] a = new byte[1024];
        byte[] b = new byte[2048];
        int c = 0;
        private int j = 0;

        public f(Camera.ChannelInfo channel, String LocalFile, String RemoteFile) {
            this.channelInfo = channel;
            this.h = LocalFile;
            this.i = RemoteFile;
        }

        public void a() {
            this.e = false;
            this.f = true;
        }

        public void run() {
            System.gc();
            this.e = true;
            int STRUCTSIZE = 0;
            int RECVBUFSIZE = 0;
            byte[] bRecvBuf = new byte[1024];
            byte[] bRecvRDTCommand = new byte[128];
            byte[] bSendRDTCommand = new byte[128];

            while(this.e && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(100L);
                    }
                } catch (Exception var22) {
                    var22.printStackTrace();
                }
            }

            St_RDT_Status status = new St_RDT_Status();
            MLog.i("=ThreadSendRdFile=", "mSid [" + Camera.this.nIOTCSessionID + "]");
            int RDT_ID = RDTAPIs.RDT_Create(Camera.this.nIOTCSessionID, 30000, 3);
            if (RDT_ID < 0) {
                MLog.i("=ThreadSendRdFile=", "RDT_ID < 0[" + RDT_ID + "]");
            }

            if (RDT_ID >= 0) {
                MLog.i("=ThreadSendRdFile=", "RDT_start[" + this.i + "]");

                for(; this.e; this.e = false) {
                    if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                        byte[] bFileName = this.i.getBytes();
                        byte bFileCmdx = 1;
                        bSendRDTCommand[0] = bFileCmdx;
                        System.arraycopy(bFileName, 0, bSendRDTCommand, 1, bFileName.length < 128 ? bFileName.length : 128);
                        int nRead = RDTAPIs.RDT_Write(RDT_ID, bSendRDTCommand, 128);
                        if (nRead < 0) {
                            break;
                        }

                        nRead = RDTAPIs.RDT_Read(RDT_ID, bRecvBuf, 1024, 30000);
                        System.arraycopy(bRecvBuf, 0, bRecvRDTCommand, 0, 128);
                        if (nRead < 0) {
                            break;
                        }

                        byte bFileCmd = bRecvRDTCommand[0];
                        if (bFileCmd != 2) {
                            break;
                        }

                        byte[] bsize = new byte[8];
                        System.arraycopy(bRecvRDTCommand, 1, bsize, 0, 8);
                        String strFileSize = Camera.getString(bsize);
                        int nRemainFileSize = Integer.parseInt(strFileSize);
                        MLog.i("=ThreadDownloadRdFile=", "total read[" + nRemainFileSize + "]");
                        byte[] sendStart = RDTCommand.parseContent((byte)4, (new String("Start")).getBytes());
                        nRead = RDTAPIs.RDT_Write(RDT_ID, sendStart, 128);
                        if (nRead < 0) {
                            break;
                        }

                        int nFileNum = 0;

                        for(int i = 0; i < 1 && RDTAPIs.RDT_Status_Check(RDT_ID, status) >= 0; ++i) {
                            for(int k = 0; k < Camera.this.iRegisterIOTCListenerList.size(); ++k) {
                                IRegisterIOTCListener listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(k);
                                listener.receiveExtraInfo(Camera.this, this.channelInfo.getChannel(), 14, 4113, nRemainFileSize);
                            }

                            FileOutputStream fosLocalFile;
                            try {
                                fosLocalFile = new FileOutputStream(this.h);
                            } catch (FileNotFoundException var23) {
                                var23.printStackTrace();
                                break;
                            }

                            IRegisterIOTCListener listenerx;
                            int j;
                            do {
                                if (nRemainFileSize < 1024) {
                                    nRead = RDTAPIs.RDT_Read(RDT_ID, bRecvBuf, nRemainFileSize, 30000);
                                } else {
                                    nRead = RDTAPIs.RDT_Read(RDT_ID, bRecvBuf, 1024, 30000);
                                }

                                if (nRead < 0 || nRead < 0 && nRead != -10007) {
                                    break;
                                }

                                nRemainFileSize -= nRead;

                                try {
                                    if (nRead > 0) {
                                        fosLocalFile.write(bRecvBuf, 0, nRead);
                                    }
                                } catch (IOException var25) {
                                    var25.printStackTrace();
                                    break;
                                }

                                if (nRemainFileSize <= 0) {
                                    break;
                                }

                                for(j = 0; j < Camera.this.iRegisterIOTCListenerList.size(); ++j) {
                                    listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(j);
                                    listenerx.receiveExtraInfo(Camera.this, this.channelInfo.getChannel(), 14, 4115, nRemainFileSize);
                                }
                            } while(!this.f);

                            try {
                                fosLocalFile.close();
                            } catch (IOException var24) {
                                var24.printStackTrace();
                                break;
                            }

                            for(j = 0; j < Camera.this.iRegisterIOTCListenerList.size(); ++j) {
                                listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(j);
                                listenerx.receiveExtraInfo(Camera.this, this.channelInfo.getChannel(), 14, 4114, nRemainFileSize);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(32L);
                        } catch (InterruptedException var20) {
                            var20.printStackTrace();
                        }
                    }
                }
            }

            Camera.this.g = null;
            RDTAPIs.RDT_Destroy(RDT_ID);
        }
    }

    private class g extends Thread {
        private final int b = 1280;
        private int ret = 0;
        private boolean d = false;
        private Camera.ChannelInfo channelInfo;

        public g(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.d = false;
        }

        public void run() {
            this.d = true;

            while(this.d && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(100L);
                    }
                } catch (Exception var23) {
                    var23.printStackTrace();
                }
            }

            this.channelInfo.h = 0;
            byte[] recvBuf = new byte[1280];
            byte[] bytAVFrame = new byte[24];
            int[] pFrmNo = new int[1];
            byte[] mp3OutBuf = new byte['\uffff'];
            short[] speexOutBuf = new short[160];
            byte[] adpcmOutBuf = new byte[640];
            byte[] G726OutBuf = new byte[2048];
            long[] G726OutBufLen = new long[1];
            short[] G711OutBuf = new short[320];
            boolean bFirst = true;
            boolean bInitAudio = false;
            int nSamplerate = 44100;
            int nDatabits = 1;
            int nChannel = 1;
            int nCodecId = 0;
            int nFPS = 0;
            if (this.d && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 768, Packet.intToByteArray_Little(Camera.this.m));
            }

            while(this.d) {
                if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                    this.ret = AVAPIs.avRecvAudioData(this.channelInfo.getAvIndex(), recvBuf, recvBuf.length, bytAVFrame, 24, pFrmNo);
                    if (this.ret < 0 && this.ret != -20012) {
                        MLog.i("IOTCamera", "avRecvAudioData < 0");
                    }

                    if (this.ret <= 0) {
                        if (this.ret == -20012) {
                            try {
                                Thread.sleep((long)(nFPS == 0 ? 33 : 1000 / nFPS));
                            } catch (InterruptedException var21) {
                                var21.printStackTrace();
                            }
                        } else if (this.ret == -20014) {
                            MLog.i("IOTCamera", "avRecvAudioData returns AV_ER_LOSED_THIS_FRAME");
                        } else {
                            try {
                                Thread.sleep((long)(nFPS == 0 ? 33 : 1000 / nFPS));
                            } catch (InterruptedException var20) {
                                var20.printStackTrace();
                            }

                            MLog.i("IOTCamera", "avRecvAudioData returns " + this.ret);
                        }
                    } else {
                        this.channelInfo.h += this.ret;
                        byte[] frameData = new byte[this.ret];
                        System.arraycopy(recvBuf, 0, frameData, 0, this.ret);
                        AVFrame frame = new AVFrame((long)pFrmNo[0], (byte)0, bytAVFrame, frameData, this.ret);
                        nCodecId = frame.getCodecId();
                        if (bFirst && (!Camera.this.k && (nCodecId == 142 || nCodecId == 141 || nCodecId == 139 || nCodecId == 140 || nCodecId == 143) || nCodecId == 138)) {
                            bFirst = false;
                            nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                            nDatabits = frame.getFlags() & 2;
                            nDatabits = nDatabits == 2 ? 1 : 0;
                            nChannel = frame.getFlags() & 1;
                            if (nCodecId == 141) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 160;
                            } else if (nCodecId == 139) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 640;
                            } else if (nCodecId == 140) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / frame.getFrmSize();
                            } else if (nCodecId == 138) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 320;
                            }

                            bInitAudio = Camera.this.a(nSamplerate, nChannel, nDatabits, nCodecId);
                            if (!bInitAudio) {
                                break;
                            }
                        }

                        if (nCodecId == 141) {
                            DecSpeex.Decode(recvBuf, this.ret, speexOutBuf);
                            Camera.this.audioTrack.write(speexOutBuf, 0, 160);
                        } else if (nCodecId == 142) {
                            int len = DecMp3.Decode(recvBuf, this.ret, mp3OutBuf);
                            Camera.this.audioTrack.write(mp3OutBuf, 0, len);
                            nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / len;
                        } else if (nCodecId == 139) {
                            DecADPCM.Decode(recvBuf, this.ret, adpcmOutBuf);
                            Camera.this.audioTrack.write(adpcmOutBuf, 0, 640);
                        } else if (nCodecId == 140) {
                            Camera.this.audioTrack.write(recvBuf, 0, this.ret);
                        } else if (nCodecId == 143) {
                            DecG726.g726_decode(recvBuf, (long)this.ret, G726OutBuf, G726OutBufLen);
                            MLog.i("IOTCamera", "G726 decode size:" + G726OutBufLen[0]);
                            Camera.this.audioTrack.write(G726OutBuf, 0, (int)G726OutBufLen[0]);
                            nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / (int)G726OutBufLen[0];
                        } else if (nCodecId == 138) {
                            PCMA.alaw2linear(recvBuf, G711OutBuf, this.ret);
                            Camera.this.audioTrack.write(G711OutBuf, 0, this.ret);
                        }
                    }
                }
            }

            if (bInitAudio) {
                Camera.this.a(nCodecId);
            }

            this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 769, Packet.intToByteArray_Little(Camera.this.m));
            MLog.i("IOTCamera", "===ThreadRecvAudio exit===");
        }
    }

    private class h extends Thread {
        private final int b = 0;
        private boolean c = false;
        private Camera.ChannelInfo channelInfo;
        private int e = 100;

        public h(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.c = false;
        }

        public void a(int times) {
            if (times > 5) {
                this.e = times;
            }

        }

        public void run() {
            this.c = true;

            while(this.c && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(1000L);
                    }
                } catch (Exception var12) {
                    var12.printStackTrace();
                }
            }

            boolean var1 = false;

            while(true) {
                while(true) {
                    do {
                        do {
                            if (!this.c) {
                                MLog.i("IOTCamera", "===ThreadRecvIOCtrl exit===");
                                return;
                            }
                        } while(Camera.this.nIOTCSessionID < 0);
                    } while(this.channelInfo.getAvIndex() < 0);

                    int[] ioCtrlType = new int[1];
                    byte[] ioCtrlBuf = new byte[1024];
                    int nRet = AVAPIs.avRecvIOCtrl(this.channelInfo.getAvIndex(), ioCtrlType, ioCtrlBuf, ioCtrlBuf.length, 0);
                    if (nRet >= 0) {
                        MLog.i("IOTCamera", "avRecvIOCtrl(" + this.channelInfo.getAvIndex() + ", 0x" + Integer.toHexString(ioCtrlType[0]) + ", " + Camera.a(ioCtrlBuf, nRet) + ")");
                        byte[] data = new byte[nRet];
                        System.arraycopy(ioCtrlBuf, 0, data, 0, nRet);
                        int channel;
                        if (ioCtrlType[0] == 811) {
                            channel = Packet.byteArrayToInt_Little(data, 0);
                            int format = Packet.byteArrayToInt_Little(data, 4);
                            Iterator var9 = Camera.this.channelInfoList.iterator();

                            while(var9.hasNext()) {
                                Camera.ChannelInfo ch = (Camera.ChannelInfo)var9.next();
                                if (ch.getChannel() == channel) {
                                    ch.b(format);
                                    break;
                                }
                            }
                        }

                        for(channel = 0; channel < Camera.this.iRegisterIOTCListenerList.size(); ++channel) {
                            IRegisterIOTCListener listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(channel);
                            listener.receiveIOCtrlData(Camera.this, this.channelInfo.getChannel(), ioCtrlType[0], data);
                        }
                    } else {
                        try {
                            Thread.sleep((long)this.e);
                        } catch (InterruptedException var10) {
                            var10.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class i extends Thread {
        private boolean b = false;
        private Camera.ChannelInfo channelInfo;

        public i(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.b = false;
        }

        public void run() {
            System.gc();
            this.b = true;

            while(this.b && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(100L);
                    }
                } catch (Exception var25) {
                    var25.printStackTrace();
                }
            }

            this.channelInfo.outBufSize = 0;
            byte[] buf = new byte[2764800];
            byte[] pFrmInfoBuf = new byte[24];
            int[] pFrmNo = new int[1];
            int nCodecId = -1;
            int nReadSize = -1;
            int nFrmCount = 0;
            int nIncompleteFrmCount = 0;
            int nOnlineNumber = 0;
            long nPrevFrmNo = 268435455L;
            long lastTimeStamp = System.currentTimeMillis();
            int[] outBufSize = new int[1];
            int[] outFrmSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            int nemptyCount = 0;
            if (this.b && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                int nDelayTime_ms = 0;
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 255, Packet.intToByteArray_Little(nDelayTime_ms));
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 511, Packet.intToByteArray_Little(Camera.this.m));
            }

            this.channelInfo.audioAVFrameContainer.clear();
            if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                AVAPIs.avClientCleanBuf(this.channelInfo.getAvIndex());
            }

            while(true) {
                while(true) {
                    while(true) {
                        while(true) {
                            do {
                                do {
                                    if (!this.b) {
                                        this.channelInfo.videoAVFrameContainer.clear();
                                        if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                                            this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 767, Packet.intToByteArray_Little(Camera.this.m));
                                            AVAPIs.avClientCleanBuf(this.channelInfo.getAvIndex());
                                        }

                                        byte[] bufx = null;
                                        return;
                                    }
                                } while(Camera.this.nIOTCSessionID < 0);
                            } while(this.channelInfo.getAvIndex() < 0);

                            if (System.currentTimeMillis() - lastTimeStamp > 1000L) {
                                lastTimeStamp = System.currentTimeMillis();

                                for(int i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                                    IRegisterIOTCListener listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                                    listener.receiveFrameInfo(Camera.this, this.channelInfo.getChannel(), (long)((this.channelInfo.h + this.channelInfo.outBufSize) * 8 / 1024), this.channelInfo.frameRate, nOnlineNumber, nFrmCount, nIncompleteFrmCount);
                                    listener.receiveExtraInfo(Camera.this, this.channelInfo.getChannel(), 3, 0, 1);
                                }

                                Camera.this.frameRate = this.channelInfo.frameRate;
                                this.channelInfo.frameRate = this.channelInfo.outBufSize = this.channelInfo.h = 0;
                                Camera var10000 = Camera.this;
                                Camera.this.q = 0;
                                var10000.r = 0;
                            }

                            int nReadSizex = AVAPIs.avRecvFrameData2(this.channelInfo.getAvIndex(), buf, buf.length, outBufSize, outFrmSize, pFrmInfoBuf, pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);
                            Camera.this.r = Camera.this.r + 1;
                            short nCodecIdx;
                            byte[] frameData;
                            AVFrame frame;
                            if (nReadSizex >= 0) {
                                this.channelInfo.outBufSize += outBufSize[0];
                                ++nFrmCount;
                                Camera.this.q = Camera.this.q + 1;
                                frameData = new byte[nReadSizex];
                                System.arraycopy(buf, 0, frameData, 0, nReadSizex);
                                frame = new AVFrame((long)pFrmNo[0], (byte)0, pFrmInfoBuf, frameData, nReadSizex);
                                nCodecIdx = frame.getCodecId();
                                nOnlineNumber = frame.getOnlineNum();
                                if (nCodecIdx == 78) {
                                    if (!frame.isIFrame() && (long)pFrmNo[0] != nPrevFrmNo + 1L) {
                                        if (nemptyCount > 30) {
                                            nemptyCount = 0;
                                            Camera.this.sendIOCtrl(this.channelInfo.getAvIndex(), 511, Packet.intToByteArray_Little(Camera.this.m));
                                        } else {
                                            ++nemptyCount;
                                        }

                                        MLog.i("IOTCamera", "Incorrect frame no(" + pFrmNo[0] + "), prev:" + nPrevFrmNo + " -> drop frame");
                                    } else if (!this.channelInfo.videoAVFrameContainer.isDrop()) {
                                        nPrevFrmNo = (long)pFrmNo[0];
                                        this.channelInfo.videoAVFrameContainer.addAVFrame(frame);
                                    }
                                } else if (nCodecIdx == 76) {
                                    if (frame.isIFrame() || (long)pFrmNo[0] == nPrevFrmNo + 1L) {
                                        nPrevFrmNo = (long)pFrmNo[0];
                                        this.channelInfo.videoAVFrameContainer.addAVFrame(frame);
                                    }
                                } else if (nCodecIdx == 79) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(frameData, 0, nReadSizex);
                                    if (bmp != null) {
                                        ++this.channelInfo.frameRate;

                                        for(int ix = 0; ix < Camera.this.iRegisterIOTCListenerList.size(); ++ix) {
                                            IRegisterIOTCListener listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(ix);
                                            listenerx.receiveFrameData(Camera.this, this.channelInfo.getChannel(), bmp);
                                        }

                                        this.channelInfo.shotBmp = bmp;
                                    }

                                    try {
                                        Thread.sleep(32L);
                                    } catch (InterruptedException var23) {
                                        var23.printStackTrace();
                                    }
                                }
                            } else if (nReadSizex == -20015) {
                                MLog.i("IOTCamera", "AV_ER_SESSION_CLOSE_BY_REMOTE");
                            } else if (nReadSizex == -20016) {
                                MLog.i("IOTCamera", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                            } else if (nReadSizex == -20012) {
                                try {
                                    Thread.sleep(32L);
                                } catch (InterruptedException var22) {
                                    var22.printStackTrace();
                                }
                            } else if (nReadSizex != -20001) {
                                if (nReadSizex == -20003) {
                                    ++nFrmCount;
                                    ++nIncompleteFrmCount;
                                    MLog.i("IOTCamera", "AV_ER_MEM_INSUFF");
                                } else if (nReadSizex == -20014) {
                                    MLog.i("IOTCamera", "AV_ER_LOSED_THIS_FRAME2");
                                    ++nFrmCount;
                                    ++nIncompleteFrmCount;
                                } else if (nReadSizex == -20013) {
                                    ++nFrmCount;
                                    this.channelInfo.outBufSize += outBufSize[0];
                                    if (outFrmInfoBufSize[0] != 0 && (double)outFrmSize[0] * 0.9D <= (double)outBufSize[0] && pFrmInfoBuf[2] != 0) {
                                        frameData = new byte[outFrmSize[0]];
                                        System.arraycopy(buf, 0, frameData, 0, outFrmSize[0]);
                                        nCodecIdx = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                                        if (nCodecIdx == 79) {
                                            ++nIncompleteFrmCount;
                                        } else if (nCodecIdx != 76 && nCodecIdx != 78) {
                                            ++nIncompleteFrmCount;
                                        } else {
                                            frame = new AVFrame((long)pFrmNo[0], (byte)0, pFrmInfoBuf, frameData, outFrmSize[0]);
                                            if (!frame.isIFrame() && (long)pFrmNo[0] != nPrevFrmNo + 1L) {
                                                ++nIncompleteFrmCount;
                                                MLog.i("IOTCamera", "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4 - LOST");
                                            } else {
                                                nPrevFrmNo = (long)pFrmNo[0];
                                                this.channelInfo.videoAVFrameContainer.addAVFrame(frame);
                                                MLog.i("IOTCamera", "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4");
                                            }
                                        }
                                    } else {
                                        ++nIncompleteFrmCount;
                                        MLog.i("IOTCamera", (pFrmInfoBuf[2] == 0 ? "P" : "I") + " frame, outFrmSize(" + outFrmSize[0] + ") * 0.9 = " + (double)outFrmSize[0] * 0.9D + " > outBufSize(" + outBufSize[0] + ")");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class j extends Thread {
        private boolean b = false;
        private int c = -1;
        private int d = -1;
        private Camera.ChannelInfo channelInfo = null;

        public j(Camera.ChannelInfo ch) {
            this.channelInfo = ch;
        }

        public void a() {
            if (Camera.this.nIOTCSessionID >= 0 && this.d >= 0) {
                AVAPIs.avServExit(Camera.this.nIOTCSessionID, this.d);
                Camera.this.sendIOCtrl(this.channelInfo.channel, 849, SMsgAVIoctrlAVStream.parseContent(this.d));
            }

            this.b = false;
        }

        public void run() {
            super.run();
            if (Camera.this.nIOTCSessionID < 0) {
                MLog.i("IOTCamera", "=== ThreadSendAudio exit because SID < 0 ===");
            } else {
                this.b = true;
                boolean bInitSpeexEnc = false;
                boolean bInitG726Enc = false;
                boolean bInitADPCM = false;
                boolean bInitPCM = false;
                boolean bInitG711 = false;
                int nMinBufSize = 0;
                int nReadBytesx = -1;
                this.d = IOTCAPIs.IOTC_Session_Get_Free_Channel(Camera.this.nIOTCSessionID);
                if (this.d < 0) {
                    MLog.i("IOTCamera", "=== ThreadSendAudio exit becuase no more channel for connection ===");
                } else {
                    Camera.this.sendIOCtrl(this.channelInfo.channel, 848, SMsgAVIoctrlAVStream.parseContent(this.d));
                    MLog.i("IOTCamera", "start avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.d + ")");

                    while(this.b && (this.c = AVAPIs.avServStart(Camera.this.nIOTCSessionID, (byte[])null, (byte[])null, 60L, 0L, this.d)) < 0) {
                        MLog.i("IOTCamera", "avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.d + ") : " + this.c);
                    }

                    MLog.i("IOTCamera", "avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.d + ") : " + this.c);
                    if (this.b && this.channelInfo.d() == 141) {
                        EncSpeex.InitEncoder(8);
                        bInitSpeexEnc = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "Speex encoder init");
                    }

                    if (this.b && this.channelInfo.d() == 139) {
                        EncADPCM.ResetEncoder();
                        bInitADPCM = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "ADPCM encoder init");
                    }

                    if (this.b && this.channelInfo.d() == 143) {
                        EncG726.g726_enc_state_create((byte)0, (byte)2);
                        bInitG726Enc = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "G726 encoder init");
                    }

                    if (this.b && this.channelInfo.d() == 138) {
                        bInitG711 = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "G711 encoder init");
                    }

                    if (this.b && this.channelInfo.d() == 140) {
                        bInitPCM = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                    }

                    AudioRecord recorder = null;
                    if (this.b && (bInitADPCM || bInitG726Enc || bInitSpeexEnc || bInitPCM || bInitG711)) {
                        recorder = new AudioRecord(1, 8000, 16, 2, nMinBufSize);
                        recorder.startRecording();
                    }

                    short[] inSpeexBuf = new short[160];
                    byte[] inADPCMBuf = new byte[640];
                    byte[] inG726Buf = new byte[320];
                    byte[] inPCMBuf = new byte[640];
                    short[] inG711Buf = new short[320];
                    byte[] outSpeexBuf = new byte[38];
                    byte[] outADPCMBuf = new byte[160];
                    byte[] outG726Buf = new byte[2048];
                    long[] outG726BufLen = new long[1];
                    byte[] outG711Buf = new byte[2048];

                    while(this.b) {
                        int nReadBytes;
                        if (this.channelInfo.d() == 141) {
                            nReadBytes = recorder.read(inSpeexBuf, 0, inSpeexBuf.length);
                            if (nReadBytes > 0) {
                                int len = EncSpeex.Encode(inSpeexBuf, nReadBytes, outSpeexBuf);
                                byte flagx = 2;
                                byte[] frameInfox = SFrameInfo.parseContent((short)141, flagx, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                AVAPIs.avSendAudioData(this.c, outSpeexBuf, len, frameInfox, 16);
                            }
                        } else {
                            byte flag;
                            byte[] frameInfo;
                            if (this.channelInfo.d() == 139) {
                                nReadBytes = recorder.read(inADPCMBuf, 0, inADPCMBuf.length);
                                if (nReadBytes > 0) {
                                    EncADPCM.Encode(inADPCMBuf, nReadBytes, outADPCMBuf);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)139, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.c, outADPCMBuf, nReadBytes / 4, frameInfo, 16);
                                }
                            } else if (this.channelInfo.d() == 143) {
                                nReadBytes = recorder.read(inG726Buf, 0, inG726Buf.length);
                                if (nReadBytes > 0) {
                                    EncG726.g726_encode(inG726Buf, (long)nReadBytes, outG726Buf, outG726BufLen);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)143, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.c, outG726Buf, (int)outG726BufLen[0], frameInfo, 16);
                                }
                            } else if (this.channelInfo.d() == 140) {
                                nReadBytes = recorder.read(inPCMBuf, 0, inPCMBuf.length);
                                if (nReadBytes > 0) {
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)140, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.c, inPCMBuf, nReadBytes, frameInfo, 16);
                                }
                            } else if (this.channelInfo.d() == 138) {
                                nReadBytes = recorder.read(inG711Buf, 0, inG711Buf.length);
                                if (nReadBytes > 0) {
                                    PCMA.linear2alaw(inG711Buf, 0, outG711Buf, nReadBytes);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)138, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.c, outG711Buf, nReadBytes, frameInfo, 16);
                                }
                            }
                        }
                    }

                    if (bInitSpeexEnc) {
                        EncSpeex.UninitEncoder();
                    }

                    if (bInitG726Enc) {
                        EncG726.g726_enc_state_destroy();
                    }

                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                    }

                    if (this.c >= 0) {
                        AVAPIs.avServStop(this.c);
                    }

                    if (this.d >= 0) {
                        IOTCAPIs.IOTC_Session_Channel_OFF(Camera.this.nIOTCSessionID, this.d);
                    }

                    this.c = -1;
                    this.d = -1;
                    MLog.i("IOTCamera", "===ThreadSendAudio exit===");
                }
            }
        }
    }

    private class k extends Thread {
        private boolean b = false;
        private Camera.ChannelInfo channelInfo;
        private int d = 50;

        public k(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.b = false;
            if (this.channelInfo.getAvIndex() >= 0) {
                MLog.i("IOTCamera", "avSendIOCtrlExit(" + this.channelInfo.getAvIndex() + ")");
                AVAPIs.avSendIOCtrlExit(this.channelInfo.getAvIndex());
            }

        }

        public void a(int times) {
            if (times > 5) {
                this.d = times;
            }

        }

        public void run() {
            this.b = true;

            while(this.b && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(1000L);
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

            if (this.b && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                int nDelayTime_ms = 0;
                AVAPIs.avSendIOCtrl(this.channelInfo.getAvIndex(), 255, Packet.intToByteArray_Little(nDelayTime_ms), 4);
            }

            while(true) {
                while(this.b) {
                    if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0 && !this.channelInfo.ioCtrlQueue.isEmpty()) {
                        Camera.IOCtrlQueue.IOCtrlSet data = this.channelInfo.ioCtrlQueue.getIOCtrlSet();
                        if (this.b && data != null) {
                            int ret = AVAPIs.avSendIOCtrl(this.channelInfo.getAvIndex(), data.IOCtrlType, data.IOCtrlBuf, data.IOCtrlBuf.length);
                            if (ret >= 0) {
                                MLog.i("IOTCamera", "avSendIOCtrl(" + this.channelInfo.getAvIndex() + ", 0x" + Integer.toHexString(data.IOCtrlType) + ", " + Camera.a(data.IOCtrlBuf, data.IOCtrlBuf.length) + ")");
                            } else {
                                MLog.i("IOTCamera", "avSendIOCtrl failed : " + ret);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep((long)this.d);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }
                }

                MLog.i("IOTCamera", "===ThreadSendIOCtrl exit===");
                return;
            }
        }
    }

    private class l extends Thread {
        private boolean b = false;
        private Camera.ChannelInfo channelInfo;
        private Object d = new Object();
        private int e = 0;

        public l(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void a() {
            this.b = false;
            if (Camera.this.nIOTCSessionID >= 0) {
                MLog.i("IOTCamera", "avClientExit(" + Camera.this.nIOTCSessionID + ", " + this.channelInfo.getChannel() + ")");
                AVAPIs.avClientExit(Camera.this.nIOTCSessionID, this.channelInfo.getChannel());
            }

            Object var1 = this.d;
            synchronized(this.d) {
                this.d.notify();
            }
        }

        public void run() {
            this.b = true;
            boolean var1 = true;

            label112:
            while(this.b) {
                if (Camera.this.nIOTCSessionID < 0) {
                    try {
                        synchronized(Camera.this.c) {
                            Camera.this.c.wait(100L);
                        }
                    } catch (Exception var11) {
                        var11.printStackTrace();
                    }
                } else {
                    for(int i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                        IRegisterIOTCListener listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                        listener.receiveChannelInfo(Camera.this, this.channelInfo.getChannel(), 1);
                    }

                    long[] nServType = new long[1];
                    int[] mResend = new int[1];
                    nServType[0] = -1L;
                    int avIndex;
                    if (Camera.this.getSessionMode() == 2) {
                        avIndex = AVAPIs.avClientStart(Camera.this.nIOTCSessionID, this.channelInfo.getView_acc(), this.channelInfo.getView_pwd(), 30L, nServType, this.channelInfo.getChannel());
                    } else {
                        avIndex = AVAPIs.avClientStart2(Camera.this.nIOTCSessionID, this.channelInfo.getView_acc(), this.channelInfo.getView_pwd(), 30L, nServType, this.channelInfo.getChannel(), mResend);
                    }

                    MLog.i("IOTCamera", "avClientStart(" + this.channelInfo.getChannel() + ", " + this.channelInfo.getView_acc() + ", " + this.channelInfo.getView_pwd() + ") in Session(" + Camera.this.nIOTCSessionID + ") returns " + avIndex);
                    long servType = nServType[0];
                    int ix;
                    IRegisterIOTCListener listenerx;
                    if (Camera.w && ((Camera.this.getChannelServiceType(this.channelInfo.getChannel()) & 262144L) != 262144L || (Camera.this.getChannelServiceType(this.channelInfo.getChannel()) & 1048576L) != 1048576L || (Camera.this.getChannelServiceType(this.channelInfo.getChannel()) & 2097152L) != 2097152L || (Camera.this.getChannelServiceType(this.channelInfo.getChannel()) & 4194304L) != 4194304L)) {
                        for(ix = 0; ix < Camera.this.iRegisterIOTCListenerList.size(); ++ix) {
                            listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(ix);
                            listenerx.receiveChannelInfo(Camera.this, this.channelInfo.getChannel(), 9);
                        }

                        Camera.this.disconnect1();
                        break;
                    }

                    if (avIndex >= 0) {
                        this.channelInfo.setAvIndex(avIndex);
                        this.channelInfo.setServiceType(servType);
                        ix = 0;

                        while(true) {
                            if (ix >= Camera.this.iRegisterIOTCListenerList.size()) {
                                break label112;
                            }

                            listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(ix);
                            listenerx.receiveChannelInfo(Camera.this, this.channelInfo.getChannel(), 2);
                            ++ix;
                        }
                    }

                    if (avIndex != -20016 && avIndex != -20011) {
                        if (avIndex == -20009) {
                            ix = 0;

                            while(true) {
                                if (ix >= Camera.this.iRegisterIOTCListenerList.size()) {
                                    break label112;
                                }

                                listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(ix);
                                listenerx.receiveChannelInfo(Camera.this, this.channelInfo.getChannel(), 5);
                                ++ix;
                            }
                        }

                        try {
                            Object var15 = this.d;
                            synchronized(this.d) {
                                this.d.wait(1000L);
                            }
                        } catch (Exception var9) {
                            var9.printStackTrace();
                        }
                    } else {
                        for(ix = 0; ix < Camera.this.iRegisterIOTCListenerList.size(); ++ix) {
                            listenerx = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(ix);
                            listenerx.receiveChannelInfo(Camera.this, this.channelInfo.getChannel(), 6);
                        }
                    }
                }
            }

            MLog.i("IOTCamera", "===ThreadStartDev exit===");
        }
    }
}
