package com.share1024.chat.handler;

import com.share1024.chat.protocol.IMEncoder;
import com.share1024.chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yesheng on 2017/3/7.
 */
public class ImpHandler extends SimpleChannelInboundHandler<IMMessage> {

    private IMEncoder encoder = new IMEncoder();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMMessage imMessage) throws Exception {
        System.out.println(encoder.encode(imMessage));
    }
}
