package com.gracelogic.platform.tcpserver.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 23.03.12
 * Time: 15:22
 */
public class Message {
    private byte [] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Message(byte[] bytes) {

        this.bytes = bytes;
    }
}
