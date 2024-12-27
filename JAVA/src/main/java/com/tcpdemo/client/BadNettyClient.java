package com.tcpdemo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadNettyClient {
    private final String host;
    private final int port;
    private final int dataSize;

    public BadNettyClient(String host, int port, int dataSize) {
        this.host = host;
        this.port = port;
        this.dataSize = dataSize;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_LINGER, 0)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new BadClientHandler(dataSize));
                    }
                });

            ChannelFuture f = b.connect(host, port).sync();
            Thread.sleep(50);
            f.channel().close().sync();
            
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: BadNettyClient <host> <port> <size>");
            System.exit(1);
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int size = Integer.parseInt(args[2]);
        
        new BadNettyClient(host, port, size).start();
    }
}
