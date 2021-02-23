package com.felix.rpc.common;

import lombok.Data;

/**
 * @description: 服务元数据
 * @author: Felix
 * @date: 2021/2/23 18:03
 */
@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

}
