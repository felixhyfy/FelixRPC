package com.felix.rpc.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description: Hessian 序列化算法实现
 * @author: Felix
 * @date: 2021/2/23 20:37
 */
@Slf4j
@Component
public class HessianSerialization implements RpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new NullPointerException("对象数据为空");
        }
        byte[] results;

        //创建 Hessian序列化输出对象
        HessianSerializerOutput output;
        //创建输出流，转换为字节数组
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            output = new HessianSerializerOutput(os);
            output.writeObject(obj);
            //刷新
            output.flush();
            //转换为字节数组
            results = os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return results;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        if (data == null) {
            throw new NullPointerException("二进制字节流为空");
        }
        T result;

        //创建输入流，将字节数组反序列化
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            HessianSerializerInput input = new HessianSerializerInput(is);
            result = (T) input.readObject(clazz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return result;
    }
}
