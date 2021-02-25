package com.felix.rpc.provider.facade;

/**
 * @description: RPC服务对外暴露的接口
 * @author: Felix
 * @date: 2021/2/25 14:39
 */
public interface HelloFacade {
    /**
     * hello
     *
     * @param name
     * @return
     */
    String hello(String name);
}
