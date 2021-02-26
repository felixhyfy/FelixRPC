package com.felix.rpc.consumer;

import com.felix.rpc.provider.registry.RegistryFactory;
import com.felix.rpc.provider.registry.RegistryService;
import com.felix.rpc.provider.registry.RegistryType;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @description: 自定义Bean返回RpcReference
 * @author: Felix
 * @date: 2021/2/24 21:24
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    /**
     * Spring 的 FactoryBean 接口可以帮助我们实现自定义的 Bean，
     * FactoryBean 是一种特种的工厂 Bean，通过 getObject() 方法返回对象，而并不是 FactoryBean 本身。
     */
    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;

    private String registryAddr;

    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {
        /**
         * 此处需要实现动态代理对象，并通过代理对象完成 RPC 调用。
         * 对于使用者来说只是通过 @RpcReference 订阅了服务，并不感知底层调用的细节。
         * 对于如何实现 RPC 通信、服务寻址等，都是在动态代理类中完成的
         */
        //获得注册中心服务
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddr, RegistryType.valueOf(registryType));
        //注入动态代理类，之后直接操作这个动态代理类
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcInvokerProxy(serviceVersion, timeout, registryService));
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
