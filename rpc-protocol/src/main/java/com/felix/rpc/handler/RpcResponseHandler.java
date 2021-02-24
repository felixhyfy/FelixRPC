package com.felix.rpc.handler;

import com.felix.rpc.common.FelixRpcFuture;
import com.felix.rpc.common.FelixRpcRequestHolder;
import com.felix.rpc.common.FelixRpcResponse;
import com.felix.rpc.protocol.FelixRpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @description: 入站到服务消费者，返回RPC响应信息
 * @author: Felix
 * @date: 2021/2/24 17:01
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<FelixRpcProtocol<FelixRpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FelixRpcProtocol<FelixRpcResponse> protocol) throws Exception {
        long requestId = protocol.getHeader().getRequestId();
        //删除当前requestId的请求信息，获得对应的异步future
        FelixRpcFuture<FelixRpcResponse> future = FelixRpcRequestHolder.REQUEST_MAP.remove(requestId);
        //返回响应信息
        future.getPromise().setSuccess(protocol.getBody());
    }
}
