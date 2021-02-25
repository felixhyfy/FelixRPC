package com.felix.rpc.consumer;

import com.felix.rpc.codec.FelixRpcDecoder;
import com.felix.rpc.codec.FelixRpcEncoder;
import com.felix.rpc.common.FelixRpcRequest;
import com.felix.rpc.common.RpcServiceHelper;
import com.felix.rpc.common.ServiceMeta;
import com.felix.rpc.handler.RpcResponseHandler;
import com.felix.rpc.protocol.FelixRpcProtocol;
import com.felix.rpc.provider.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: RPC 服务调用者对象
 * @author: Felix
 * @date: 2021/2/25 12:25
 */
@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new FelixRpcEncoder())
                                .addLast(new FelixRpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * 发送RPC调用请求
     *
     * @param protocol        包含调用请求的协议
     * @param registryService 注册中心服务
     * @throws Exception
     */
    public void sendRequest(FelixRpcProtocol<FelixRpcRequest> protocol, RegistryService registryService) throws Exception {
        //获取请求相关信息
        FelixRpcRequest request = protocol.getBody();
        //获取参数
        Object[] params = request.getParams();
        //构造serviceKey
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());

        //采用一致性hash算法实现服务发现功能
        //如果有参数的话，采用第一个参数的hashCode，否则采用serviceKey的hashCode
        int invokerHashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode();
        //获取调用到的服务节点
        ServiceMeta serviceMetaData = registryService.discovery(serviceKey, invokerHashCode);

        if (serviceMetaData != null) {
            //引导器根据 IP + Port 进行连接
            //使用Netty提供的Promise工具类来实现RPC请求的同步等待
            ChannelFuture future = bootstrap.connect(serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (future.isSuccess()) {
                    log.info("connect rpc server {} on port {} success", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                } else {
                    log.error("connect rpc server {} on port {} failed", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });
            future.channel().writeAndFlush(protocol);
        }

    }
}
