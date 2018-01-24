package com.gracelogic.platform.tcpserver.service;

import com.gracelogic.platform.tcpserver.dto.Client;
import com.gracelogic.platform.tcpserver.dto.Message;

import java.util.List;

public interface TCPServerService {
    boolean addMessageToQueue(Client client, Message message);

    List<Client> addBroadcastMessageToQueue(Message message);

    void disconnectClient(Client controlledMachine);

    boolean isStarted();
}
