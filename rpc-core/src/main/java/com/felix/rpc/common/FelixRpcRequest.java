package com.felix.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: RPC请求类
 * @author: Felix
 * @date: 2021/2/23 18:16
 */
@Data
public class FelixRpcRequest implements Serializable {

    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
