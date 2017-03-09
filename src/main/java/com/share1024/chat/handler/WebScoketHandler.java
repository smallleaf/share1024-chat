package com.share1024.chat.handler;

import com.share1024.chat.processor.MsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yesheng on 2017/3/7.
 */
public class WebScoketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Logger logger = LoggerFactory.getLogger(WebScoketHandler.class);
    private MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        logger.info("====" + msg.text());
        processor.sendMsg(ctx.channel(), msg.text());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // super.channelActive(ctx);
        // logger.info("client:{},上线",ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // logger.info("client:{},掉线",ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //  logger.info("client:{},异常",ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        //  logger.info("client:{},加入",ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        //  logger.info("client:{},离开",ctx.channel().remoteAddress());
        processor.logout(ctx.channel());
    }
}
