package com.felix.rpc.provider.facade;

import com.felix.rpc.provider.annotation.RpcService;

/**
 * @description: 接口实现类
 * @author: Felix
 * @date: 2021/2/25 15:33
 */
@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "1.0.0")
public class HelloFacadeImpl implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello" + name;
    }
}
