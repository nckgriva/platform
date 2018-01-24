package com.gracelogic.platform.tcpserver.dto;

import java.util.Arrays;

public class Message {
    private byte [] bytes;
    private boolean dataInitializedAsString = false; //for toString only

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Message(byte[] bytes) {
        this.bytes = bytes;
    }

    public Message(String message) {
        dataInitializedAsString = true;
        if (message != null) {
            this.bytes = message.getBytes();
        }
    }

    @Override
    public String toString() {
        if (dataInitializedAsString) {
            return "Message{" +
                    "bytesAsString=" + new String(bytes) +
                    '}';
        }
        else {
            return "Message{" +
                    "bytes=" + Arrays.toString(bytes) +
                    '}';
        }
    }
}
