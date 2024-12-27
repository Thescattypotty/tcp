package com.tcpdemo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final int dataSize;
    private boolean welcomeReceived = false;

    public ClientHandler(int dataSize) {
        this.dataSize = dataSize;
    }

    private void checkPendingData(ChannelHandlerContext ctx) {
        if (ctx.channel() instanceof NioSocketChannel) {
            NioSocketChannel ch = (NioSocketChannel) ctx.channel();
            long pendingBytes = ch.unsafe().outboundBuffer().totalPendingWriteBytes();
            log.info("Données en attente d'envoi : {} bytes", pendingBytes);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        checkPendingData(ctx);
        if (!welcomeReceived) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            log.info("Reçu: {}", new String(bytes));
            welcomeReceived = true;
            
            ByteBuf data = Unpooled.buffer(dataSize);
            data.writeZero(dataSize);
            
            checkPendingData(ctx);
            
            ctx.writeAndFlush(data).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("Envoyé {} bytes", dataSize);
                    checkPendingData(ctx);
                    ctx.executor().schedule(() -> {
                        checkPendingData(ctx);
                        ctx.close();
                    }, 1000, java.util.concurrent.TimeUnit.MILLISECONDS);
                } else {
                    log.error("Erreur lors de l'envoi des données", future.cause());
                    ctx.close();
                }
            });
        }
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Erreur : ", cause);
        ctx.close();
    }
}
