package com.gracelogic.platform.tcpserver.service;

import com.gracelogic.platform.tcpserver.Constants;
import com.gracelogic.platform.tcpserver.TcpServerUtils;
import com.gracelogic.platform.tcpserver.dto.Client;
import com.gracelogic.platform.tcpserver.dto.Message;
import com.gracelogic.platform.tcpserver.dto.Package;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class TCPServerServiceImpl extends Thread implements TCPServerService {
    private static Log logger = LogFactory.getLog(TCPServerServiceImpl.class);

    @Autowired
    private TCPServerMessageProcessor TCPServerMessageProcessor;

    private boolean running = false;
    private int serverPort = 8001;
    private ServerSocket listenSocket = null;
    private LinkedList<Connection> connections = new LinkedList<Connection>();

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @PostConstruct
    public void startService() {
        if (!running) {
            super.start();
        }
    }

    @PreDestroy
    public void stopService() {
        running = false;
    }

    @Override
    public boolean addMessageToQueue(Client client, Message message) {
        boolean result = false;
        if (message != null) {
            if (running) {
                synchronized (connections) {
                    for (Connection connection : connections) {
                        if (client != null && connection.client.getId().equals(client.getId())) {
                            try {
                                connection.send(message.getBytes());
                                result = true;
                                logger.info(String.format("[%s] - sent message: '%s'", client.getId().toString(), message));
                            } catch (Exception e) {
                                logger.warn(String.format("[%s] - failed to send message", client.getId().toString()), e);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<Client> addBroadcastMessageToQueue(Message message) {
        if (!running) {
            return null;
        }

        LinkedList<Client> onlineMachines = new LinkedList<Client>();
        if (message != null) {
            synchronized (connections) {
                for (Connection connection : connections) {
                    if (connection.client != null) {
                        try {
                            connection.send(message.getBytes());
                            onlineMachines.add(connection.client);
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        return onlineMachines;
    }

    @Override
    public void disconnectClient(Client client) {
        if (!running) {
            return;
        }

        synchronized (connections) {
            for (Connection connection : connections) {
                if (connection.client != null && connection.client.getId().equals(client.getId())) {
                    connection.close();
                    break;
                }
            }
        }
    }

    @Override
    public boolean isStarted() {
        return running;
    }

    public class Connection extends Thread {
        private boolean active = true;
        public Socket sock = null;
        public DataInputStream in;
        public DataOutputStream out;
        private LinkedList<Package> packages = new LinkedList<Package>();
        private Client client = new Client();

        public Connection(Socket sock) {
            this.sock = sock;
        }

        public void init() throws IOException {
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            if (client != null) {
                TCPServerMessageProcessor.clientConnected(client);
            }
        }

        public void close() {
            active = false;
            if (client != null) {
                TCPServerMessageProcessor.clientDisconnected(client);
            }
            try {
                sock.close();
            } catch (IOException ex) {
                logger.fatal("Exception when closing a socket", ex);
            }
            onCloseConnection(this);
        }

        public void run() {
            try {
                while (active && !Thread.currentThread().isInterrupted()) {
                    byte[] stream = TcpServerUtils.readBytes(in);

                    if (stream == null) break;


                    if (!packages.isEmpty()) {
                        int totalLen = stream.length;

                        logger.debug("Current invalid packages to glue processing: " + packages.size());
                        for (Package aPackage : packages) {
                            totalLen += aPackage.getData().length;
                        }
                        if (totalLen > Constants.MAX_MESSAGE_BYTE_SIZE) {
                            logger.warn(
                                    "Message with glue packages is too big size: " + totalLen + " bytes ; max size: " + Constants.MAX_MESSAGE_BYTE_SIZE + " bytes.");
                            packages.clear();
                            return;          //close connection
                        }
                        logger.debug("Full size of invalid packages for glue processing: " + totalLen);
                        byte[] newStream = new byte[totalLen];
                        int lastPos = 0;

                        for (Package pack : packages) {
                            System.arraycopy(pack.getData(), 0, newStream, lastPos, pack.getData().length);
                            lastPos += pack.getData().length;
                        }
                        //1 - источник
                        //2 - начиная откуда надо читать в источнике
                        //3 - новый массив
                        //4 - начиная откуда надо писать в новом массиве
                        //5 - сколько надо писать в новый массив
                        System.arraycopy(stream, 0, newStream, lastPos, stream.length);

                        stream = newStream;

                        packages.clear();

                    }

                    int processedBytes = 0;
                    int offset = processedBytes;
                    boolean error = false;

                    do {
                        try {
                            int newProcessedBytes = TCPServerMessageProcessor.processMessage(client, stream, offset);
                            if (processedBytes == newProcessedBytes) {
                                error = true;
                                break;
                            } else {
                                processedBytes = newProcessedBytes;
                            }

                            offset = processedBytes;
                        } catch (Exception e) {
                            error = true;
                            break;
                        }
                    }
                    while (processedBytes < stream.length);

                    if (error) {
                        packages.clear();
                        stream = TcpServerUtils.clipArray(stream, offset);
                        packages.addFirst(new Package(stream));
                    }


                    try {
                        sleep(Constants.INCOMING_MESSAGE_INTERVAL);
                    } catch (InterruptedException e) {
                        logger.fatal("Thread interruption exception", e);
                    }
                }
            } catch (IOException ex) {
                logger.info("Error reading from " + sock.getInetAddress() + " " + ex.toString() + ";");
            }
            close();
        }

        synchronized public void send(byte[] msg) throws IOException {
            if (msg.length > 0) {
                out.write(msg, 0, msg.length);
                out.flush();
            }
        }
    }

    private void updateConnectionStatus() {
        connections.notifyAll();
    }

    public void run() {
        running = true;

        logger.info("Binding to port: " + serverPort + ";");
        bind(serverPort);
        listen();
    }

    public int bind(int port) {
        try {
            listenSocket = new ServerSocket(port);
        } catch (IOException ex) {
            logger.error("Error binding to port " + port + " " + ex.toString() + ";");
            return -1;
        }
        return 0;
    }

    protected void onCloseConnection(Connection con) {
        synchronized (connections) {
            connections.remove(con);
            updateConnectionStatus();
        }
        logger.debug("Connection closed: " + con.sock.getInetAddress() + ":" + con.sock
                .getPort() + ";");
    }

    protected void listen() {
        while (running) {
            try {
                Socket socket = listenSocket.accept();
                socket.setSoLinger(true, 1);
                socket.setSoTimeout(Constants.SOCKET_TIMEOUT);

                logger.debug("New connection: " + socket.getInetAddress() + ":" + socket.getPort() + ";");
                Connection con = new Connection(socket);
                con.init();

                synchronized (connections) {
                    connections.addLast(con);
                    updateConnectionStatus();
                }

                con.start();

            } catch (IOException ex) {
                logger.error("Exception when listening.", ex);
            }
        }
    }
}
