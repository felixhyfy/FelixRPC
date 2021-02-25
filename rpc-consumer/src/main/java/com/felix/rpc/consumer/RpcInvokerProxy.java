package com.felix.rpc.consumer;

import com.felix.rpc.common.FelixRpcFuture;
import com.felix.rpc.common.FelixRpcRequest;
import com.felix.rpc.common.FelixRpcRequestHolder;
import com.felix.rpc.common.FelixRpcResponse;
import com.felix.rpc.protocol.*;
import com.felix.rpc.provider.registry.RegistryService;
import com.felix.rpc.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @description: RPC调用动态代理类
 * @author: Felix
 * @date: 2021/2/25 12:13
 */
public class RpcInvokerProxy implements InvocationHandler {

    /*private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;

    public RpcInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        */
    /**
     * 1、构造RPC协议对象
     * 2、发起RPC远程调用
     * 3、等待RPC调用执行结果
     *//*
        FelixRpcProtocol<FelixRpcRequest> protocol = new FelixRpcProtocol<>();
        //构造协议头
        MsgHeader header = new MsgHeader();
        long requestId = FelixRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        FelixRpcRequest request = new FelixRpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        protocol.setBody(request);

        RpcConsumer rpcConsumer = new RpcConsumer();
        //创建异步任务对象
        FelixRpcFuture<FelixRpcResponse> future = new FelixRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        //将该异步任务的requestId放入REQUEST_MAP中
        FelixRpcRequestHolder.REQUEST_MAP.put(requestId, future);
        //发送请求
        rpcConsumer.sendRequest(protocol, this.registryService);

        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();

    }*/

    private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;

    public RpcInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        FelixRpcProtocol<FelixRpcRequest> protocol = new FelixRpcProtocol<>();
        MsgHeader header = new MsgHeader();
        long requestId = FelixRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        FelixRpcRequest request = new FelixRpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        protocol.setBody(request);

        RpcConsumer rpcConsumer = new RpcConsumer();
        FelixRpcFuture<FelixRpcResponse> future = new FelixRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        FelixRpcRequestHolder.REQUEST_MAP.put(requestId, future);
        rpcConsumer.sendRequest(protocol, this.registryService);

        // TODO hold request by ThreadLocal


        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
