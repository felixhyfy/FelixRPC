package com.felix.rpc.protocol;

import lombok.Getter;

/**
 * @description: 消息状态枚举
 * @author: Felix
 * @date: 2021/2/23 20:13
 */
public enum MsgStatus {
    // 成功
    SUCCESS(0),
    // 失败
    FAIL(1);

    @Getter
    private final int code;

    MsgStatus(int code) {
        this.code = code;
    }
}
