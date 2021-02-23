package com.felix.rpc.provider;

import com.felix.rpc.common.RpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

    //todo: init方法
}
