//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AVIOCTRLDEFs {
    public static final int IOTYPE_USER_IPCAM_START = 511;
    public static final int IOTYPE_USER_IPCAM_STOP = 767;
    public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 768;
    public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 769;
    public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 848;
    public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 849;
    public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 800;
    public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_RESP = 801;
    public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ = 802;
    public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP = 803;
    public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ = 804;
    public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP = 805;
    public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ = 806;
    public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP = 807;
    public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ = 808;
    public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP = 809;
    public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ = 810;
    public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP = 811;
    public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 816;
    public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 817;
    public static final int IOTYPE_USER_IPCAM_SETPASSWORD_REQ = 818;
    public static final int IOTYPE_USER_IPCAM_SETPASSWORD_RESP = 819;
    public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_REQ = 832;
    public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_RESP = 833;
    public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ = 834;
    public static final int IOTYPE_USER_IPCAM_SETWIFI_RESP = 835;
    public static final int IOTYPE_USER_IPCAM_GETWIFI_REQ = 836;
    public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP = 837;
    public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ_2 = 838;
    public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP_2 = 839;
    public static final int IOTYPE_USER_IPCAM_SETRECORD_REQ = 784;
    public static final int IOTYPE_USER_IPCAM_SETRECORD_RESP = 785;
    public static final int IOTYPE_USER_IPCAM_GETRECORD_REQ = 786;
    public static final int IOTYPE_USER_IPCAM_GETRECORD_RESP = 787;
    public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_REQ = 788;
    public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_RESP = 789;
    public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_REQ = 790;
    public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_RESP = 791;
    public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 792;
    public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 793;
    public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 794;
    public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 795;
    public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_REQ = 1024;
    public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_RESP = 1025;
    public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_REQ = 1026;
    public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_RESP = 1027;
    public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ = 864;
    public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_RESP = 865;
    public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ = 866;
    public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP = 867;
    public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ = 880;
    public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_RESP = 881;
    public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ = 882;
    public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP = 883;
    public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ = 896;
    public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP = 897;
    public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND = 4097;
    public static final int IOTYPE_USER_IPCAM_EVENT_REPORT = 8191;
    public static final int AVIOCTRL_EVENT_ALL = 0;
    public static final int AVIOCTRL_EVENT_MOTIONDECT = 1;
    public static final int AVIOCTRL_EVENT_VIDEOLOST = 2;
    public static final int AVIOCTRL_EVENT_IOALARM = 3;
    public static final int AVIOCTRL_EVENT_MOTIONPASS = 4;
    public static final int AVIOCTRL_EVENT_VIDEORESUME = 5;
    public static final int AVIOCTRL_EVENT_IOALARMPASS = 6;
    public static final int AVIOCTRL_EVENT_EXPT_REBOOT = 16;
    public static final int AVIOCTRL_EVENT_SDFAULT = 17;
    public static final int AVIOCTRL_RECORD_PLAY_PAUSE = 0;
    public static final int AVIOCTRL_RECORD_PLAY_STOP = 1;
    public static final int AVIOCTRL_RECORD_PLAY_STEPFORWARD = 2;
    public static final int AVIOCTRL_RECORD_PLAY_STEPBACKWARD = 3;
    public static final int AVIOCTRL_RECORD_PLAY_FORWARD = 4;
    public static final int AVIOCTRL_RECORD_PLAY_BACKWARD = 5;
    public static final int AVIOCTRL_RECORD_PLAY_SEEKTIME = 6;
    public static final int AVIOCTRL_RECORD_PLAY_END = 7;
    public static final int AVIOCTRL_RECORD_PLAY_START = 16;
    public static final int AVIOCTRL_PTZ_STOP = 0;
    public static final int AVIOCTRL_PTZ_UP = 1;
    public static final int AVIOCTRL_PTZ_DOWN = 2;
    public static final int AVIOCTRL_PTZ_LEFT = 3;
    public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
    public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
    public static final int AVIOCTRL_PTZ_RIGHT = 6;
    public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
    public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
    public static final int AVIOCTRL_PTZ_AUTO = 9;
    public static final int AVIOCTRL_PTZ_SET_POINT = 10;
    public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
    public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;
    public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
    public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
    public static final int AVIOCTRL_PTZ_MODE_RUN = 15;
    public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
    public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
    public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;
    public static final int AVIOCTRL_PTZ_FLIP = 19;
    public static final int AVIOCTRL_PTZ_START = 20;
    public static final int AVIOCTRL_PTZ_LEFT_RIGHT = 55;
    public static final int AVIOCTRL_PTZ_UP_DOWN = 56;
    public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
    public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;
    public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
    public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;
    public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
    public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;
    public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;
    public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
    public static final int AVIOCTRL_AUTO_PAN_START = 29;
    public static final int AVIOCTRL_PATTERN_START = 30;
    public static final int AVIOCTRL_PATTERN_STOP = 31;
    public static final int AVIOCTRL_PATTERN_RUN = 32;
    public static final int AVIOCTRL_SET_AUX = 33;
    public static final int AVIOCTRL_CLEAR_AUX = 34;
    public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;
    public static final int AVIOCTRL_QUALITY_UNKNOWN = 0;
    public static final int AVIOCTRL_QUALITY_MAX = 1;
    public static final int AVIOCTRL_QUALITY_HIGH = 2;
    public static final int AVIOCTRL_QUALITY_MIDDLE = 3;
    public static final int AVIOCTRL_QUALITY_LOW = 4;
    public static final int AVIOCTRL_QUALITY_MIN = 5;
    public static final int AVIOTC_WIFIAPMODE_ADHOC = 0;
    public static final int AVIOTC_WIFIAPMODE_MANAGED = 1;
    public static final int AVIOTC_WIFIAPENC_INVALID = 0;
    public static final int AVIOTC_WIFIAPENC_NONE = 1;
    public static final int AVIOTC_WIFIAPENC_WEP = 2;
    public static final int AVIOTC_WIFIAPENC_WPA_TKIP = 3;
    public static final int AVIOTC_WIFIAPENC_WPA_AES = 4;
    public static final int AVIOTC_WIFIAPENC_WPA2_TKIP = 5;
    public static final int AVIOTC_WIFIAPENC_WPA2_AES = 6;
    public static final int AVIOTC_WIFIAPENC_WPA_PSK_TKIP = 7;
    public static final int AVIOTC_WIFIAPENC_WPA_PSK_AES = 8;
    public static final int AVIOTC_WIFIAPENC_WPA2_PSK_TKIP = 9;
    public static final int AVIOTC_WIFIAPENC_WPA2_PSK_AES = 10;
    public static final int AVIOTC_RECORDTYPE_OFF = 0;
    public static final int AVIOTC_RECORDTYPE_FULLTIME = 1;
    public static final int AVIOTC_RECORDTYPE_ALAM = 2;
    public static final int AVIOTC_RECORDTYPE_MANUAL = 3;
    public static final int AVIOCTRL_ENVIRONMENT_INDOOR_50HZ = 0;
    public static final int AVIOCTRL_ENVIRONMENT_INDOOR_60HZ = 1;
    public static final int AVIOCTRL_ENVIRONMENT_OUTDOOR = 2;
    public static final int AVIOCTRL_ENVIRONMENT_NIGHT = 3;
    public static final int AVIOCTRL_VIDEOMODE_NORMAL = 0;
    public static final int AVIOCTRL_VIDEOMODE_FLIP = 1;
    public static final int AVIOCTRL_VIDEOMODE_MIRROR = 2;
    public static final int AVIOCTRL_VIDEOMODE_FLIP_MIRROR = 3;
    public static final int IOTYPE_USER_IPCAM_SET_UPGRADEONLIN_REQ = 8262;
    public static final int IOTYPE_USER_IPCAM_SET_UPGRADEONLIN_RESP = 8263;
    public static final int UPGRADE_ONLINE_TYPE_CHECK = 0;
    public static final int UPGRADE_ONLINE_TYPE_SYS = 1;
    public static final int UPGRADE_ONLINE_TYPE_UI = 2;
    public static final int UPGRADE_ONLINE_TYPE_SYS_UI = 3;
    public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ = 928;
    public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP = 929;
    public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ = 944;
    public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP = 945;
    public static final int IOTYPE_USER_IPCAM_SET_TIME_REQ = 8274;
    public static final int IOTYPE_USER_IPCAM_SET_TIME_RESP = 8275;
    public static final int IOTYPE_USER_IPCAM_DEVREBOOT_REQ = 8204;
    public static final int IOTYPE_USER_IPCAM_DEVREBOOT_RESP = 8205;
    public static final int IOTYPE_USER_IPCAM_GET_DOOR_STAUTS_REQ = 1298;
    public static final int IOTYPE_USER_IPCAM_GET_DOOR_STATUS_RESP = 1299;
    public static final int IOTYPE_USER_IPCAM_SET_DOOR_STAUTS_REQ = 1300;
    public static final int IOTYPE_USER_IPCAM_SET_DOOR_STATUS_RESP = 1301;
    public static final int IOTYPE_USER_IPCAM_GET_DOORPASSWORDMODE_REQ = 1302;
    public static final int IOTYPE_USER_IPCAM_GET_DOORPASSWORDMODE_RESP = 1303;
    public static final int IOTYPE_USER_IPCAM_SET_DOORPASSWORDMODE_REQ = 1304;
    public static final int IOTYPE_USER_IPCAM_SET_DOORPASSWORDMODE_RESP = 1305;
    public static final int IOTYPE_USER_IPCAM_SET_DOORPASSWORD_REQ = 1312;
    public static final int IOTYPE_USER_IPCAM_SET_DOORPASSWORD_RESP = 1313;
    public static final int IOTYPE_USER_IPCAM_TRANSFER_TTY_DATA_REQ = 8318;
    public static final int IOTYPE_USER_IPCAM_TRANSFER_TTY_DATA_RESP = 8319;
    public static final int IOTYPE_USER_IPCAM_THIRDPART_SETTING_RESP = 8279;
    public static final int IOTYPE_USER_IPCAM_THIRDPART_SETTING_REQ = 8278;
    public static final int AVIOCTRL_THIRDPART_SET_LED_STATUS = 0;
    public static final int AVIOCTRL_THIRDPART_GET_LED_STATUS = 1;
    public static final int IOTYPE_USER_IPCAM_EDIT_FILES_REQ = 8276;
    public static final int IOTYPE_USER_IPCAM_EDIT_FILES_RESP = 8277;
    public static final int IOTYPE_USER_IPCAM_DOWNLOAD_FILE_REQ = 8258;
    public static final int IOTYPE_USER_IPCAM_DOWNLOAD_FILE_RESP = 8259;
    public static final int ENUM_FILE_EDIT_REMOVEFILE = 16;
    public static final int ENUM_FILE_EDIT_DOWNLOAD = 17;

    public AVIOCTRLDEFs() {
    }

    public static class SAvEvent {
        byte[] a = new byte[8];
        byte[] b = new byte[2];

        public SAvEvent() {
        }

        public static int getTotalSize() {
            return 12;
        }
    }

    public static class SFrameInfo {
        byte[] a = new byte[3];

        public SFrameInfo() {
        }

        public static byte[] parseContent(short codec_id, byte flags, byte cam_index, byte online_num, int timestamp) {
            byte[] result = new byte[16];
            byte[] codec = Packet.shortToByteArray_Little(codec_id);
            System.arraycopy(codec, 0, result, 0, 2);
            result[2] = flags;
            result[3] = cam_index;
            result[4] = online_num;
            byte[] time = Packet.intToByteArray_Little(timestamp);
            System.arraycopy(time, 0, result, 12, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlAVStream {
        int a = 0;
        byte[] b = new byte[4];

        public SMsgAVIoctrlAVStream() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlDeviceInfoReq {
        static byte[] a = new byte[4];

        public SMsgAVIoctrlDeviceInfoReq() {
        }

        public static byte[] parseContent() {
            return a;
        }
    }

    public class SMsgAVIoctrlDeviceInfoResp {
        byte[] a = new byte[16];
        byte[] b = new byte[16];
        byte[] c = new byte[8];

        public SMsgAVIoctrlDeviceInfoResp() {
        }
    }

    public static class SMsgAVIoctrlDownloadFileReq {
        public SMsgAVIoctrlDownloadFileReq() {
        }

        public static byte[] parseContent(int filenum) {
            byte[] result = new byte[8];
            byte[] nfilenum = Packet.intToByteArray_Little(filenum);
            System.arraycopy(nfilenum, 0, result, 0, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlEditfileReq {
        public SMsgAVIoctrlEditfileReq() {
        }

        public static byte[] parseContent(int channel, int command, int param, byte[] bSTimeDay, int reserved) {
            byte[] result = new byte[32];
            byte[] ch = Packet.intToByteArray_Little(channel);
            byte[] bcommand = Packet.intToByteArray_Little(command);
            byte[] bparam = Packet.intToByteArray_Little(param);
            System.arraycopy(ch, 0, result, 0, 4);
            System.arraycopy(bcommand, 0, result, 4, 4);
            System.arraycopy(bparam, 0, result, 8, 4);
            System.arraycopy(bSTimeDay, 0, result, 12, 8);
            return result;
        }
    }

    public class SMsgAVIoctrlEvent {
        byte[] a = new byte[4];

        public SMsgAVIoctrlEvent() {
        }
    }

    public class SMsgAVIoctrlEventConfig {
        public SMsgAVIoctrlEventConfig() {
        }
    }

    public static class SMsgAVIoctrlFormatExtStorageReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlFormatExtStorageReq() {
        }

        public static byte[] parseContent(int storage) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(storage);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlFormatExtStorageResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlFormatExtStorageResp() {
        }
    }

    public static class SMsgAVIoctrlGetAudioOutFormatReq {
        public SMsgAVIoctrlGetAudioOutFormatReq() {
        }

        public static byte[] parseContent() {
            return new byte[8];
        }
    }

    public class SMsgAVIoctrlGetAudioOutFormatResp {
        public int channel;
        public int format;

        public SMsgAVIoctrlGetAudioOutFormatResp() {
        }
    }

    public static class SMsgAVIoctrlGetDoorPasModeReq {
        public SMsgAVIoctrlGetDoorPasModeReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[4];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlGetEnvironmentReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetEnvironmentReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlGetEnvironmentResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlGetEnvironmentResp() {
        }
    }

    public static class SMsgAVIoctrlGetMotionDetectReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetMotionDetectReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlGetMotionDetectResp {
        public SMsgAVIoctrlGetMotionDetectResp() {
        }
    }

    public class SMsgAVIoctrlGetRcdDurationReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetRcdDurationReq() {
        }
    }

    public class SMsgAVIoctrlGetRcdDurationResp {
        public SMsgAVIoctrlGetRcdDurationResp() {
        }
    }

    public static class SMsgAVIoctrlGetRecordReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetRecordReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlGetRecordResp {
        public SMsgAVIoctrlGetRecordResp() {
        }
    }

    public static class SMsgAVIoctrlGetStreamCtrlReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetStreamCtrlReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlGetStreamCtrlResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlGetStreamCtrlResp() {
        }
    }

    public static class SMsgAVIoctrlGetSupportStreamReq {
        public SMsgAVIoctrlGetSupportStreamReq() {
        }

        public static byte[] parseContent() {
            return new byte[4];
        }

        public static int getContentSize() {
            return 4;
        }
    }

    public class SMsgAVIoctrlGetSupportStreamResp {
        public AVIOCTRLDEFs.SStreamDef[] mStreamDef;
        public long number;

        public SMsgAVIoctrlGetSupportStreamResp() {
        }
    }

    public static class SMsgAVIoctrlGetUpdateReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetUpdateReq() {
        }

        public static byte[] parseContent(int asktype) {
            byte[] result = new byte[8];
            byte[] btype = Packet.intToByteArray_Little(asktype);
            System.arraycopy(btype, 0, result, 0, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlGetVideoModeReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlGetVideoModeReq() {
        }

        public static byte[] parseContent(int channel) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlGetVideoModeResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlGetVideoModeResp() {
        }
    }

    public static class SMsgAVIoctrlGetWifiReq {
        static byte[] a = new byte[4];

        public SMsgAVIoctrlGetWifiReq() {
        }

        public static byte[] parseContent() {
            return a;
        }
    }

    public class SMsgAVIoctrlGetWifiResp {
        byte[] a = new byte[32];
        byte[] b = new byte[32];

        public SMsgAVIoctrlGetWifiResp() {
        }
    }

    public static class SMsgAVIoctrlListEventReq {
        byte[] a = new byte[8];
        byte[] b = new byte[8];
        byte[] c = new byte[2];

        public SMsgAVIoctrlListEventReq() {
        }

        public static byte[] parseConent(int channel, long startutctime, long endutctime, byte event, byte status) {
            Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            Calendar stopCal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            startCal.setTimeInMillis(startutctime);
            stopCal.setTimeInMillis(endutctime);
            System.out.println("search from " + startCal.get(Calendar.YEAR) + "/" + startCal.get(Calendar.MONTH) + "/" + startCal.get(Calendar.DAY_OF_MONTH) + " "
                    + startCal.get(Calendar.HOUR_OF_DAY) + ":" + startCal.get(Calendar.MINUTE) + ":" + startCal.get(Calendar.SECOND));
            System.out.println("       to   " + stopCal.get(Calendar.YEAR) + "/" + stopCal.get(Calendar.MONTH) + "/" + stopCal.get(Calendar.DAY_OF_MONTH)
                    + " " + stopCal.get(Calendar.HOUR_OF_DAY) + ":" + stopCal.get(Calendar.MINUTE) + ":" + stopCal.get(Calendar.SECOND));
            byte[] result = new byte[24];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            byte[] start = AVIOCTRLDEFs.STimeDay.parseContent(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH) + 1, startCal.get(Calendar.DAY_OF_MONTH), startCal.get(Calendar.DAY_OF_WEEK), startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), 0);
            System.arraycopy(start, 0, result, 4, 8);
            byte[] stop = AVIOCTRLDEFs.STimeDay.parseContent(stopCal.get(Calendar.YEAR), stopCal.get(Calendar.MONTH) + 1, stopCal.get(Calendar.DAY_OF_MONTH), stopCal.get(Calendar.DAY_OF_WEEK), stopCal.get(Calendar.HOUR_OF_DAY), stopCal.get(Calendar.MINUTE), 0);
            System.arraycopy(stop, 0, result, 12, 8);
            result[20] = event;
            result[21] = status;
            return result;
        }
    }

    public class SMsgAVIoctrlListEventResp {
        public SMsgAVIoctrlListEventResp() {
        }
    }

    public static class SMsgAVIoctrlListWifiApReq {
        static byte[] a = new byte[4];

        public SMsgAVIoctrlListWifiApReq() {
        }

        public static byte[] parseContent() {
            return a;
        }
    }

    public class SMsgAVIoctrlListWifiApResp {
        public SMsgAVIoctrlListWifiApResp() {
        }
    }

    public static class SMsgAVIoctrlPlayRecord {
        byte[] a = new byte[8];
        byte[] b = new byte[4];

        public SMsgAVIoctrlPlayRecord() {
        }

        public static byte[] parseContent(int channel, int command, int param, long time) {
            byte[] result = new byte[24];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            byte[] cmd = Packet.intToByteArray_Little(command);
            System.arraycopy(cmd, 0, result, 4, 4);
            byte[] p = Packet.intToByteArray_Little(param);
            System.arraycopy(p, 0, result, 8, 4);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            cal.setTimeInMillis(time);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            byte[] timedata = AVIOCTRLDEFs.STimeDay.parseContent(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_WEEK),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            System.arraycopy(timedata, 0, result, 12, 8);
            return result;
        }

        public static byte[] parseContent(int channel, int command, int param, byte[] time) {
            byte[] result = new byte[24];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            byte[] cmd = Packet.intToByteArray_Little(command);
            System.arraycopy(cmd, 0, result, 4, 4);
            byte[] p = Packet.intToByteArray_Little(param);
            System.arraycopy(p, 0, result, 8, 4);
            System.arraycopy(time, 0, result, 12, 8);
            return result;
        }
    }

    public class SMsgAVIoctrlPlayRecordResp {
        byte[] a = new byte[4];

        public SMsgAVIoctrlPlayRecordResp() {
        }
    }

    public static class SMsgAVIoctrlPtzCmd {
        byte[] a = new byte[2];

        public SMsgAVIoctrlPtzCmd() {
        }

        public static byte[] parseContent(byte control, byte speed, byte point, byte limit, byte aux, byte channel) {
            byte[] result = new byte[]{control, speed, point, limit, aux, channel, 0, 0};
            return result;
        }
    }

    public static class SMsgAVIoctrlSetDevRebootReq {
        public SMsgAVIoctrlSetDevRebootReq() {
        }

        public static byte[] parseContent() {
            return new byte[8];
        }
    }

    public static class SMsgAVIoctrlSetDevRebootResp {
        byte[] a = new byte[4];

        public SMsgAVIoctrlSetDevRebootResp() {
        }
    }

    public static class SMsgAVIoctrlSetDoorModeReq {
        public SMsgAVIoctrlSetDoorModeReq() {
        }

        public static byte[] parseContent(int channel, int state) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            byte[] sta = Packet.intToByteArray_Little(state);
            System.arraycopy(ch, 0, result, 0, 4);
            System.arraycopy(sta, 0, result, 4, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlSetDoorStatusReq {
        byte[] a = new byte[32];

        public SMsgAVIoctrlSetDoorStatusReq() {
        }

        public static byte[] parseContent(int channel, int door1, int door2, String doorpwd) {
            byte[] result = new byte[44];
            byte[] ch = Packet.intToByteArray_Little(channel);
            byte[] bdoor = Packet.intToByteArray_Little(door1);
            byte[] bdoor2 = Packet.intToByteArray_Little(door2);
            byte[] pwd = doorpwd.getBytes();
            System.arraycopy(ch, 0, result, 0, 4);
            System.arraycopy(bdoor, 0, result, 4, 4);
            System.arraycopy(bdoor2, 0, result, 8, 4);
            System.arraycopy(pwd, 0, result, 12, pwd.length);
            return result;
        }
    }

    public static class SMsgAVIoctrlSetEnvironmentReq {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetEnvironmentReq() {
        }

        public static byte[] parseContent(int channel, byte mode) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = mode;
            return result;
        }
    }

    public class SMsgAVIoctrlSetEnvironmentResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetEnvironmentResp() {
        }
    }

    public static class SMsgAVIoctrlSetMotionDetectReq {
        public SMsgAVIoctrlSetMotionDetectReq() {
        }

        public static byte[] parseContent(int channel, int sensitivity) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            byte[] sen = Packet.intToByteArray_Little(sensitivity);
            System.arraycopy(ch, 0, result, 0, 4);
            System.arraycopy(sen, 0, result, 4, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlSetMotionDetectResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetMotionDetectResp() {
        }
    }

    public static class SMsgAVIoctrlSetPasswdReq {
        byte[] a = new byte[32];
        byte[] b = new byte[32];

        public SMsgAVIoctrlSetPasswdReq() {
        }

        public static byte[] parseContent(String oldPwd, String newPwd) {
            byte[] oldpwd = oldPwd.getBytes();
            byte[] newpwd = newPwd.getBytes();
            byte[] result = new byte[64];
            System.arraycopy(oldpwd, 0, result, 0, oldpwd.length);
            System.arraycopy(newpwd, 0, result, 32, newpwd.length);
            return result;
        }
    }

    public class SMsgAVIoctrlSetPasswdResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetPasswdResp() {
        }
    }

    public class SMsgAVIoctrlSetRcdDurationReq {
        public SMsgAVIoctrlSetRcdDurationReq() {
        }
    }

    public class SMsgAVIoctrlSetRcdDurationResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetRcdDurationResp() {
        }
    }

    public static class SMsgAVIoctrlSetRecordReq {
        byte[] a = new byte[4];

        public SMsgAVIoctrlSetRecordReq() {
        }

        public static byte[] parseContent(int channel, int recordType) {
            byte[] result = new byte[12];
            byte[] ch = Packet.intToByteArray_Little(channel);
            byte[] type = Packet.intToByteArray_Little(recordType);
            System.arraycopy(ch, 0, result, 0, 4);
            System.arraycopy(type, 0, result, 4, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlSetRecordResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetRecordResp() {
        }
    }

    public static class SMsgAVIoctrlSetStreamCtrlReq {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetStreamCtrlReq() {
        }

        public static byte[] parseContent(int channel, byte quality) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = quality;
            return result;
        }
    }

    public class SMsgAVIoctrlSetStreamCtrlResp {
        byte[] a = new byte[4];

        public SMsgAVIoctrlSetStreamCtrlResp() {
        }
    }

    public static class SMsgAVIoctrlSetTime {
        public int lCurrentTimeSec;
        public int nDiffZoneMin;
        byte[] a = new byte[4];

        public SMsgAVIoctrlSetTime() {
        }

        public static byte[] parseContent(int lCurrentTimeSec, int nDiffZoneMin) {
            byte[] result = new byte[16];
            byte[] bCurrentTimeSec = Packet.intToByteArray_Little(lCurrentTimeSec);
            System.arraycopy(bCurrentTimeSec, 0, result, 0, 4);
            byte[] bDiffZoneMin = Packet.intToByteArray_Little(nDiffZoneMin);
            System.arraycopy(bDiffZoneMin, 0, result, 4, 4);
            return result;
        }
    }

    public static class SMsgAVIoctrlSetVideoModeReq {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetVideoModeReq() {
        }

        public static byte[] parseContent(int channel, byte mode) {
            byte[] result = new byte[8];
            byte[] ch = Packet.intToByteArray_Little(channel);
            System.arraycopy(ch, 0, result, 0, 4);
            result[4] = mode;
            return result;
        }
    }

    public class SMsgAVIoctrlSetVideoModeResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetVideoModeResp() {
        }
    }

    public static class SMsgAVIoctrlSetWifiReq {
        byte[] a = new byte[32];
        byte[] b = new byte[32];
        byte[] c = new byte[10];

        public SMsgAVIoctrlSetWifiReq() {
        }

        public static byte[] parseContent(byte[] ssid, byte[] password, byte mode, byte enctype) {
            byte[] result = new byte[76];
            System.arraycopy(ssid, 0, result, 0, ssid.length);
            System.arraycopy(password, 0, result, 32, password.length);
            result[64] = mode;
            result[65] = enctype;
            return result;
        }
    }

    public class SMsgAVIoctrlSetWifiResp {
        byte[] a = new byte[3];

        public SMsgAVIoctrlSetWifiResp() {
        }
    }

    public static class SMsgAVIoctrlThirdpartReq {
        byte[] a = new byte[8];

        public SMsgAVIoctrlThirdpartReq() {
        }

        public static byte[] parseContent(int cmd_type, int value) {
            byte[] result = new byte[16];
            byte[] type = Packet.intToByteArray_Little(cmd_type);
            byte[] val = Packet.intToByteArray_Little(value);
            System.arraycopy(type, 0, result, 0, 4);
            System.arraycopy(val, 0, result, 4, 4);
            return result;
        }
    }

    public class SMsgAVIoctrlThirdpartResp {
        public SMsgAVIoctrlThirdpartResp() {
        }
    }

    public static class SMsgAVIoctrlTimeZone {
        public int cbSize;
        public int nIsSupportTimeZone;
        public int nGMTDiff;
        public byte[] szTimeZoneString = new byte[256];

        public SMsgAVIoctrlTimeZone() {
        }

        public static byte[] parseContent() {
            return new byte[268];
        }

        public static byte[] parseContent(int cbSize, int nIsSupportTimeZone, int nGMTDiff, byte[] szTimeZoneString) {
            byte[] result = new byte[268];
            byte[] size = Packet.intToByteArray_Little(cbSize);
            System.arraycopy(size, 0, result, 0, 4);
            byte[] isSupportTimeZone = Packet.intToByteArray_Little(nIsSupportTimeZone);
            System.arraycopy(isSupportTimeZone, 0, result, 4, 4);
            byte[] GMTDiff = Packet.intToByteArray_Little(nGMTDiff);
            System.arraycopy(GMTDiff, 0, result, 8, 4);
            System.arraycopy(szTimeZoneString, 0, result, 12, szTimeZoneString.length);
            return result;
        }
    }

    public static class SStreamDef {
        public int index;
        public int channel;

        public SStreamDef(byte[] data) {
            this.index = Packet.byteArrayToShort_Little(data, 0);
            this.channel = Packet.byteArrayToShort_Little(data, 2);
        }

        public String toString() {
            return "CH" + String.valueOf(this.index + 1);
        }
    }

    public static class STimeDay {
        private byte[] a = new byte[8];
        public short year;
        public byte month;
        public byte day;
        public byte wday;
        public byte hour;
        public byte minute;
        public byte second;

        public STimeDay(byte[] data) {
            System.arraycopy(data, 0, this.a, 0, 8);
            this.year = Packet.byteArrayToShort_Little(data, 0);
            this.month = data[2];
            this.day = data[3];
            this.wday = data[4];
            this.hour = data[5];
            this.minute = data[6];
            this.second = data[7];
        }

        public long getTimeInMillis() {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            cal.set(this.year, this.month - 1, this.day, this.hour, this.minute, this.second);
            return cal.getTimeInMillis();
        }

        public String getLocalTime() {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            calendar.setTimeInMillis(this.getTimeInMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(calendar.getTime());
        }

        public byte[] toByteArray() {
            return this.a;
        }

        public static byte[] parseContent(int year, int month, int day, int wday, int hour, int minute, int second) {
            byte[] result = new byte[8];
            byte[] y = Packet.shortToByteArray_Little((short)year);
            System.arraycopy(y, 0, result, 0, 2);
            result[2] = (byte)month;
            result[3] = (byte)day;
            result[4] = (byte)wday;
            result[5] = (byte)hour;
            result[6] = (byte)minute;
            result[7] = (byte)second;
            return result;
        }
    }

    public static class SWifiAp {
        public byte[] ssid = new byte[32];
        public byte mode;
        public byte enctype;
        public byte signal;
        public byte status;

        public static int getTotalSize() {
            return 36;
        }

        public SWifiAp(byte[] data) {
            System.arraycopy(data, 1, this.ssid, 0, data.length);
            this.mode = data[32];
            this.enctype = data[33];
            this.signal = data[34];
            this.status = data[35];
        }

        public SWifiAp(byte[] bytsSSID, byte bytMode, byte bytEnctype, byte bytSignal, byte bytStatus) {
            System.arraycopy(bytsSSID, 0, this.ssid, 0, bytsSSID.length);
            this.mode = bytMode;
            this.enctype = bytEnctype;
            this.signal = bytSignal;
            this.status = bytStatus;
        }
    }
}
