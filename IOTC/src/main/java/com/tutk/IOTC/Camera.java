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

import static com.tutk.IOTC.AVAPIs.AV_ER_BUFPARA_MAXSIZE_INSUFF;
import static com.tutk.IOTC.AVAPIs.AV_ER_DATA_NOREADY;
import static com.tutk.IOTC.AVAPIs.AV_ER_INCOMPLETE_FRAME;
import static com.tutk.IOTC.AVAPIs.AV_ER_LOSED_THIS_FRAME;
import static com.tutk.IOTC.AVAPIs.AV_ER_MEM_INSUFF;
import static com.tutk.IOTC.AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT;
import static com.tutk.IOTC.AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_ADPCM;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_G711A;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_G726;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_MP3;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_PCM;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_AUDIO_SPEEX;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_VIDEO_H264;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_VIDEO_MJPEG;
import static com.tutk.IOTC.AVFrame.MEDIA_CODEC_VIDEO_MPEG4;
import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTART;
import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTOP;
import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTOP;
import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START;
import static com.tutk.IOTC.AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STOP;

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
    private Camera.ThreadConnectDev threadConnectDev = null;
    private Camera.ThreadCheckDevStatus threadCheckDevStatus = null;
    private Camera.ThreadSendAudio threadSendAudio = null;
    private Camera.ThreadSendRdFile threadSendRdFile = null;
    private volatile int nIOTCSessionID = -1;
    private volatile int sessionMode = -1;
    private volatile int sid;
    private boolean k = false;
    private AudioTrack audioTrack = null;//音频播放器
    private int m = 0;
    private boolean isStartRecordVideo;
    private String mp4Name;
    private int frameRate;
    private int q = 0;
    private int r = 0;
    private String uid = "";
    private String pwd = "";
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
        if (this.threadConnectDev == null) {
            MLog.i("==checklive==", "enterthis create connect");
            this.threadConnectDev = new Camera.ThreadConnectDev(0);
            this.threadConnectDev.start();
        } else if (!this.threadConnectDev.isThreadStart) {
            this.threadConnectDev.run();
        }

        if (this.threadCheckDevStatus == null) {
            MLog.i("==checklive==", "enterthis create connect");
            this.threadCheckDevStatus = new Camera.ThreadCheckDevStatus();
            this.threadCheckDevStatus.start();
        }

        MLog.i("==checklive==", "exit this create connect");
    }

    public void connect(String uid, String pwd) {
        this.uid = uid;
        this.pwd = pwd;
        if (this.threadConnectDev == null) {
            this.threadConnectDev = new Camera.ThreadConnectDev(1);
            this.threadConnectDev.start();
        }

        if (this.threadCheckDevStatus == null) {
            this.threadCheckDevStatus = new Camera.ThreadCheckDevStatus();
            this.threadCheckDevStatus.start();
        }

    }

    public void disconnect() {
        List var1 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            Iterator var3 = this.channelInfoList.iterator();

            while(var3.hasNext()) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)var3.next();
                this.stopSpeaking(ch.getChannel());
                if (ch.threadStartDev != null) {
                    ch.threadStartDev.stopAvClient();
                }

                if (ch.threadDecodeAudio != null) {
                    ch.threadDecodeAudio.stopAudioDecode();
                }

                if (ch.threadDecodeVideo != null) {
                    ch.threadDecodeVideo.stopVideoDecode();
                }

                if (ch.threadRecvAudio != null) {
                    ch.threadRecvAudio.stopAudioRecv();
                }

                if (ch.threadRecvVideo != null) {
                    ch.threadRecvVideo.stopVideoRecv();
                }

                if (ch.threadRecvIOCtrl != null) {
                    ch.threadRecvIOCtrl.stopIOCtrlRecv();
                }

                if (ch.threadSendIOCtrl != null) {
                    ch.threadSendIOCtrl.stopIOCtrlSend();
                }

                if (ch.threadRecvVideo != null) {
                    try {
                        ch.threadRecvVideo.interrupt();
                        ch.threadRecvVideo.join();
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }

                    ch.threadRecvVideo = null;
                }

                if (ch.threadRecvAudio != null) {
                    try {
                        ch.threadRecvAudio.interrupt();
                        ch.threadRecvAudio.join();
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }

                    ch.threadRecvAudio = null;
                }

                if (ch.threadDecodeAudio != null) {
                    try {
                        ch.threadDecodeAudio.interrupt();
                        ch.threadDecodeAudio.join();
                    } catch (InterruptedException var12) {
                        var12.printStackTrace();
                    }

                    ch.threadDecodeAudio = null;
                }

                if (ch.threadDecodeVideo != null) {
                    try {
                        ch.threadDecodeVideo.interrupt();
                        ch.threadDecodeVideo.join();
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }

                    ch.threadDecodeVideo = null;
                }

                if (ch.threadRecvIOCtrl != null) {
                    try {
                        ch.threadRecvIOCtrl.interrupt();
                        ch.threadRecvIOCtrl.join();
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    ch.threadRecvIOCtrl = null;
                }

                if (ch.threadSendIOCtrl != null) {
                    try {
                        ch.threadSendIOCtrl.interrupt();
                        ch.threadSendIOCtrl.join();
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    ch.threadSendIOCtrl = null;
                }

                if (ch.threadStartDev != null && ch.threadStartDev.isAlive()) {
                    try {
                        ch.threadStartDev.interrupt();
                        ch.threadStartDev.join();
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                }

                ch.threadStartDev = null;
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

        if (this.threadCheckDevStatus != null) {
            this.threadCheckDevStatus.stopCheckStatus();
        }

        if (this.threadConnectDev != null) {
            this.threadConnectDev.stopDevConnect();
        }

        if (this.threadCheckDevStatus != null) {
            try {
                this.threadCheckDevStatus.interrupt();
                this.threadCheckDevStatus.join();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.threadCheckDevStatus = null;
        }

        if (this.threadConnectDev != null && this.threadConnectDev.isAlive()) {
            try {
                this.threadConnectDev.interrupt();
                this.threadConnectDev.join();
            } catch (InterruptedException var5) {
                var5.printStackTrace();
            }
        }

        this.threadConnectDev = null;
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
            ch.threadStartDev = new Camera.ThreadStartDev(ch);
            ch.threadStartDev.start();
            ch.threadRecvIOCtrl = new Camera.ThreadRecvIOCtrl(ch);
            ch.threadRecvIOCtrl.start();
            ch.threadSendIOCtrl = new Camera.ThreadSendIOCtrl(ch);
            ch.threadSendIOCtrl.start();
        } else {
            if (session.threadStartDev == null) {
                session.threadStartDev = new Camera.ThreadStartDev(session);
                session.threadStartDev.start();
            }

            if (session.threadRecvIOCtrl == null) {
                session.threadRecvIOCtrl = new Camera.ThreadRecvIOCtrl(session);
                session.threadRecvIOCtrl.start();
            }

            if (session.threadSendIOCtrl == null) {
                session.threadSendIOCtrl = new Camera.ThreadSendIOCtrl(session);
                session.threadSendIOCtrl.start();
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
                    if (ch.threadStartDev != null) {
                        ch.threadStartDev.stopAvClient();
                    }

                    if (ch.threadDecodeAudio != null) {
                        ch.threadDecodeAudio.stopAudioDecode();
                    }

                    if (ch.threadDecodeVideo != null) {
                        ch.threadDecodeVideo.stopVideoDecode();
                    }

                    if (ch.threadRecvAudio != null) {
                        ch.threadRecvAudio.stopAudioRecv();
                    }

                    if (ch.threadRecvVideo != null) {
                        ch.threadRecvVideo.stopVideoRecv();
                    }

                    if (ch.threadRecvIOCtrl != null) {
                        ch.threadRecvIOCtrl.stopIOCtrlRecv();
                    }

                    if (ch.threadSendIOCtrl != null) {
                        ch.threadSendIOCtrl.stopIOCtrlSend();
                    }

                    if (ch.threadRecvVideo != null) {
                        try {
                            ch.threadRecvVideo.interrupt();
                            ch.threadRecvVideo.join();
                        } catch (InterruptedException var13) {
                            var13.printStackTrace();
                        }

                        ch.threadRecvVideo = null;
                    }

                    if (ch.threadRecvAudio != null) {
                        try {
                            ch.threadRecvAudio.interrupt();
                            ch.threadRecvAudio.join();
                        } catch (InterruptedException var12) {
                            var12.printStackTrace();
                        }

                        ch.threadRecvAudio = null;
                    }

                    if (ch.threadDecodeAudio != null) {
                        try {
                            ch.threadDecodeAudio.interrupt();
                            ch.threadDecodeAudio.join();
                        } catch (InterruptedException var11) {
                            var11.printStackTrace();
                        }

                        ch.threadDecodeAudio = null;
                    }

                    if (ch.threadDecodeVideo != null) {
                        try {
                            ch.threadDecodeVideo.interrupt();
                            ch.threadDecodeVideo.join();
                        } catch (InterruptedException var10) {
                            var10.printStackTrace();
                        }

                        ch.threadDecodeVideo = null;
                    }

                    if (ch.threadRecvIOCtrl!= null) {
                        try {
                            ch.threadRecvIOCtrl.interrupt();
                            ch.threadRecvIOCtrl.join();
                        } catch (InterruptedException var9) {
                            var9.printStackTrace();
                        }

                        ch.threadRecvIOCtrl = null;
                    }

                    if (ch.threadSendIOCtrl != null) {
                        try {
                            ch.threadSendIOCtrl.interrupt();
                            ch.threadSendIOCtrl.join();
                        } catch (InterruptedException var8) {
                            var8.printStackTrace();
                        }

                        ch.threadSendIOCtrl = null;
                    }

                    if (ch.threadStartDev != null && ch.threadStartDev.isAlive()) {
                        try {
                            ch.threadStartDev.interrupt();
                            ch.threadStartDev.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }
                    }

                    ch.threadStartDev = null;
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
                    if (ch.threadRecvVideo == null) {
                        ch.threadRecvVideo = new Camera.ThreadRecvVideo(ch);
                        ch.threadRecvVideo.start();
                    }

                    if (ch.threadDecodeVideo == null) {
                        ch.threadDecodeVideo = new Camera.ThreadDecodeVideo(ch);
                        ch.threadDecodeVideo.start();
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
                    if (ch.threadRecvVideo != null) {
                        ch.threadRecvVideo.stopVideoRecv();

                        try {
                            ch.threadRecvVideo.interrupt();
                            ch.threadRecvVideo.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }

                        ch.threadRecvVideo = null;
                    }

                    if (ch.threadDecodeVideo != null) {
                        ch.threadDecodeVideo.stopVideoDecode();

                        try {
                            ch.threadDecodeVideo.interrupt();
                            ch.threadDecodeVideo.join();
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        ch.threadDecodeVideo = null;
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
                    if (this.threadSendAudio == null) {
                        this.threadSendAudio = new Camera.ThreadSendAudio(ch);
                        this.threadSendAudio.start();
                    }
                    break;
                }
            }

        }
    }

    public void stopSpeaking(int avChannel) {
        if (this.threadSendAudio != null) {
            this.threadSendAudio.stopAudioSend();

            try {
                this.threadSendAudio.interrupt();
                this.threadSendAudio.join();
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }

            this.threadSendAudio = null;
        }

    }

    public void startListening(int avChannel) {
        List var2 = this.channelInfoList;
        synchronized(this.channelInfoList) {
            for(int i = 0; i < this.channelInfoList.size(); ++i) {
                Camera.ChannelInfo ch = (Camera.ChannelInfo)this.channelInfoList.get(i);
                if (avChannel == ch.getChannel()) {
                    ch.audioAVFrameContainer.clear();
                    if (ch.threadRecvAudio == null) {
                        ch.threadRecvAudio = new Camera.ThreadRecvAudio(ch);
                        ch.threadRecvAudio.start();
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
                    if (ch.threadRecvAudio != null) {
                        ch.threadRecvAudio.stopAudioRecv();

                        try {
                            ch.threadRecvAudio.interrupt();
                            ch.threadRecvAudio.join();
                        } catch (InterruptedException var7) {
                            var7.printStackTrace();
                        }

                        ch.threadRecvAudio = null;
                    }

                    if (ch.threadDecodeAudio != null) {
                        ch.threadDecodeAudio.stopAudioDecode();

                        try {
                            ch.threadDecodeAudio.interrupt();
                            ch.threadDecodeAudio.join();
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        ch.threadDecodeAudio = null;
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
        this.isStartRecordVideo = true;
        this.mp4Name = mp4Name;
    }

    public void StopRecordvideo() {
        this.isStartRecordVideo = false;
    }

    public int getDispFrmPreSec() {
        int tmp = this.q;
        return tmp;
    }

    public int getRecvFrmPreSec() {
        int tmp = this.r;
        return tmp;
    }

    private synchronized boolean initAudio(int sampleRateInHz, int channel, int dataBit, int codec_id) {
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

                if (codec_id == MEDIA_CODEC_AUDIO_SPEEX) {
                    DecSpeex.InitDecoder(sampleRateInHz);
                } else if (codec_id == MEDIA_CODEC_AUDIO_MP3) {
                    int bit = dataBit == 1 ? 16 : 8;
                    DecMp3.InitDecoder(sampleRateInHz, bit);
                } else if (codec_id != MEDIA_CODEC_AUDIO_ADPCM && codec_id != MEDIA_CODEC_AUDIO_PCM) {
                    if (codec_id == MEDIA_CODEC_AUDIO_G726) {
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

    private synchronized void release(int codec_id) {
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
                if (ch.threadRecvIOCtrl != null) {
                    ch.threadRecvIOCtrl.a(recvintval);
                }

                if (ch.threadSendIOCtrl != null) {
                    ch.threadSendIOCtrl.a(sendintval);
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
                if (ch.threadDecodeAudio != null) {
                    ch.threadDecodeAudio.stopAudioDecode();
                }

                if (ch.threadDecodeVideo != null) {
                    ch.threadDecodeVideo.stopVideoDecode();
                }

                if (ch.threadRecvAudio != null) {
                    ch.threadRecvAudio.stopAudioRecv();
                }

                if (ch.threadRecvVideo != null) {
                    ch.threadRecvVideo.stopVideoRecv();
                }

                if (ch.threadRecvIOCtrl != null) {
                    ch.threadRecvIOCtrl.stopIOCtrlRecv();
                }

                if (ch.threadSendIOCtrl != null) {
                    ch.threadSendIOCtrl.stopIOCtrlSend();
                }

                if (ch.threadRecvVideo != null) {
                    try {
                        ch.threadRecvVideo.interrupt();
                        ch.threadRecvVideo.join();
                    } catch (InterruptedException var14) {
                        var14.printStackTrace();
                    }

                    ch.threadRecvVideo = null;
                }

                if (ch.threadRecvAudio != null) {
                    try {
                        ch.threadRecvAudio.interrupt();
                        ch.threadRecvAudio.join();
                    } catch (InterruptedException var13) {
                        var13.printStackTrace();
                    }

                    ch.threadRecvAudio = null;
                }

                if (ch.threadDecodeAudio != null) {
                    try {
                        ch.threadDecodeAudio.interrupt();
                        ch.threadDecodeAudio.join();
                    } catch (InterruptedException var12) {
                        var12.printStackTrace();
                    }

                    ch.threadDecodeAudio = null;
                }

                if (ch.threadDecodeVideo != null) {
                    try {
                        ch.threadDecodeVideo.interrupt();
                        ch.threadDecodeVideo.join();
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }

                    ch.threadDecodeVideo = null;
                }

                if (ch.threadRecvIOCtrl != null) {
                    try {
                        ch.threadRecvIOCtrl.interrupt();
                        ch.threadRecvIOCtrl.join();
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    ch.threadRecvIOCtrl = null;
                }

                if (ch.threadSendIOCtrl != null) {
                    try {
                        ch.threadSendIOCtrl.interrupt();
                        ch.threadSendIOCtrl.join();
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    ch.threadSendIOCtrl = null;
                }

                if (ch.threadStartDev != null && ch.threadStartDev.isAlive()) {
                    try {
                        ch.threadStartDev.interrupt();
                        ch.threadStartDev.join();
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                }

                ch.threadStartDev = null;
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

        if (this.threadCheckDevStatus != null) {
            this.threadCheckDevStatus.stopCheckStatus();
        }

        if (this.threadConnectDev != null) {
            this.threadConnectDev.stopDevConnect();
        }

        if (this.threadCheckDevStatus != null) {
            try {
                this.threadCheckDevStatus.interrupt();
                this.threadCheckDevStatus.join();
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.threadCheckDevStatus = null;
        }

        if (this.threadConnectDev != null && this.threadConnectDev.isAlive()) {
            try {
                this.threadConnectDev.interrupt();
                this.threadConnectDev.join();
            } catch (InterruptedException var5) {
                var5.printStackTrace();
            }
        }

        this.threadConnectDev = null;
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
                        if (this.threadSendRdFile == null) {
                            this.threadSendRdFile = new Camera.ThreadSendRdFile(ch, LocalPath, RemotePaht);
                            this.threadSendRdFile.start();
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
                if (this.threadSendRdFile != null) {
                    this.threadSendRdFile.stopSend();

                    try {
                        this.threadSendRdFile.interrupt();
                        this.threadSendRdFile.join();
                    } catch (InterruptedException var4) {
                        var4.printStackTrace();
                    }

                    this.threadSendRdFile = null;
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
        private int audioCodecId;//数据类型
        public Camera.IOCtrlQueue ioCtrlQueue;
        public AVFrameContainer videoAVFrameContainer;
        public AVFrameContainer audioAVFrameContainer;
        public Bitmap shotBmp;
        public Bitmap quickShotBmp;
        public int frameRate;
        public int outBufSize;
        public int h;
        public Camera.ThreadStartDev threadStartDev = null;
        public Camera.ThreadRecvIOCtrl threadRecvIOCtrl = null;
        public Camera.ThreadSendIOCtrl threadSendIOCtrl = null;
        public Camera.ThreadRecvVideo threadRecvVideo = null;
        public Camera.ThreadRecvAudio threadRecvAudio = null;
        public Camera.ThreadDecodeVideo threadDecodeVideo = null;
        public Camera.ThreadDecodeAudio threadDecodeAudio = null;

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

        public synchronized int getCodecId() {
            return this.audioCodecId;
        }

        public synchronized void setCodecId(int codec) {
            this.audioCodecId = codec;
        }

        public synchronized void setServiceType(long serviceType) {
            this.serviceType = serviceType;
            this.audioCodecId = (serviceType & 4096L) == 0L ? MEDIA_CODEC_AUDIO_G711A : MEDIA_CODEC_AUDIO_G711A;
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

    private class ThreadCheckDevStatus extends Thread {
        private boolean isThreadStart;
        private Object c;
        private int d;

        private ThreadCheckDevStatus() {
            this.isThreadStart = false;
            this.c = new Object();
            this.d = 0;
        }

        public void stopCheckStatus() {
            this.isThreadStart = false;
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
            this.isThreadStart = true;
            St_SInfo stSInfo = new St_SInfo();
            boolean var2 = false;

            while(this.isThreadStart && Camera.this.nIOTCSessionID < 0) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(1000L);
                    }
                } catch (Exception var11) {
                    var11.printStackTrace();
                }
            }

            while(true) {
                while(this.isThreadStart) {
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

    private class ThreadConnectDev extends Thread {
        private int connType = -1;
        private boolean isThreadStart = false;
        private Object d = new Object();

        public ThreadConnectDev(int connType) {
            this.connType = connType;
        }

        public void stopDevConnect() {
            this.isThreadStart = false;
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
            this.isThreadStart = true;

            label136:
            while(this.isThreadStart && Camera.this.nIOTCSessionID < 0) {
                int i;
                IRegisterIOTCListener listener;
                for(i = 0; i < Camera.this.iRegisterIOTCListenerList.size(); ++i) {
                    listener = (IRegisterIOTCListener)Camera.this.iRegisterIOTCListenerList.get(i);
                    listener.receiveSessionInfo(Camera.this, 1);
                }

                if (this.connType == 0) {
                    Camera.this.sid = IOTCAPIs.IOTC_Get_SessionID();
                    if (Camera.this.sid >= 0) {
                        Camera.this.nIOTCSessionID = IOTCAPIs.IOTC_Connect_ByUID_Parallel(Camera.this.uid, Camera.this.sid);
                    }

                    Camera.this.sid = -1;
                } else {
                    if (this.connType != 1) {
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

            this.isThreadStart = false;
            MLog.i("IOTCamera", "===ThreadConnectDev exit===");
        }
    }

    private class ThreadDecodeAudio extends Thread {
        private boolean isThreadStart;
        private Camera.ChannelInfo channelInfo;

        public void stopAudioDecode() {
            this.isThreadStart = false;
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
            this.isThreadStart = true;

            while(this.isThreadStart) {
                if (this.channelInfo.audioAVFrameContainer.getAVFrameNums() <= 0) {
                    try {
                        Thread.sleep(4L);
                    } catch (InterruptedException var21) {
                        var21.printStackTrace();
                    }
                } else {
                    AVFrame frame = this.channelInfo.audioAVFrameContainer.getFirstAVFrame();
                    nCodecId = frame.getCodecId();
                    if (bFirst && !Camera.this.k && (nCodecId == MEDIA_CODEC_AUDIO_MP3 || nCodecId == MEDIA_CODEC_AUDIO_SPEEX || nCodecId == MEDIA_CODEC_AUDIO_ADPCM || nCodecId == MEDIA_CODEC_AUDIO_PCM || nCodecId == MEDIA_CODEC_AUDIO_G726)) {
                        bFirst = false;
                        nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                        nDatabits = frame.getFlags() & 2;
                        nDatabits = nDatabits == 2 ? 1 : 0;
                        nChannel = frame.getFlags() & 1;
                        bInitAudio = Camera.this.initAudio(nSamplerate, nChannel, nDatabits, nCodecId);
                        if (!bInitAudio) {
                            break;
                        }
                    }

                    if (nCodecId == MEDIA_CODEC_AUDIO_SPEEX) {
                        DecSpeex.Decode(frame.frmData, frame.getFrmSize(), speexOutBuf);
                        Camera.this.audioTrack.write(speexOutBuf, 0, 160);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 160;
                    } else if (nCodecId == MEDIA_CODEC_AUDIO_MP3) {
                        int len = DecMp3.Decode(frame.frmData, frame.getFrmSize(), mp3OutBuf);
                        Camera.this.audioTrack.write(mp3OutBuf, 0, len);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / len;
                    } else if (nCodecId == MEDIA_CODEC_AUDIO_ADPCM) {
                        DecADPCM.Decode(frame.frmData, frame.getFrmSize(), adpcmOutBuf);
                        Camera.this.audioTrack.write(adpcmOutBuf, 0, 640);
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 640;
                    } else if (nCodecId == MEDIA_CODEC_AUDIO_PCM) {
                        Camera.this.audioTrack.write(frame.frmData, 0, frame.getFrmSize());
                        nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / frame.getFrmSize();
                    } else if (nCodecId == MEDIA_CODEC_AUDIO_G726) {
                        DecG726.g726_decode(frame.frmData, (long)frame.getFrmSize(), G726OutBuf, G726OutBufLen);
                        MLog.i("IOTCamera", "G726 decode size:" + G726OutBufLen[0]);
                        Camera.this.audioTrack.write(G726OutBuf, 0, (int)G726OutBufLen[0]);
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
                Camera.this.release(nCodecId);
            }

            MLog.i("IOTCamera", "===ThreadDecodeAudio exit===");
        }
    }

    private class ThreadDecodeVideo extends Thread {
        private boolean isRunning = false;
        private Camera.ChannelInfo channelInfo;

        public ThreadDecodeVideo(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopVideoDecode() {
            this.isRunning = false;
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
            this.isRunning = true;
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
                                        while(this.isRunning) {
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
                                if (avFrame.getCodecId() == MEDIA_CODEC_VIDEO_H264) {
                                    if (!bInitH264) {
                                        DecH264.InitDecoder();
                                        bInitH264 = true;
                                    }

                                    DecH264.DecoderNal(avFrame.frmData, avFrameSizex, framePara, bufOut, mEnableDither);
                                    if (Camera.this.isStartRecordVideo) {
                                        if (mp4StartFlag && bInitMp4Record) {
                                            int isKeyframe = avFrame.isIFrame() ? 1 : 0;
                                            H264toMP4.mp4packvideo(avFrame.frmData, avFrameSizex, isKeyframe, Camera.this.frameRate);
                                        } else if (avFrame.isIFrame() && !bInitMp4Record && Camera.this.frameRate > 0 && Camera.this.mp4Name.length() > 0) {
                                            if (H264toMP4.mp4init(Camera.this.mp4Name, videoWidth, videoHeight) == 1) {
                                                MLog.i(" mp4 video", "mp4 filename[" + Camera.this.mp4Name + "]" + "width[" + videoWidth + "]height" + videoHeight);
                                                bInitMp4Record = true;
                                                H264toMP4.mp4packvideo(avFrame.frmData, avFrameSizex, 1, Camera.this.frameRate);
                                                mp4StartFlag = true;
                                            } else {
                                                bInitMp4Record = false;
                                                MLog.i("Tutk mp4 video", "mp4 error filename[" + Camera.this.mp4Name + "]" + "width[" + videoWidth + "]height" + videoHeight);
                                            }
                                        }
                                    }

                                    if (bInitMp4Record && !Camera.this.isStartRecordVideo) {
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

                                if (avFrame.getCodecId() == MEDIA_CODEC_VIDEO_H264) {
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

    private class ThreadSendRdFile extends Thread {
        private boolean e = false;
        private boolean f = false;
        private Camera.ChannelInfo channelInfo;
        private String localFile;
        private String remoteFile;
        byte[] a = new byte[1024];
        byte[] b = new byte[2048];
        int c = 0;
        private int j = 0;

        public ThreadSendRdFile(Camera.ChannelInfo channel, String LocalFile, String RemoteFile) {
            this.channelInfo = channel;
            this.localFile = LocalFile;
            this.remoteFile = RemoteFile;
        }

        public void stopSend() {
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
                MLog.i("=ThreadSendRdFile=", "RDT_start[" + this.remoteFile + "]");

                for(; this.e; this.e = false) {
                    if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                        byte[] bFileName = this.remoteFile.getBytes();
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
                                fosLocalFile = new FileOutputStream(this.localFile);
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

            Camera.this.threadSendRdFile = null;
            RDTAPIs.RDT_Destroy(RDT_ID);
        }
    }

    private class ThreadRecvAudio extends Thread {
        private final int b = 1280;
        private int ret = 0;
        private boolean isStart = false;
        private Camera.ChannelInfo channelInfo;

        public ThreadRecvAudio(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopAudioRecv() {
            this.isStart = false;
        }

        public void run() {
            this.isStart = true;

            while(this.isStart && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
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
            if (this.isStart && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), IOTYPE_USER_IPCAM_AUDIOSTART, Packet.intToByteArray_Little(Camera.this.m));
            }

            while(this.isStart) {
                if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                    this.ret = AVAPIs.avRecvAudioData(this.channelInfo.getAvIndex(), recvBuf, recvBuf.length, bytAVFrame, 24, pFrmNo);
                    if (this.ret < 0 && this.ret != AV_ER_DATA_NOREADY) {
                        MLog.i("IOTCamera", "avRecvAudioData < 0");
                    }

                    if (this.ret <= 0) {
                        if (this.ret == AV_ER_DATA_NOREADY) {
                            try {
                                Thread.sleep((long)(nFPS == 0 ? 33 : 1000 / nFPS));
                            } catch (InterruptedException var21) {
                                var21.printStackTrace();
                            }
                        } else if (this.ret == AV_ER_LOSED_THIS_FRAME) {
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
                        if (bFirst && (!Camera.this.k && (nCodecId == MEDIA_CODEC_AUDIO_MP3 || nCodecId == MEDIA_CODEC_AUDIO_SPEEX || nCodecId == MEDIA_CODEC_AUDIO_ADPCM || nCodecId == MEDIA_CODEC_AUDIO_PCM || nCodecId == MEDIA_CODEC_AUDIO_G726) || nCodecId == MEDIA_CODEC_AUDIO_G711A)) {
                            bFirst = false;
                            nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                            nDatabits = frame.getFlags() & 2;
                            nDatabits = nDatabits == 2 ? 1 : 0;
                            nChannel = frame.getFlags() & 1;
                            if (nCodecId == MEDIA_CODEC_AUDIO_SPEEX) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 160;
                            } else if (nCodecId == MEDIA_CODEC_AUDIO_ADPCM) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 640;
                            } else if (nCodecId == MEDIA_CODEC_AUDIO_PCM) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / frame.getFrmSize();
                            } else if (nCodecId == MEDIA_CODEC_AUDIO_G711A) {
                                nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / 320;
                            }

                            bInitAudio = Camera.this.initAudio(nSamplerate, nChannel, nDatabits, nCodecId);
                            if (!bInitAudio) {
                                break;
                            }
                        }

                        //编码音频数据，并将数据写入到audio播放器播放
                        if (nCodecId == MEDIA_CODEC_AUDIO_SPEEX) {
                            DecSpeex.Decode(recvBuf, this.ret, speexOutBuf);
                            Camera.this.audioTrack.write(speexOutBuf, 0, 160);
                        } else if (nCodecId == MEDIA_CODEC_AUDIO_MP3) {
                            int len = DecMp3.Decode(recvBuf, this.ret, mp3OutBuf);
                            Camera.this.audioTrack.write(mp3OutBuf, 0, len);
                            nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / len;
                        } else if (nCodecId == MEDIA_CODEC_AUDIO_ADPCM) {
                            DecADPCM.Decode(recvBuf, this.ret, adpcmOutBuf);
                            Camera.this.audioTrack.write(adpcmOutBuf, 0, 640);
                        } else if (nCodecId == MEDIA_CODEC_AUDIO_PCM) {
                            Camera.this.audioTrack.write(recvBuf, 0, this.ret);
                        } else if (nCodecId == MEDIA_CODEC_AUDIO_G726) {
                            DecG726.g726_decode(recvBuf, (long)this.ret, G726OutBuf, G726OutBufLen);
                            MLog.i("IOTCamera", "G726 decode size:" + G726OutBufLen[0]);
                            Camera.this.audioTrack.write(G726OutBuf, 0, (int)G726OutBufLen[0]);
                            nFPS = nSamplerate * (nChannel == 0 ? 1 : 2) * (nDatabits == 0 ? 8 : 16) / 8 / (int)G726OutBufLen[0];
                        } else if (nCodecId == MEDIA_CODEC_AUDIO_G711A) {
                            PCMA.alaw2linear(recvBuf, G711OutBuf, this.ret);
                            Camera.this.audioTrack.write(G711OutBuf, 0, this.ret);
                        }
                    }
                }
            }

            if (bInitAudio) {
                Camera.this.release(nCodecId);
            }

            this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), IOTYPE_USER_IPCAM_AUDIOSTOP, Packet.intToByteArray_Little(Camera.this.m));
            MLog.i("IOTCamera", "===ThreadRecvAudio exit===");
        }
    }

    private class ThreadRecvIOCtrl extends Thread {
        private final int b = 0;
        private boolean isRunning = false;
        private Camera.ChannelInfo channelInfo;
        private int sleepTime = 100;

        public ThreadRecvIOCtrl(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopIOCtrlRecv() {
            this.isRunning = false;
        }

        public void a(int times) {
            if (times > 5) {
                this.sleepTime = times;
            }

        }

        public void run() {
            this.isRunning = true;

            while(this.isRunning && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
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
                            if (!this.isRunning) {
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
                                    ch.setCodecId(format);
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
                            Thread.sleep((long)this.sleepTime);
                        } catch (InterruptedException var10) {
                            var10.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class ThreadRecvVideo extends Thread {
        private boolean isRunning = false;
        private Camera.ChannelInfo channelInfo;

        public ThreadRecvVideo(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopVideoRecv() {
            this.isRunning = false;
        }

        public void run() {
            System.gc();
            this.isRunning = true;

            while(this.isRunning && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
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
            if (this.isRunning && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                int nDelayTime_ms = 0;
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), 255, Packet.intToByteArray_Little(nDelayTime_ms));
                this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), IOTYPE_USER_IPCAM_START, Packet.intToByteArray_Little(Camera.this.m));
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
                                    if (!this.isRunning) {
                                        this.channelInfo.videoAVFrameContainer.clear();
                                        if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                                            this.channelInfo.ioCtrlQueue.addData(this.channelInfo.getAvIndex(), IOTYPE_USER_IPCAM_STOP, Packet.intToByteArray_Little(Camera.this.m));
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

                            int nReadSizex = AVAPIs.avRecvFrameData2(this.channelInfo.getAvIndex(), buf, buf.length, outBufSize,
                                    outFrmSize, pFrmInfoBuf, pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);
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
                                if (nCodecIdx == MEDIA_CODEC_VIDEO_H264) {
                                    if (!frame.isIFrame() && (long)pFrmNo[0] != nPrevFrmNo + 1L) {
                                        if (nemptyCount > 30) {
                                            nemptyCount = 0;
                                            Camera.this.sendIOCtrl(this.channelInfo.getAvIndex(), IOTYPE_USER_IPCAM_START, Packet.intToByteArray_Little(Camera.this.m));
                                        } else {
                                            ++nemptyCount;
                                        }

                                        MLog.i("IOTCamera", "Incorrect frame no(" + pFrmNo[0] + "), prev:" + nPrevFrmNo + " -> drop frame");
                                    } else if (!this.channelInfo.videoAVFrameContainer.isDrop()) {
                                        nPrevFrmNo = (long)pFrmNo[0];
                                        this.channelInfo.videoAVFrameContainer.addAVFrame(frame);
                                    }
                                } else if (nCodecIdx == MEDIA_CODEC_VIDEO_MPEG4) {
                                    if (frame.isIFrame() || (long)pFrmNo[0] == nPrevFrmNo + 1L) {
                                        nPrevFrmNo = (long)pFrmNo[0];
                                        this.channelInfo.videoAVFrameContainer.addAVFrame(frame);
                                    }
                                } else if (nCodecIdx == MEDIA_CODEC_VIDEO_MJPEG) {
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
                            } else if (nReadSizex == AV_ER_SESSION_CLOSE_BY_REMOTE) {
                                MLog.i("IOTCamera", "AV_ER_SESSION_CLOSE_BY_REMOTE");
                            } else if (nReadSizex == AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                                MLog.i("IOTCamera", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                            } else if (nReadSizex == AV_ER_DATA_NOREADY) {
                                try {
                                    Thread.sleep(32L);
                                } catch (InterruptedException var22) {
                                    var22.printStackTrace();
                                }
                            } else if (nReadSizex != AV_ER_BUFPARA_MAXSIZE_INSUFF) {
                                if (nReadSizex == AV_ER_MEM_INSUFF) {
                                    ++nFrmCount;
                                    ++nIncompleteFrmCount;
                                    MLog.i("IOTCamera", "AV_ER_MEM_INSUFF");
                                } else if (nReadSizex == AV_ER_LOSED_THIS_FRAME) {
                                    MLog.i("IOTCamera", "AV_ER_LOSED_THIS_FRAME2");
                                    ++nFrmCount;
                                    ++nIncompleteFrmCount;
                                } else if (nReadSizex == AV_ER_INCOMPLETE_FRAME) {
                                    ++nFrmCount;
                                    this.channelInfo.outBufSize += outBufSize[0];
                                    if (outFrmInfoBufSize[0] != 0 && (double)outFrmSize[0] * 0.9D <= (double)outBufSize[0] && pFrmInfoBuf[2] != 0) {
                                        frameData = new byte[outFrmSize[0]];
                                        System.arraycopy(buf, 0, frameData, 0, outFrmSize[0]);
                                        nCodecIdx = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                                        if (nCodecIdx == MEDIA_CODEC_VIDEO_MJPEG) {
                                            ++nIncompleteFrmCount;
                                        } else if (nCodecIdx != MEDIA_CODEC_VIDEO_MPEG4 && nCodecIdx != MEDIA_CODEC_VIDEO_H264) {
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

    private class ThreadSendAudio extends Thread {
        private boolean isRunning = false;
        private int nAVChannelID = -1;
        private int nIOTCChannelID = -1;
        private Camera.ChannelInfo channelInfo = null;

        public ThreadSendAudio(Camera.ChannelInfo ch) {
            this.channelInfo = ch;
        }

        public void stopAudioSend() {
            if (Camera.this.nIOTCSessionID >= 0 && this.nIOTCChannelID >= 0) {
                AVAPIs.avServExit(Camera.this.nIOTCSessionID, this.nIOTCChannelID);
                Camera.this.sendIOCtrl(this.channelInfo.channel, IOTYPE_USER_IPCAM_SPEAKERSTOP, SMsgAVIoctrlAVStream.parseContent(this.nIOTCChannelID));
            }

            this.isRunning = false;
        }

        public void run() {
            super.run();
            if (Camera.this.nIOTCSessionID < 0) {
                MLog.i("IOTCamera", "=== ThreadSendAudio exit because SID < 0 ===");
            } else {
                this.isRunning = true;
                boolean bInitSpeexEnc = false;
                boolean bInitG726Enc = false;
                boolean bInitADPCM = false;
                boolean bInitPCM = false;
                boolean bInitG711 = false;
                int nMinBufSize = 0;
                int nReadBytesx = -1;
                this.nIOTCChannelID = IOTCAPIs.IOTC_Session_Get_Free_Channel(Camera.this.nIOTCSessionID);
                if (this.nIOTCChannelID < 0) {
                    MLog.i("IOTCamera", "=== ThreadSendAudio exit becuase no more channel for connection ===");
                } else {
                    Camera.this.sendIOCtrl(this.channelInfo.channel, 848, SMsgAVIoctrlAVStream.parseContent(this.nIOTCChannelID));
                    MLog.i("IOTCamera", "start avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.nIOTCChannelID + ")");

                    while(this.isRunning && (this.nAVChannelID = AVAPIs.avServStart(Camera.this.nIOTCSessionID, (byte[])null, (byte[])null, 60L, 0L, this.nIOTCChannelID)) < 0) {
                        MLog.i("IOTCamera", "avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.nIOTCChannelID + ") : " + this.nAVChannelID);
                    }

                    MLog.i("IOTCamera", "avServerStart(" + Camera.this.nIOTCSessionID + ", " + this.nIOTCChannelID + ") : " + this.nAVChannelID);
                    if (this.isRunning && this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_SPEEX) {
                        EncSpeex.InitEncoder(8);
                        bInitSpeexEnc = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "Speex encoder init");
                    }

                    if (this.isRunning && this.channelInfo.getCodecId() == AVFrame.MEDIA_CODEC_AUDIO_ADPCM) {
                        EncADPCM.ResetEncoder();
                        bInitADPCM = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "ADPCM encoder init");
                    }

                    if (this.isRunning && this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_G726) {
                        EncG726.g726_enc_state_create((byte)0, (byte)2);
                        bInitG726Enc = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "G726 encoder init");
                    }

                    if (this.isRunning && this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_G711A) {
                        bInitG711 = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                        MLog.i("IOTCamera", "G711 encoder init");
                    }

                    if (this.isRunning && this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_PCM) {
                        bInitPCM = true;
                        nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);
                    }

                    AudioRecord recorder = null;
                    if (this.isRunning && (bInitADPCM || bInitG726Enc || bInitSpeexEnc || bInitPCM || bInitG711)) {
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

                    while(this.isRunning) {
                        int nReadBytes;
                        if (this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_SPEEX) {
                            nReadBytes = recorder.read(inSpeexBuf, 0, inSpeexBuf.length);
                            if (nReadBytes > 0) {
                                int len = EncSpeex.Encode(inSpeexBuf, nReadBytes, outSpeexBuf);
                                byte flagx = 2;
                                byte[] frameInfox = SFrameInfo.parseContent((short)MEDIA_CODEC_AUDIO_SPEEX, flagx, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                AVAPIs.avSendAudioData(this.nAVChannelID, outSpeexBuf, len, frameInfox, 16);
                            }
                        } else {
                            byte flag;
                            byte[] frameInfo;
                            if (this.channelInfo.getCodecId() == AVFrame.MEDIA_CODEC_AUDIO_ADPCM) {
                                nReadBytes = recorder.read(inADPCMBuf, 0, inADPCMBuf.length);
                                if (nReadBytes > 0) {
                                    EncADPCM.Encode(inADPCMBuf, nReadBytes, outADPCMBuf);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)AVFrame.MEDIA_CODEC_AUDIO_ADPCM, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.nAVChannelID, outADPCMBuf, nReadBytes / 4, frameInfo, 16);
                                }
                            } else if (this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_G726) {
                                nReadBytes = recorder.read(inG726Buf, 0, inG726Buf.length);
                                if (nReadBytes > 0) {
                                    EncG726.g726_encode(inG726Buf, (long)nReadBytes, outG726Buf, outG726BufLen);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)MEDIA_CODEC_AUDIO_G726, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.nAVChannelID, outG726Buf, (int)outG726BufLen[0], frameInfo, 16);
                                }
                            } else if (this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_PCM) {
                                nReadBytes = recorder.read(inPCMBuf, 0, inPCMBuf.length);
                                if (nReadBytes > 0) {
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)MEDIA_CODEC_AUDIO_PCM, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.nAVChannelID, inPCMBuf, nReadBytes, frameInfo, 16);
                                }
                            } else if (this.channelInfo.getCodecId() == MEDIA_CODEC_AUDIO_G711A) {
                                nReadBytes = recorder.read(inG711Buf, 0, inG711Buf.length);
                                if (nReadBytes > 0) {
                                    PCMA.linear2alaw(inG711Buf, 0, outG711Buf, nReadBytes);
                                    flag = 2;
                                    frameInfo = SFrameInfo.parseContent((short)MEDIA_CODEC_AUDIO_G711A, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
                                    AVAPIs.avSendAudioData(this.nAVChannelID, outG711Buf, nReadBytes, frameInfo, 16);
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

                    if (this.nAVChannelID >= 0) {
                        AVAPIs.avServStop(this.nAVChannelID);
                    }

                    if (this.nIOTCChannelID >= 0) {
                        IOTCAPIs.IOTC_Session_Channel_OFF(Camera.this.nIOTCSessionID, this.nIOTCChannelID);
                    }

                    this.nAVChannelID = -1;
                    this.nIOTCChannelID = -1;
                    MLog.i("IOTCamera", "===ThreadSendAudio exit===");
                }
            }
        }
    }

    private class ThreadSendIOCtrl extends Thread {
        private boolean isRunning = false;
        private Camera.ChannelInfo channelInfo;
        private int sleepTime = 50;

        public ThreadSendIOCtrl(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopIOCtrlSend() {
            this.isRunning = false;
            if (this.channelInfo.getAvIndex() >= 0) {
                MLog.i("IOTCamera", "avSendIOCtrlExit(" + this.channelInfo.getAvIndex() + ")");
                AVAPIs.avSendIOCtrlExit(this.channelInfo.getAvIndex());
            }

        }

        public void a(int times) {
            if (times > 5) {
                this.sleepTime = times;
            }

        }

        public void run() {
            this.isRunning = true;

            while(this.isRunning && (Camera.this.nIOTCSessionID < 0 || this.channelInfo.getAvIndex() < 0)) {
                try {
                    synchronized(Camera.this.c) {
                        Camera.this.c.wait(1000L);
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

            if (this.isRunning && Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0) {
                int nDelayTime_ms = 0;
                AVAPIs.avSendIOCtrl(this.channelInfo.getAvIndex(), 255, Packet.intToByteArray_Little(nDelayTime_ms), 4);
            }

            while(true) {
                while(this.isRunning) {
                    if (Camera.this.nIOTCSessionID >= 0 && this.channelInfo.getAvIndex() >= 0 && !this.channelInfo.ioCtrlQueue.isEmpty()) {
                        Camera.IOCtrlQueue.IOCtrlSet data = this.channelInfo.ioCtrlQueue.getIOCtrlSet();
                        if (this.isRunning && data != null) {
                            int ret = AVAPIs.avSendIOCtrl(this.channelInfo.getAvIndex(), data.IOCtrlType, data.IOCtrlBuf, data.IOCtrlBuf.length);
                            if (ret >= 0) {
                                MLog.i("IOTCamera", "avSendIOCtrl(" + this.channelInfo.getAvIndex() + ", 0x" + Integer.toHexString(data.IOCtrlType) + ", " + Camera.a(data.IOCtrlBuf, data.IOCtrlBuf.length) + ")");
                            } else {
                                MLog.i("IOTCamera", "avSendIOCtrl failed : " + ret);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep((long)this.sleepTime);
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

    private class ThreadStartDev extends Thread {
        private boolean isRunning = false;
        private Camera.ChannelInfo channelInfo;
        private Object d = new Object();
        private int e = 0;

        public ThreadStartDev(Camera.ChannelInfo channel) {
            this.channelInfo = channel;
        }

        public void stopAvClient() {
            this.isRunning = false;
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
            this.isRunning = true;
            boolean var1 = true;

            label112:
            while(this.isRunning) {
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
                        //30L：timeout_sec
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
