package com.felix.rpc.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @description: RPC服务提供者 主启动类
 * @author: Felix
 * @date: 2021/2/25 15:33
 */
@EnableConfigurationProperties
@SpringBootApplication
public class RpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderApplication.class, args);
    }
}
