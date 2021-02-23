package com.felix.rpc.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: Rpc服务启动配置
 * @author: Felix
 * @date: 2021/2/23 16:41
 */
@Data
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {

    /**
     * 服务暴露的端口
     */
    private int servicePort;

    /**
     * 注册中心地址
     */
    private String registryAddr;

    /**
     * 注册中心类型
     */
    private String registryType;
}
