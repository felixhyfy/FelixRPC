package com.felix.rpc.provider.registry;

import com.felix.rpc.common.ServiceMeta;

/**
 * @description: Eureka 注册中心实现
 * @author: Felix
 * @date: 2021/2/24 21:39
 */
public class EurekaRegistryServiceImpl implements RegistryService {

    public EurekaRegistryServiceImpl(String registryAddr) {
        // TODO
    }

    @Override
    public void register(ServiceMeta serviceMeta) {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
