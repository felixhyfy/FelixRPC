package com.felix.rpc.handler;

import java.util.concurrent.*;

/**
 * @description: 自定义业务线程池执行RPC请求调用
 * @author: Felix
 * @date: 2021/2/24 15:51
 */
public class RpcRequestProcessor {

    private static ThreadPoolExecutor threadPoolExecutor;

    /**
     * 提交Request
     *
     * @param task
     */
    public static void submitRequest(Runnable task) {
        // DCL双重校验锁模式，安全且在多线程情况下能保持高性能
        if (threadPoolExecutor == null) {
            synchronized (RpcRequestProcessor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(10,
                            10,
                            60L,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(10000));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }
}
