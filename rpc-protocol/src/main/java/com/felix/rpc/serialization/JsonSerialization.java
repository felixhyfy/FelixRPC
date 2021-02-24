package com.felix.rpc.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * @description: Json 序列化算法
 * @author: Felix
 * @date: 2021/2/24 11:12
 */
public class JsonSerialization implements RpcSerialization {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = generateMapper(JsonInclude.Include.ALWAYS);
    }

    private static ObjectMapper generateMapper(JsonInclude.Include include) {
        ObjectMapper customMapper = new ObjectMapper();

        customMapper.setSerializationInclusion(include);
        customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return customMapper;
    }

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        //如果obj是String，则直接返回其字节数组，否则使用MAPPER进行序列化
        return obj instanceof String ? ((String) obj).getBytes() : MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        //将data转换为String，传入对应的类型
        return MAPPER.readValue(Arrays.toString(data), clazz);
    }
}
