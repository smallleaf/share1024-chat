package com.share1024.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.share1024.chat.protocol.IMDecoder;
import com.share1024.chat.protocol.IMEncoder;
import com.share1024.chat.protocol.IMMessage;
import com.share1024.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 专门处理消息和逻辑
 * Created by yesheng on 2017/3/7.
 */
public class MsgProcessor {

    //记录在线人数
    private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private IMDecoder decoder = new IMDecoder();
    private IMEncoder encoder = new IMEncoder();


    //自定义保存一些属性
    private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    private final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    private final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");

    /**
     * 登出,如果有用户退出就在容器中去掉该用户
     *
     * @param client
     */
    public void logout(Channel client) {
        if (null == client) {
            return;
        }
        for (Channel channel : onlineUsers) {
            IMMessage message = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(client) + "退出");
            String content = encoder.encode(message);
            channel.writeAndFlush(new TextWebSocketFrame(content));
        }
        onlineUsers.remove(client);
    }

    /**
     * 报消息发送到每一个客户端,发送到网页端
     */
    public void sendMsg(Channel client, IMMessage msg) {
        if (null == msg) {
            return;
        }

        if (IMP.LOGIN.getName().equals(msg.getCmd())) {
            //保存到自定义属性当中去
            client.attr(NICK_NAME).getAndSet(msg.getSender());
            client.attr(IP_ADDR).getAndSet(getIpAddr(client));

            onlineUsers.add(client);

            //扫描所有用户通知上线了
            for (Channel channel : onlineUsers) {
                if (channel != client) {
                    msg = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getNickName(client) + "上线");
                } else {
                    msg = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), "已经与服务器建立连接");
                }
                String content = encoder.encode(msg);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        } else {//其他命令操作

        }

    }

    public void sendMsg(Channel client, String msg) {
        sendMsg(client, decoder.decode(msg));
    }


    /**
     * 获得客户端的昵称
     *
     * @param client
     * @return
     */
    public String getNickName(Channel client) {
        return client.attr(NICK_NAME).get();
    }

    /**
     * 返回ip地址
     *
     * @param client
     * @return
     */
    public String getIpAddr(Channel client) {
        return client.remoteAddress().toString();
    }

    /**
     * 获得系统时间
     *
     * @return
     */
    public long sysTime() {
        return System.currentTimeMillis();
    }
}
