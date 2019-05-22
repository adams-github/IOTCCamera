//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tutk.IOTC;

public class RDCTRLDEFs {
    public static final byte RDT_COMMAND_FILE_TOTAL = 0;
    public static final byte RDT_COMMAND_FILE_NAME = 1;
    public static final byte RDT_COMMAND_FILE_SIZE = 2;
    public static final byte RDT_COMMAND_FILE_TIMES = 3;
    public static final byte RDT_COMMAND_FILE_START = 4;
    public static final byte RDT_COMMAND_FILE_STOP = 5;
    public static final byte RDT_COMMAND_START = 6;
    public static final byte RDT_COMMAND_STOP = 7;
    public static final byte RDT_COMMAND_NEXT_FILE = 8;

    public RDCTRLDEFs() {
    }

    public static class RDTCommand {
        byte[] a = new byte[127];

        public RDTCommand() {
        }

        public static byte[] parseContent(byte type, byte[] _content) {
            byte[] result = new byte[128];
            result[0] = type;
            System.arraycopy(_content, 0, result, 1, _content.length);
            return result;
        }
    }
}
