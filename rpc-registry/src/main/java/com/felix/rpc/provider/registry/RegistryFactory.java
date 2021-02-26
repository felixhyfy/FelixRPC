package com.felix.rpc.provider.registry;

/**
 * @description: 注册中心工厂类
 * @author: Felix
 * @date: 2021/2/24 21:31
 */
public class RegistryFactory {

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddr, RegistryType type) throws Exception {
        if (null == registryService) {
            synchronized (RegistryFactory.class) {
                if (null == registryService) {
                    switch (type) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryServiceImpl(registryAddr);
                            break;
                        case EUREKA:
                            registryService = new EurekaRegistryServiceImpl(registryAddr);
                            break;
                        default:
                            throw new IllegalArgumentException("illegal registry service type: " + type);
                    }
                }
            }
        }
        return registryService;
    }
}
