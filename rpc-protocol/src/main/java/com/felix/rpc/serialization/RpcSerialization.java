package com.felix.rpc.serialization;

import java.io.IOException;

/**
 * @description: 通用序列化接口，所有序列化算法都必须实现
 * @author: Felix
 * @date: 2021/2/23 20:29
 */
public interface RpcSerialization {
    /**
     * 序列化
     *
     * @param obj 对象数据
     * @return 二进制数据流
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data  二进制数据流
     * @param clazz 序列化算法类
     * @return 对象数据
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;

}
