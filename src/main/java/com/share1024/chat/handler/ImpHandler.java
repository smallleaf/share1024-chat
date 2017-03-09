package com.share1024.chat.handler;

import com.share1024.chat.processor.MsgProcessor;
import com.share1024.chat.protocol.IMEncoder;
import com.share1024.chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yesheng on 2017/3/7.
 */
public class ImpHandler extends SimpleChannelInboundHandler<IMMessage> {


    private MsgProcessor processor = new MsgProcessor();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage message) throws Exception {
        System.out.println("==================");
        processor.sendMsg(ctx.channel(), message);
    }
}
