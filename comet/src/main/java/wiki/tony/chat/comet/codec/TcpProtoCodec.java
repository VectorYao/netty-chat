package wiki.tony.chat.comet.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import wiki.tony.chat.comet.bean.Proto;

import java.util.List;

/**
 * tcp codec
 * tcp协议的编解码器，一般来说，codec必须放在channel的pipeline事件处理链的首部
 * Created by Tony on 4/14/16.
 */
@Component
@ChannelHandler.Sharable
public class TcpProtoCodec extends MessageToMessageCodec<ByteBuf, Proto> {

    private Logger logger = LoggerFactory.getLogger(TcpProtoCodec.class);

    /**
     * tcp协议编码器，将Outbound类型的消息编码为ByteBuf对象
     * ByteBuf消息的所包含的格式含义如下：
     * [PacketLen : HeaderLen : Version : OperationSeqId : Body]
     *
     * @param channelHandlerContext Channel的上下文
     * @param proto OutBound类型的消息对象
     * @param list Handler处理完后得到的对象列表（供pipeline上的其他handler继续处理）
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Proto proto, List<Object> list) throws Exception {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        if (proto.getBody() != null) {
            byteBuf.writeInt(Proto.HEADER_LENGTH + proto.getBody().length);
            byteBuf.writeShort(Proto.HEADER_LENGTH);
            byteBuf.writeShort(Proto.VERSION);
            byteBuf.writeInt(proto.getOperation());
            byteBuf.writeInt(proto.getSeqId());
            byteBuf.writeBytes(proto.getBody());
        } else {
            byteBuf.writeInt(Proto.HEADER_LENGTH);
            byteBuf.writeShort(Proto.HEADER_LENGTH);
            byteBuf.writeShort(Proto.VERSION);
            byteBuf.writeInt(proto.getOperation());
            byteBuf.writeInt(proto.getSeqId());
        }

        list.add(byteBuf);

        logger.debug("encode: {}", proto);
    }

    /**
     * tcp协议解码器，将接收到的ByteBuf类型的消息解码为Proto对象
     * 正确使用的前提是知道encode的编码含义，故必须和encode配对使用
     * @param channelHandlerContext Channel的上下文
     * @param byteBuf 接受到的Inbound类型消息
     * @param list Handler处理完后得到的对象列表
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Proto proto = new Proto();
        proto.setPacketLen(byteBuf.readInt());
        proto.setHeaderLen(byteBuf.readShort());
        proto.setVersion(byteBuf.readShort());
        proto.setOperation(byteBuf.readInt());
        proto.setSeqId(byteBuf.readInt());
        if (proto.getPacketLen() > proto.getHeaderLen()) {
            byte[] bytes = new byte[proto.getPacketLen() - proto.getHeaderLen()];
            byteBuf.readBytes(bytes);
            proto.setBody(bytes);
        }

        list.add(proto);

        logger.debug("decode: {}", proto);
    }
}
