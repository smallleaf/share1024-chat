package com.share1024.chat.protocol;

/**
 * Created by yesheng on 2017/3/7.
 */
public enum IMP {
    /**
     * 系统命令
     */
    SYSTEM("SYSTEM"),
    /**
     * 登录命令
     */
    LOGIN("LOGIN"),
    /**
     * 登出命令
     */
    LOGOUT("LOGIN"),
    /*聊天命令*/
    CHAT("CHAT"),
    /*鲜花命令*/
    FLOWER("FLOWER");

    private String name;

    IMP(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 判断是否是支持该自定义协议类型的
     *
     * @param msg
     * @return
     */
    public static boolean isIMP(String msg) {
        return msg.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
    }
}
