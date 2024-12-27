package com.tcpdemo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
    private final String host;
    private final int port;
    private final int dataSize;

    public NettyClient(String host, int port, int dataSize) {
        this.host = host;
        this.port = port;
        this.dataSize = dataSize;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(new ClientHandler(dataSize));
                 }
             });

            ChannelFuture f = b.connect(host, port).sync();
            log.info("Connecté à {}:{}", host, port);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            log.error("Usage: NettyClient <host> <port> <size>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int size = Integer.parseInt(args[2]);
        new NettyClient(host, port, size).start();
    }
}
