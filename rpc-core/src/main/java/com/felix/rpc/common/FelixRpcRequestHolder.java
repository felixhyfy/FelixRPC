package com.felix.rpc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description: RPC request 全局变量
 * @author: Felix
 * @date: 2021/2/23 18:24
 */
public class FelixRpcRequestHolder {

    public static final AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static final Map<Long, FelixRpcFuture<FelixRpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
