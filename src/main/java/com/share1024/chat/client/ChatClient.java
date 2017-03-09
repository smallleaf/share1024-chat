package com.share1024.chat.client;

import com.share1024.chat.protocol.IMDecoder;
import com.share1024.chat.protocol.IMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yesheng on 2017/3/8.
 */
public class ChatClient {

    private ChatClientHandler chatClientHandler;

    public ChatClient(String nickName) {
        chatClientHandler = new ChatClientHandler(nickName);
    }

    public void run(String host, int port) throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new IMDecoder());
                            ch.pipeline().addLast(new IMEncoder());
                            ch.pipeline().addLast(chatClientHandler);

                        }
                    });
            ChannelFuture cf = bootstrap.connect(host, port).sync();
            cf.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new ChatClient("yesheng1").run("localhost", 8080);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
