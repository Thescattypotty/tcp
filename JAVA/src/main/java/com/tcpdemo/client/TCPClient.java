package com.tcpdemo.client;

import java.io.*;
import java.net.*;

public class TCPClient {
    private final String host;
    private final int port;
    private Socket socket;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        
        try (
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream()
        ) {
            byte[] welcome = new byte[256];
            int bytesRead = in.read(welcome);
            System.out.println("Received: " + new String(welcome, 0, bytesRead));

            byte[] data = new byte[1_000_000];
            out.write(data);
            System.out.println("Bytes written: " + data.length);

            socket.shutdownOutput();

            while (in.read(welcome) != -1) {
                // Attendre que le serveur ferme la connexion
            }

        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: TCPClient <host> <port> <size>");
            System.exit(1);
        }

        try {
            TCPClient client = new TCPClient(args[0], Integer.parseInt(args[1]));
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
