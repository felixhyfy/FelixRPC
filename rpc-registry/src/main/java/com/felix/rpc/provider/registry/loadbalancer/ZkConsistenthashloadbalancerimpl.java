package com.felix.rpc.provider.registry.loadbalancer;

import com.felix.rpc.common.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description: Zookeeper基于一致性hash的负载均衡算法实现
 * @author: Felix
 * @date: 2021/2/24 19:32
 */
public class ZkConsistenthashloadbalancerimpl implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {

    /**
     * 虚拟节点数目
     */
    private final static int VIRTUAL_NODE_SIZE = 10;
    /**
     * 虚拟节点分隔符
     */
    private final static String VIRTUAL_NODE_SPLIT = "#";


    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode) {
        final TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = makeConsistentHashRing(servers);
        return allocateNode(ring, hashCode);
    }

    private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring,
                                                      int hashCode) {
        //通过ceilingEntry()方法找出大于或者等于客户端hashCode的第一个节点
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> entry = ring.ceilingEntry(hashCode);
        if (entry == null) {
            //如果没有找到，直接取TreeMap中的第一个节点
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 构造哈希环
     *
     * @param servers 服务节点
     * @return
     */
    private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();
        for (ServiceInstance<ServiceMeta> instance : servers) {
            //构造虚拟节点
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                //虚拟节点格式：key#i
                ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
            }
        }
        return ring;
    }

    /**
     * 构造服务实例的key
     *
     * @param instance 服务实例
     * @return
     */
    private String buildServiceInstanceKey(ServiceInstance<ServiceMeta> instance) {
        ServiceMeta payload = instance.getPayload();
        //根据 IP + Port 构造key: IP:Port
        return String.join(":", payload.getServiceAddr(), String.valueOf(payload.getServicePort()));
    }
}
