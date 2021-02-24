package com.felix.rpc.serialization;

import lombok.Getter;

/**
 * @description: 序列化算法枚举
 * @author: Felix
 * @date: 2021/2/24 11:03
 */
public enum SerializationTypeEnum {
    HESSIAN(0x10),
    JSON(0x20);

    @Getter
    private final int type;

    SerializationTypeEnum(int type) {
        this.type = type;
    }

    public static SerializationTypeEnum findByType(byte serializationType) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == serializationType) {
                return typeEnum;
            }
        }
        //默认为Hessian算法实现
        return HESSIAN;
    }
}
