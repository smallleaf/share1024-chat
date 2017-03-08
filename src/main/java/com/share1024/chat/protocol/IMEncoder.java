package com.share1024.chat.protocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.msgpack.MessagePack;
import sun.misc.resources.Messages_es;
import sun.nio.cs.ext.MS874;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IM协议解码器 客户端过来要解码
 * Created by yesheng on 2017/3/7.
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {


    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    /**
     * 把Immessage'对象转化成imp对象
     *
     * @param message
     * @return
     */
    public String encode(IMMessage message) {

        if (null == message) {
            return "";
        }
        String prex = "[" + message.getCmd() + "][" + message.getTime() + "]";
        if (IMP.LOGIN.getName().equals(message.getCmd()) ||
                IMP.LOGOUT.getName().equals(message.getCmd()) ||
                IMP.CHAT.getName().equals(message.getCmd()) ||
                IMP.FLOWER.getName().equals(message.getCmd())) {
            prex += ("[" + message.getSender() + "]");
        } else if (IMP.SYSTEM.getName().equals(message.getCmd())) {
            prex += ("[" + message.getOnline() + "]");
        }

        if (!(null == message.getContent() || "".equals(message.getContent().trim()))) {
            prex += (" - " + message.getContent());
        }
        return prex;
    }
}
