package com.tcpdemo.server;

import java.io.*;
import java.net.*;

public class TCPServer {
    private final int port;
    private ServerSocket serverSocket;

    public TCPServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            OutputStream out = clientSocket.getOutputStream();
            InputStream in = clientSocket.getInputStream()
        ) {
            String welcome = "220 Welcome\r\n";
            out.write(welcome.getBytes());
            out.flush();

            byte[] buffer = new byte[4096];
            int totalBytesRead = 0;
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytesRead += bytesRead;
            }

            System.out.println("Total bytes received: " + totalBytesRead);
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: TCPServer <port>");
            System.exit(1);
        }
        new TCPServer(Integer.parseInt(args[0])).start();
    }
}
