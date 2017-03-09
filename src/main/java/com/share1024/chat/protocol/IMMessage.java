package com.share1024.chat.protocol;

import org.msgpack.annotation.Message;

/**
 * 自定义协议消息体
 * Created by yesheng on 2017/3/7.
 */
@Message
public class IMMessage {

    /**
     * Ip地址及端口
     */
    private String addr;

    /**
     * 命令
     */
    private String cmd;
    /**
     * 发送时间
     */
    private long time;
    /**
     * 消息接受者
     */
    private String receiver;
    /**
     * 消息发送者
     */
    private String sender;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 在线人数
     */
    private int online;


    public IMMessage() {

    }

    public IMMessage(String cmd, long time, int online, String content) {
        this.cmd = cmd;
        this.time = time;
        this.content = content;
        this.online = online;
    }

    public IMMessage(String cmd, long time, String sender) {
        this.cmd = cmd;
        this.time = time;
        this.sender = sender;
    }

    public IMMessage(String cmd, long time, String sender, String content) {
        this.cmd = cmd;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public IMMessage(String cmd, long time, String sender, int online, String content) {
        this.cmd = cmd;
        this.time = time;
        this.sender = sender;
        this.content = content;
        this.online = online;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "addr='" + addr + '\'' +
                ", cmd='" + cmd + '\'' +
                ", time=" + time +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", online=" + online +
                '}';
    }
}
