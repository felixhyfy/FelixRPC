package com.felix.rpc.codec;

import com.felix.rpc.protocol.FelixRpcProtocol;
import com.felix.rpc.protocol.MsgHeader;
import com.felix.rpc.serialization.RpcSerialization;
import com.felix.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @description: 自定义编码器
 * @author: Felix
 * @date: 2021/2/24 11:54
 */
public class FelixRpcEncoder extends MessageToByteEncoder<FelixRpcProtocol<Object>> {

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
    protected void encode(ChannelHandlerContext ctx, FelixRpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        MsgHeader header = msg.getHeader();
        //写头信息到bytebuf中，按照自定义的格式顺序
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        //从Serialization Factory中获取序列化算法
        final RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        //序列化
        final byte[] data = rpcSerialization.serialize(msg.getBody());
        //写入序列化后字符数组的长度和数据内容
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
