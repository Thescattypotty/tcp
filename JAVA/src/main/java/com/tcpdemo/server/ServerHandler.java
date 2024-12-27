package com.tcpdemo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private long totalBytes = 0;
    private long expectedBytes = 1_000_000; // taille attendue

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connecté: {}", ctx.channel().remoteAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer("220 Welcome\r\n".getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        totalBytes += buf.readableBytes();
        buf.release();
        log.info("Reçu {} bytes sur {} attendus", totalBytes, expectedBytes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (totalBytes < expectedBytes) {
            log.warn("❌ Connexion fermée avec perte de données! Reçu {} bytes sur {} attendus ({} bytes perdus)", 
                    totalBytes, expectedBytes, expectedBytes - totalBytes);
        } else {
            log.info("✅ Toutes les données ont été reçues : {} bytes", totalBytes);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Erreur : ", cause);
        ctx.close();
    }
}
