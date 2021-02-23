package com.felix.rpc.protocol;

import lombok.Data;

/**
 * @description: 自定义rpc协议
 * @author: Felix
 * @date: 2021/2/23 20:08
 */
@Data
public class FelixRpcProtocol<T> {

    /**
     * 协议头：存放各种信息
     */
    private MsgHeader msgHeader;

    /**
     * 协议体：存放数据
     */
    private T body;
}
