package com.felix.rpc.provider;

import com.felix.rpc.codec.FelixRpcDecoder;
import com.felix.rpc.codec.FelixRpcEncoder;
import com.felix.rpc.common.RpcServiceHelper;
import com.felix.rpc.common.ServiceMeta;
import com.felix.rpc.handler.RpcRequestHandler;
import com.felix.rpc.provider.annotation.RpcService;
import com.felix.rpc.provider.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 服务提供者
 * @author: Felix
 * @date: 2021/2/25 14:54
 */
@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;
    private final int serverPort;
    private final RegistryService serviceRegistry;

    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    public RpcProvider(int serverPort, RegistryService serviceRegistry) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        }).start();
    }

    /**
     * 启动Rpc服务
     */
    private void startRpcServer() throws Exception {
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();

        //启动Reactor主从线程
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new FelixRpcEncoder())
                                    .addLast(new FelixRpcDecoder())
                                    .addLast(new RpcRequestHandler(rpcServiceMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("server addr {} started on port {}", this.serverAddress, this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /**
         * Spring 的 BeanPostProcessor 接口给提供了对 Bean 进行再加工的扩展点，
         * BeanPostProcessor 常用于处理自定义注解。
         * 自定义的 Bean 可以通过实现 BeanPostProcessor 接口，在 Bean 实例化的前后加入自定义的逻辑处理
         */

        /**
         * RpcProvider 重写了 BeanPostProcessor 接口的 postProcessAfterInitialization 方法，
         * 对所有初始化完成后的 Bean 进行扫描。如果 Bean 包含 @RpcService 注解，
         * 那么通过注解读取服务的元数据信息并构造出 ServiceMeta 对象，接下来准备将服务的元数据信息发布至注册中心，
         * 注册中心的实现我们先暂且跳过，后面会有单独一节课进行讲解注册中心的实现。
         * 此外，RpcProvider 还维护了一个 rpcServiceMap，存放服务初始化后所对应的 Bean，
         * rpcServiceMap 起到了缓存的角色，在处理 RPC 请求时可以直接通过 rpcServiceMap 拿到对应的服务进行调用。
         */
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();

            try {
                //封装服务元数据
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(serverPort);
                serviceMeta.setServiceName(serviceName);
                serviceMeta.setServiceVersion(serviceVersion);

                //注册服务元数据到注册中心
                serviceRegistry.register(serviceMeta);
                //放置到map中
                rpcServiceMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()), bean);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceName, serviceVersion, e);
            }
        }
        return bean;
    }
}
