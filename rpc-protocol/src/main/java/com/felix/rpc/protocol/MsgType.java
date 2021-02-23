package com.felix.rpc.protocol;

import lombok.Getter;

/**
 * @description: 消息类型枚举
 * @author: Felix
 * @date: 2021/2/23 20:14
 */
public enum MsgType {

    //请求消息
    REQUEST(1),
    //响应消息
    RESPONSE(2),
    //心跳检测消息
    HEARTBEAT(3);

    @Getter
    private final int type;

    MsgType(int type) {
        this.type = type;
    }

    public static MsgType findByType(int type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
