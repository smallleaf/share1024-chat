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
        } else if (IMP.CHAT.getName().equals(msg.getCmd())) {//聊天
            for (Channel channel : onlineUsers) {
                if (channel != client) {
                    msg.setSender(getNickName(client));
                } else {
                    msg.setSender("you");
                }
                msg.setTime(sysTime());
                String content = encoder.encode(msg);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        } else if (IMP.FLOWER.getName().equals(msg.getCmd())) {
            //非正常的情况下，就频繁刷花，导致整个屏幕一直是鲜花特效
            //影响聊天效果
            //这时候，我们就要加上一个限制，规定1分钟之内，每个人只能刷一次鲜花
            JSONObject attrs = getAttrs(client);

            //如果为空，就表示这个人从来没有送过鲜花
            //处女送
            if (null != attrs) {
                //就开始判断上次送花时间
                long lastFlowerTime = attrs.getLongValue("lastFlowerTime");
                int secends = 60;//60秒之内不能重复送花
                long sub = sysTime() - lastFlowerTime;
                if (sub < 1000 * secends) {
                    msg.setSender("you");
                    msg.setCmd(IMP.SYSTEM.getName());
                    msg.setOnline(onlineUsers.size());
                    msg.setContent("您送鲜花太频繁,请" + (secends - Math.round(sub / 1000)) + "秒后再试");

                    String content = encoder.encode(msg);
                    client.writeAndFlush(new TextWebSocketFrame(content));

                    return;
                }
            }
            //正常的送花流程
            for (Channel channel : onlineUsers) {
                if (channel != client) {
                    msg.setSender(getNickName(client));
                    msg.setContent(getNickName(client) + "送来一波鲜花");
                } else {
                    msg.setSender("you");
                    msg.setContent("你给大家送了一波鲜花");
                    setAttrs(client, "lastFlowerTime", sysTime());
                }
                msg.setTime(sysTime());
                String content = encoder.encode(msg);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }

    }

    /**
     * 获得扩展属性
     *
     * @param client
     * @return
     */
    public JSONObject getAttrs(Channel client) {
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 往扩展属性添加自定义key
     *
     * @param client
     * @param key
     * @param value
     */
    public void setAttrs(Channel client, String key, Object value) {
        JSONObject extendAtrrs = client.attr(ATTRS).get();
        if (null == extendAtrrs) {
            extendAtrrs = new JSONObject();
        }
        extendAtrrs.put(key, value);
        client.attr(ATTRS).set(extendAtrrs);
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
