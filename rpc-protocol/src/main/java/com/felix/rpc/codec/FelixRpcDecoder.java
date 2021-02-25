package com.felix.rpc.codec;

import com.felix.rpc.common.FelixRpcRequest;
import com.felix.rpc.common.FelixRpcResponse;
import com.felix.rpc.protocol.FelixRpcProtocol;
import com.felix.rpc.protocol.MsgHeader;
import com.felix.rpc.protocol.MsgType;
import com.felix.rpc.protocol.ProtocolConstants;
import com.felix.rpc.serialization.RpcSerialization;
import com.felix.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @description: 自定义解码器
 * @author: Felix
 * @date: 2021/2/24 13:04
 */
public class FelixRpcDecoder extends ByteToMessageDecoder {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //byteBuf中数据长度小于18，说明没有数据内容，直接返回
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();

        //读取并检验魔数
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        //魔数校验通过，读取协议头其他信息
        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        int dataLength = in.readInt();
        /**
         * 即使已经可以完整读取出协议头Header，但是协议体Body有可能还未就绪。
         * 所以在刚开始读取数据时，需要使用markReaderIndex()方法标记读指针位置，
         * 当ByteBuf中可读字节长度小于协议体Body的长度时，再使用resetReaderIndex()还原读指针位置，
         * 说明现在ByteBuf中可读字节还不够一个完整的数据包
         */
        if (in.readableBytes() < dataLength) {
            //还原读指针位置
            in.resetReaderIndex();
            return;
        }
        //开始读取
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        //验证报文类型
        /**
         * 根据不同的报文类型，需要反序列化出不同的协议对象。
         * 在RPC请求调用的场景下，服务提供者需要将协议体内容反序列化成FelixRpcRequest对象；
         * 在RPC结果响应的场景下，服务消费者需要将协议体内容反序列化成FelixRpcResponse对象
         */
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);
        //获取序列化方式
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);
        switch (msgTypeEnum) {
            case REQUEST:
                //请求调用场景，服务提供者将协议体内容反序列化为Request对象
                FelixRpcRequest request = rpcSerialization.deserialize(data, FelixRpcRequest.class);
                if (request != null) {
                    //设置协议头和协议体
                    FelixRpcProtocol<FelixRpcRequest> protocol = new FelixRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    //输出
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                //结果响应场景，服务调用者需要将协议体内容反序列化为Response对象
                FelixRpcResponse response = rpcSerialization.deserialize(data, FelixRpcResponse.class);
                if (response != null) {
                    FelixRpcProtocol<FelixRpcResponse> protocol = new FelixRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                //todo
                break;
            default:
                throw new IllegalArgumentException("illegal msgType: " + msgTypeEnum);
        }

    }
}
