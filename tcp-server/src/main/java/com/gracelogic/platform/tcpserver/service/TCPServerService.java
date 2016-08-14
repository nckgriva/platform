package com.gracelogic.platform.tcpserver.service;

import com.gracelogic.platform.tcpserver.dto.Client;
import com.gracelogic.platform.tcpserver.dto.Message;

import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 23.03.12
 * Time: 13:09
 */
public interface TCPServerService {
    void addMessageToQueue(Client client, String message);

    List<Client> addBroadcastMessageToQueue(Message message);

    void disconnectClient(Client controlledMachine);

    boolean isStarted();
}
