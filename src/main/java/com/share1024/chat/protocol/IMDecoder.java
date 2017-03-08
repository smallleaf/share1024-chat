package com.share1024.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javassist.bytecode.stackmap.BasicBlock;
import org.msgpack.MessagePack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IM协议编码器  服务端往客户端输出要编码
 * Created by yesheng on 2017/3/7.
 */
public class IMDecoder extends ByteToMessageDecoder {

    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        try {
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array, in.readerIndex(), length);
            if (!(null == content || "".equals(content.trim()))) {
                if (!IMP.isIMP(content)) {
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }
            in.getBytes(in.readerIndex(), array, 0, length);
            out.add(new MessagePack().read(array, IMMessage.class));
            in.clear();
        } catch (Exception e) {
            ctx.channel().pipeline().remove(this);
        }
    }

    public IMMessage decode(String msg) {
        if (null == msg || "".equals(msg.trim())) {
            return null;
        }
        try {
            Matcher matcher = pattern.matcher(msg);

            String header = "";//消息的头部
            String content = "";//消息体
            if (matcher.matches()) {
                header = matcher.group(1);
                content = matcher.group(3);
            }

            String[] headers = header.split("\\]\\[");

            //获取命令发送时间
            long time = Long.parseLong(headers[1]);
            //获取昵称
            String nickName = headers[2];
            nickName = nickName.length() > 10 ? nickName.substring(0, 9) : nickName;

            String cmd = headers[0];
            //封装IMMessage对象
            if (IMP.LOGIN.getName().equals(cmd) ||
                    IMP.LOGOUT.getName().equals(cmd) ||
                    IMP.FLOWER.getName().equals(cmd)) {
                return new IMMessage(cmd, time, nickName);
            } else if (IMP.CHAT.getName().equals(cmd) ||
                    IMP.SYSTEM.getName().equals(cmd)) {
                return new IMMessage(cmd, time, nickName, content);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
