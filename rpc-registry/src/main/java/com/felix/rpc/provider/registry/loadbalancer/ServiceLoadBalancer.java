package com.felix.rpc.provider.registry.loadbalancer;

import java.util.List;

/**
 * @description: 通用负载均衡算法接口
 * @author: Felix
 * @date: 2021/2/24 19:29
 */
public interface ServiceLoadBalancer<T> {
    /**
     * 根据负载均衡算法选择服务节点
     *
     * @param servers
     * @param hashCode
     * @return
     */
    T select(List<T> servers, int hashCode);
}
