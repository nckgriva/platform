package com.gracelogic.platform.tcpserver.service;

import com.gracelogic.platform.tcpserver.dto.Client;

public interface TCPServerMessageProcessor {
    void clientConnected(Client client);

    void clientDisconnected(Client client);

    //@return - количество обработанных байт начиная с нулевого + offset
    //@msg - данные для обработки
    //@offset - начиная с какого байта нужно обрабатывать
    int processMessage(Client client, byte[] msg, int offset) throws Exception;
}
