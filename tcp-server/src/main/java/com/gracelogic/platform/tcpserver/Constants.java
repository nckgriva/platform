package com.gracelogic.platform.tcpserver;

public class Constants {
    public static int MAX_MESSAGE_BYTE_SIZE = 512 * 1024;
    public static int MAX_INCORRECT_PACKAGES_TO_PROCESS = 10;
    public static int SOCKET_TIMEOUT = 300000;
    public static int HTTP_TIMEOUT = 5000;
    public static long USER_RECEIVE_MESSAGE_DELAY = 5000;

    public static final long INCOMING_MESSAGE_INTERVAL = 1000;
}
