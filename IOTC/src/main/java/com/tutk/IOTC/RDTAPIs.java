//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

public class RDTAPIs {
	public static long ms_verRDTApis = 0L;
	public static final int API_ER_ANDROID_NULL = -10000;
	public static final int RDT_ER_NoERROR = 0;
	public static final int RDT_ER_NOT_INITIALIZED = -10000;
	public static final int RDT_ER_ALREADY_INITIALIZED = -10001;
	public static final int RDT_ER_EXCEED_MAX_CHANNEL = -10002;
	public static final int RDT_ER_MEM_INSUFF = -10003;
	public static final int RDT_ER_FAIL_CREATE_THREAD = -10004;
	public static final int RDT_ER_FAIL_CREATE_MUTEX = -10005;
	public static final int RDT_ER_RDT_DESTROYED = -10006;
	public static final int RDT_ER_TIMEOUT = -10007;
	public static final int RDT_ER_INVALID_RDT_ID = -10008;
	public static final int RDT_ER_RCV_DATA_END = -10009;
	public static final int RDT_ER_REMOTE_ABORT = -10010;
	public static final int RDT_ER_LOCAL_ABORT = -10011;
	public static final int RDT_ER_CHANNEL_OCCUPIED = -10012;
	public static final int RDT_ER_NO_PERMISSION = -10013;

	static {
		try {
			System.loadLibrary("RDTAPIs");
		} catch (UnsatisfiedLinkError var1) {
			System.out.println("loadLibrary(RDTAPIs)," + var1.getMessage());
		}

	}

	public RDTAPIs() {
	}

	public static native int RDT_GetRDTApiVer();

	public static native int RDT_Initialize();

	public static native int RDT_DeInitialize();

	public static native int RDT_Create(int var0, int var1, int var2);

	public static native int RDT_Destroy(int var0);

	public static native int RDT_Write(int var0, byte[] var1, int var2);

	public static native int RDT_Read(int var0, byte[] var1, int var2, int var3);

	public static native int RDT_Status_Check(int var0, St_RDT_Status var1);

	public static native int RDT_Abort(int var0);

	public static native void RDT_Set_Max_Channel_Number(long var0);

	public static native void RDT_Set_Log_Path(String var0, int var1);
}
