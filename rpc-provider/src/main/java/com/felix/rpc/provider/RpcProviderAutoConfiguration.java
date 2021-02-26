package com.felix.rpc.provider;

import com.felix.rpc.common.RpcProperties;
import com.felix.rpc.provider.registry.RegistryFactory;
import com.felix.rpc.provider.registry.RegistryService;
import com.felix.rpc.provider.registry.RegistryType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description: 将RpcProperties配置类注入Spring容器
 * @author: Felix
 * @date: 2021/2/23 16:50
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider init() throws Exception {
        //获取注册中心类型
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        //获取注册中心实例
        RegistryService serviceRegistry = RegistryFactory.getInstance(rpcProperties.getRegistryAddr(), type);
        return new RpcProvider(rpcProperties.getServicePort(), serviceRegistry);
    }
}
