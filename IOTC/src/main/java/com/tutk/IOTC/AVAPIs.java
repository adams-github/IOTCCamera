//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

public class AVAPIs {
	public static final int TIME_DELAY_DELTA = 1;
	public static final int TIME_DELAY_MIN = 4;
	public static final int TIME_DELAY_MAX = 500;
	public static final int TIME_DELAY_INITIAL = 0;
	public static final int TIME_SPAN_LOSED = 1000;
	public static final int IOTYPE_INNER_SND_DATA_DELAY = 255;
	public static final int API_ER_ANDROID_NULL = -10000;
	public static final int AV_ER_NoERROR = 0;
	public static final int AV_ER_INVALID_ARG = -20000;
	public static final int AV_ER_BUFPARA_MAXSIZE_INSUFF = -20001;
	public static final int AV_ER_EXCEED_MAX_CHANNEL = -20002;
	public static final int AV_ER_MEM_INSUFF = -20003;
	public static final int AV_ER_FAIL_CREATE_THREAD = -20004;
	public static final int AV_ER_EXCEED_MAX_ALARM = -20005;
	public static final int AV_ER_EXCEED_MAX_SIZE = -20006;
	public static final int AV_ER_SERV_NO_RESPONSE = -20007;
	public static final int AV_ER_CLIENT_NO_AVLOGIN = -20008;
	public static final int AV_ER_WRONG_VIEWACCorPWD = -20009;
	public static final int AV_ER_INVALID_SID = -20010;
	public static final int AV_ER_TIMEOUT = -20011;
	public static final int AV_ER_DATA_NOREADY = -20012;
	public static final int AV_ER_INCOMPLETE_FRAME = -20013;
	public static final int AV_ER_LOSED_THIS_FRAME = -20014;
	public static final int AV_ER_SESSION_CLOSE_BY_REMOTE = -20015;
	public static final int AV_ER_REMOTE_TIMEOUT_DISCONNECT = -20016;
	public static final int AV_ER_SERVER_EXIT = -20017;
	public static final int AV_ER_CLIENT_EXIT = -20018;
	public static final int AV_ER_NOT_INITIALIZED = -20019;
	public static final int AV_ER_CLIENT_NOT_SUPPORT = -20020;
	public static final int AV_ER_SENDIOCTRL_ALREADY_CALLED = -20021;
	public static final int AV_ER_SENDIOCTRL_EXIT = -20022;
	public static final int AV_ER_NO_PERMISSION = -20023;

	static {
		try {
			System.loadLibrary("AVAPIs");
		} catch (UnsatisfiedLinkError var1) {
			System.out.println("loadLibrary(AVAPIs)," + var1.getMessage());
		}

	}

	public AVAPIs() {
	}

	public static native int avGetAVApiVer();

	public static native int avInitialize(int var0);

	public static native int avDeInitialize();

	public static native int avSendIOCtrl(int var0, int var1, byte[] var2, int var3);

	public static native int avRecvIOCtrl(int var0, int[] var1, byte[] var2, int var3, int var4);

	public static native int avSendIOCtrlExit(int var0);

	public static native int avServStart(int var0, byte[] var1, byte[] var2, long var3, long var5, int var7);

	public static native void avServStop(int var0);

	public static native void avServExit(int var0, int var1);

	public static native int avSendFrameData(int var0, byte[] var1, int var2, byte[] var3, int var4);

	public static native int avSendAudioData(int var0, byte[] var1, int var2, byte[] var3, int var4);

	public static native int avClientStart(int var0, String var1, String var2, long var3, long[] var5, int var6);

	public static native int avClientStart2(int var0, String var1, String var2, long var3, long[] var5, int var6, int[] var7);

	public static native void avClientStop(int var0);

	public static native void avClientExit(int var0, int var1);

	public static native int avRecvFrameData(int var0, byte[] var1, int var2, byte[] var3, int var4, int[] var5);

	public static native int avRecvFrameData2(int var0, byte[] var1, int var2, int[] var3, int[] var4, byte[] var5, int var6, int[] var7, int[] var8);

	public static native int avRecvAudioData(int var0, byte[] var1, int var2, byte[] var3, int var4, int[] var5);

	public static native int avCheckAudioBuf(int var0);

	public static native void avClientSetMaxBufSize(int var0);

	public static native int avClientCleanBuf(int var0);

	public static native int avClientCleanVideoBuf(int var0);

	public static native int avClientCleanAudioBuf(int var0);

	public static native int avServStart2(int var0, String var1, String var2, long var3, long var5, int var7);

	public static native int avServStart3(int var0, String var1, String var2, long var3, long var5, int var7, int[] var8);

	public static native void avServSetResendSize(int var0, long var1);

	public static native int avServSetDelayInterval(int var0, int var1, int var2);

	public static native float avResendBufUsageRate(int var0);
}
