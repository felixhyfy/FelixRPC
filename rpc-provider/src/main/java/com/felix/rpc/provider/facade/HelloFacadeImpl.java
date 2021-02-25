package com.felix.rpc.provider.facade;

/**
 * @description: 接口实现类
 * @author: Felix
 * @date: 2021/2/25 15:33
 */
public class HelloFacadeImpl implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello" + name;
    }
}
