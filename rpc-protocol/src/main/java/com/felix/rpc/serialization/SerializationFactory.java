package com.felix.rpc.serialization;

/**
 * @description: 序列化算法工厂
 * @author: Felix
 * @date: 2021/2/24 11:03
 */
public class SerializationFactory {

    public static RpcSerialization getRpcSerialization(byte serializationType) {
        final SerializationTypeEnum typeEnum = SerializationTypeEnum.findByType(serializationType);

        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialization();
            case JSON:
                return new JsonSerialization();
            default:
                throw new IllegalArgumentException("serialization type is illegal, " + serializationType);
        }
    }
}
