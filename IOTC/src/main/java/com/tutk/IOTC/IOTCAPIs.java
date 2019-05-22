//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

public class IOTCAPIs {
	public static final int API_ER_ANDROID_NULL = -10000;
	public static final int IOTC_ER_NoERROR = 0;
	public static final int IOTC_ER_SERVER_NOT_RESPONSE = -1;
	public static final int IOTC_ER_FAIL_RESOLVE_HOSTNAME = -2;
	public static final int IOTC_ER_ALREADY_INITIALIZED = -3;
	public static final int IOTC_ER_FAIL_CREATE_MUTEX = -4;
	public static final int IOTC_ER_FAIL_CREATE_THREAD = -5;
	public static final int IOTC_ER_FAIL_CREATE_SOCKET = -6;
	public static final int IOTC_ER_FAIL_SOCKET_OPT = -7;
	public static final int IOTC_ER_FAIL_SOCKET_BIND = -8;
	public static final int IOTC_ER_UNLICENSE = -10;
	public static final int IOTC_ER_LOGIN_ALREADY_CALLED = -11;
	public static final int IOTC_ER_NOT_INITIALIZED = -12;
	public static final int IOTC_ER_TIMEOUT = -13;
	public static final int IOTC_ER_INVALID_SID = -14;
	public static final int IOTC_ER_UNKNOWN_DEVICE = -15;
	public static final int IOTC_ER_FAIL_GET_LOCAL_IP = -16;
	public static final int IOTC_ER_LISTEN_ALREADY_CALLED = -17;
	public static final int IOTC_ER_EXCEED_MAX_SESSION = -18;
	public static final int IOTC_ER_CAN_NOT_FIND_DEVICE = -19;
	public static final int IOTC_ER_CONNECT_IS_CALLING = -20;
	public static final int IOTC_ER_SESSION_CLOSE_BY_REMOTE = -22;
	public static final int IOTC_ER_REMOTE_TIMEOUT_DISCONNECT = -23;
	public static final int IOTC_ER_DEVICE_NOT_LISTENING = -24;
	public static final int IOTC_ER_CH_NOT_ON = -26;
	public static final int IOTC_ER_FAIL_CONNECT_SEARCH = -27;
	public static final int IOTC_ER_MASTER_TOO_FEW = -28;
	public static final int IOTC_ER_AES_CERTIFY_FAIL = -29;
	public static final int IOTC_ER_SESSION_NO_FREE_CHANNEL = -31;
	public static final int IOTC_ER_TCP_TRAVEL_FAILED = -32;
	public static final int IOTC_ER_TCP_CONNECT_TO_SERVER_FAILED = -33;
	public static final int IOTC_ER_CLIENT_NOT_SECURE_MODE = -34;
	public static final int IOTC_ER_CLIENT_SECURE_MODE = -35;
	public static final int IOTC_ER_DEVICE_NOT_SECURE_MODE = -36;
	public static final int IOTC_ER_DEVICE_SECURE_MODE = -37;
	public static final int IOTC_ER_INVALID_MODE = -38;
	public static final int IOTC_ER_EXIT_LISTEN = -39;
	public static final int IOTC_ER_NO_PERMISSION = -40;
	public static final int IOTC_ER_NETWORK_UNREACHABLE = -41;
	public static final int IOTC_ER_FAIL_SETUP_RELAY = -42;
	public static final int IOTC_ER_NOT_SUPPORT_RELAY = -43;
	public static final int IOTC_ER_NO_SERVER_LIST = -44;
	public static final int IOTC_ER_DEVICE_MULTI_LOGIN = -45;
	public static final int IOTC_ER_INVALID_ARG = -46;
	public static final int IOTC_ER_NOT_SUPPORT_PE = -47;

	static {
		try {
			System.loadLibrary("IOTCAPIs");
		} catch (UnsatisfiedLinkError var1) {
			System.out.println("loadLibrary(IOTCAPIs)," + var1.getMessage());
		}

	}

	public IOTCAPIs() {
	}

	public static native int IOTC_Get_Login_Info(long[] var0);

	public static native void IOTC_Get_Version(long[] var0);

	public static native void IOTC_Set_Max_Session_Number(long var0);

	public static native int IOTC_Initialize(int var0, String var1, String var2, String var3, String var4);

	public static native int IOTC_Initialize2(int var0);

	public static native int IOTC_DeInitialize();

	public static native int IOTC_Device_Login(String var0, String var1, String var2);

	public static native int IOTC_Listen(long var0);

	public static native int IOTC_Listen2(long var0, String var2, int var3);

	public static native int IOTC_Connect_ByUID(String var0);

	public static native int IOTC_Connect_ByUID2(String var0, String var1, int var2);

	public static native void IOTC_Connect_Stop();

	public static native int IOTC_Get_SessionID();

	public static native int IOTC_Connect_ByUID_Parallel(String var0, int var1);

	public static native int IOTC_Connect_Stop_BySID(int var0);

	public static native int IOTC_Session_Check(int var0, St_SInfo var1);

	public static native int IOTC_Session_Read(int var0, byte[] var1, int var2, int var3, int var4);

	public static native int IOTC_Session_Write(int var0, byte[] var1, int var2, int var3);

	public static native void IOTC_Session_Close(int var0);

	public static native int IOTC_Session_Get_Free_Channel(int var0);

	public static native int IOTC_Session_Channel_ON(int var0, int var1);

	public static native int IOTC_Session_Channel_OFF(int var0, int var1);

	public static native int IOTC_Get_Nat_Type();

	public static native int IOTC_IsDeviceSecureMode(int var0);

	public static native st_LanSearchInfo[] IOTC_Lan_Search(int[] var0, int var1);

	public static native st_LanSearchInfo2[] IOTC_Lan_Search2(int[] var0, int var1);

	public static native void IOTC_Set_Log_Path(String var0, int var1);

	public static native void IOTC_Listen_Exit();

	public static native void IOTC_Get_Login_Info_ByCallBackFn(Object var0);

	public static native int IOTC_Session_Check_ByCallBackFn(int var0, Object var1);

	public static native int IOTC_Set_Partial_Encryption(int var0, int var1);

	public static native int IOTC_Session_Read_Check_Lost(int var0, byte[] var1, int var2, int var3, int[] var4, int[] var5, int var6);

	public static native void IOTC_Set_Device_Name(String var0);

	public static native void IOTC_TCPRelayOnly_TurnOn();

	public static native void IOTC_Setup_LANConnection_Timeout(int var0);

	public void loginInfoCB(long nLonInfo) {
		System.out.println("[parent] LoginInfo Callback, nLogInfo=" + nLonInfo);
	}

	public void sessionStatusCB(int nSID, int nErrCode) {
		System.out.println("[parent] SessionStatus Callback, nSID=" + nSID + ", nErrCode=" + nErrCode);
	}
}
