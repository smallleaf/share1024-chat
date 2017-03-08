package com.share1024.chat.client;


import com.google.common.base.Equivalence;
import com.share1024.chat.protocol.IMMessage;
import com.share1024.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.SystemPropertyUtil;

import java.util.Scanner;

/**
 * Created by yesheng on 2017/3/8.
 */
public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private String nickName;

    private ChannelHandlerContext ctx;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //登录
        IMMessage message = new IMMessage(IMP.LOGIN.getName(), System.currentTimeMillis(), nickName);
        sendMsg(message);
        System.out.println("成功连接至服务器,已执行登录动作");
        session();
    }

    /**
     * 从控制来输入消息
     */
    private void session() {
        new Thread() {

            public void run() {
                System.out.println(nickName + ",你好,请在控制台输入消息内容");
                IMMessage message = null;
                Scanner scanner = new Scanner(System.in);
                do {
                    String content = scanner.next();
                    if ("exit".equals(content.trim())) {
                        message = new IMMessage(IMP.LOGOUT.getName(), System.currentTimeMillis(), nickName);
                    } else {
                        message = new IMMessage(IMP.CHAT.getName(), System.currentTimeMillis(), nickName, content);
                    }
                } while (sendMsg(message));
                scanner.close();
            }
        }.start();
    }


    private boolean sendMsg(IMMessage msg) {
        ctx.channel().writeAndFlush(msg);
        System.out.println(msg.toString());
        System.out.println("消息已经发送至服务器,请输入");
        return msg.getCmd().equals(IMP.LOGOUT) ? false : true;
    }
}
