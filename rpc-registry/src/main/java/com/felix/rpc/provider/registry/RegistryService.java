package com.felix.rpc.provider.registry;

import com.felix.rpc.common.ServiceMeta;

/**
 * @description: 注册中心基本操作接口
 * @author: Felix
 * @date: 2021/2/24 18:18
 */
public interface RegistryService {

    /**
     * 服务注册接口
     *
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务注销接口
     *
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现接口
     *
     * @param serviceName
     * @param invokerHashCode
     * @return
     * @throws Exception
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 注册中心销毁接口
     *
     * @throws Exception
     */
    void destroy() throws Exception;

}
