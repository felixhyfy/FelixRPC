package com.felix.rpc.common;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: RPC异步Future返回类
 * @author: Felix
 * @date: 2021/2/23 18:20
 */
@Data
@AllArgsConstructor
public class FelixRpcFuture<T> {

    private Promise<T> promise;
    private long timeout;

}
