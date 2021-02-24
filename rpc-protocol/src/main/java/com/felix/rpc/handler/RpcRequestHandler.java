package com.felix.rpc.handler;

import com.felix.rpc.common.FelixRpcRequest;
import com.felix.rpc.common.FelixRpcResponse;
import com.felix.rpc.common.RpcServiceHelper;
import com.felix.rpc.protocol.FelixRpcProtocol;
import com.felix.rpc.protocol.MsgHeader;
import com.felix.rpc.protocol.MsgStatus;
import com.felix.rpc.protocol.MsgType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @description: 自定义入站处理器，执行RPC请求调用
 * @author: Felix
 * @date: 2021/2/24 15:43
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<FelixRpcProtocol<FelixRpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FelixRpcProtocol<FelixRpcRequest> protocol) throws Exception {
        RpcRequestProcessor.submitRequest(() -> {
            FelixRpcProtocol<FelixRpcResponse> responseProtocol = new FelixRpcProtocol<>();
            FelixRpcResponse response = new FelixRpcResponse();
            //设置响应的header
            MsgHeader header = protocol.getHeader();
            //设置为响应类型消息
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            try {
                //执行RPC调用
                Object result = handle(protocol.getBody());
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.getCode());
                //将响应封装到responseProtocol中
                responseProtocol.setHeader(header);
                responseProtocol.setBody(response);
            } catch (Throwable throwable) {
                //执行失败，设置失败消息到response中
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                response.setMsg(throwable.getMessage());
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            //写入到channelHandlerContext中并刷新
            ctx.writeAndFlush(responseProtocol);
        });
    }

    /**
     * 执行RPC调用，使用动态代理
     *
     * @param request RPC请求
     * @return 调用结果
     */
    private Object handle(FelixRpcRequest request) throws Throwable {
        //获取serviceKey和对应的Bean
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);

        //判断bean
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not existL %s:%s", request.getClassName(), request.getMethodName()));
        }

        //动态代理，使用fastClass执行对应service的method
        //获取到service的类、方法、参数类型和参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParams();

        //使用动态代理框架cglib中的代理类FastClass 创建对应service的代理类
        FastClass fastClass = FastClass.create(serviceClass);
        //使用方法名和入参类型计算出索引
        int index = fastClass.getIndex(methodName, parameterTypes);
        //执行方法
        return fastClass.invoke(index, serviceBean, params);
    }
}
