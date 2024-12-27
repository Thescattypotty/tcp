package com.tcpdemo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadClientHandler extends ChannelInboundHandlerAdapter {
    private final int dataSize;
    private boolean welcomeReceived = false;

    public BadClientHandler(int dataSize) {
        this.dataSize = dataSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        if (!welcomeReceived) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            log.info("Reçu: {}", new String(bytes));
            welcomeReceived = true;
            
            ByteBuf data = Unpooled.buffer(dataSize);
            data.writeZero(dataSize);
            ctx.writeAndFlush(data);
            log.info("Envoyé {} bytes", dataSize);
            
            ctx.close();
        }
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Erreur : ", cause);
        ctx.close();
    }
}
