package com.gracelogic.platform.tcpserver;

import java.io.*;

public class TcpServerUtils {
    public static byte[] readBytes(DataInputStream is) throws IOException {
        ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
        byte[] b = new byte[Constants.MAX_MESSAGE_BYTE_SIZE];
        int n;
        n = is.read(b);
        if (n == -1) return null;
        bStrm.write(b, 0, n);
        return bStrm.toByteArray();
    }

    public static int indexOf(byte[] outerArray, int outerArrayOffset, byte[] smallerArray) {
        for(int i = outerArrayOffset; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    public static int byteArrayToInt(byte[] b, int start, int length) {
        int dt = 0;
        if ((b[start] & 0x80) != 0) {
            dt = Integer.MAX_VALUE;
        }
        for (int i = 0; i < length; i++) {
            dt = (dt << 8) + (b[start++] & 255);
        }
        return dt;
    }

    public static byte[] intToByteArray(int n, int byteCount) {
        byte[] res = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            res[byteCount - i - 1] = (byte) ((n >> i * 8) & 255);
        }
        return res;
    }

    public static byte[] clipArray(byte[] data, int startPos) {
        if (startPos > data.length || startPos < 0) {
            return null;
        }

        if (startPos > 0) {
            byte[] clippedData = new byte[data.length - startPos];
            System.arraycopy(data, startPos, clippedData, 0, clippedData.length);
            return clippedData;
        } else {
            return data;
        }
    }

}
