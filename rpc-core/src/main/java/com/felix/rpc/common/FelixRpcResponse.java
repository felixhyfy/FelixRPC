package com.felix.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: RPC响应类
 * @author: Felix
 * @date: 2021/2/23 18:18
 */
@Data
public class FelixRpcResponse implements Serializable {
    private Object data;
    private String msg;
}
