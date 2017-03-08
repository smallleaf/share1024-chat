package com.share1024.chat.server;

import com.share1024.chat.handler.HttpHandler;
import com.share1024.chat.handler.ImpHandler;
import com.share1024.chat.handler.WebScoketHandler;
import com.share1024.chat.protocol.IMDecoder;
import com.share1024.chat.protocol.IMEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by yesheng on 2017/3/6.
 */
public class ChatServer {
    private Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public void run(String host, int port) throws InterruptedException {

        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //  1024个线程
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //============= 自定义协议 =================
                            pipeline.addLast(new IMEncoder());
                            pipeline.addLast(new IMDecoder());
                            pipeline.addLast(new ImpHandler());
//                                //对http协议的支持
//                                //======== 对HTTP协议的支持  ==========
//                                //Http请求解码器
//                                pipeline.addLast(new HttpServerCodec());
//                                //主要就是将一个http请求或者响应变成一个FullHttpRequest对象
//                                pipeline.addLast(new HttpObjectAggregator(64 * 1024));
//                                //这个是用来处理文件流
//                                pipeline.addLast(new ChunkedWriteHandler());
//                                pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
//                                //处理HTTP请求的业务逻辑
//                                pipeline.addLast(new HttpHandler());
//
//                                //================= 对websocket支持===============
//                                //别名 以im开头的都用websocket来解析
//                                pipeline.addLast(new WebScoketHandler());
                        }
                    });
            ChannelFuture ch = bootstrap.bind(port).sync();
            logger.info("========服务器已经启动:ip:{},端口:{}=================================", host, port);
            ch.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().run("localhost", 8080);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
