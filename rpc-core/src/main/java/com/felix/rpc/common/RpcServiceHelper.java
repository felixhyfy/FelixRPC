package com.felix.rpc.common;

/**
 * @description: 构造serviceKey
 * @author: Felix
 * @date: 2021/2/23 18:14
 */
public class RpcServiceHelper {

    public static String buildServiceKey(String serviceName, String serviceVersion) {
        //返回格式为： serviceName#serviceVersion
        return String.join("#", serviceName, serviceVersion);
    }
}
