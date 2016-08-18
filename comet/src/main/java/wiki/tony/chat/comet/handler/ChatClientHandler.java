package wiki.tony.chat.comet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiki.tony.chat.base.bean.AuthToken;
import wiki.tony.chat.base.bean.Message;
import wiki.tony.chat.base.util.JsonUtils;
import wiki.tony.chat.comet.bean.Proto;
import wiki.tony.chat.comet.operation.AuthOperation;
import wiki.tony.chat.comet.operation.MessageOperation;

import java.io.IOException;

/**
 * netty的客户端事件处理逻辑
 * @author Yao
 * @create 2016/818
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<Proto> {
    private static Logger LOG= LoggerFactory.getLogger(ChatClientHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Proto proto) throws IOException {
        if(proto.getOperation() == AuthOperation.OP_REPLY){
            System.out.println("Server auth to Client " + JsonUtils.fromJson(proto.getBody(), AuthToken.class).getUserId()+" succeed! ^_^");
        }else if(proto.getOperation() == MessageOperation.OP_REPLY){
            System.out.println("Received Server Echo:" + JsonUtils.fromJson(proto.getBody(), Message.class).getContent());
        }else{//其他客户端发来的消息
            System.out.println("Received Client "+JsonUtils.fromJson(proto.getBody(), Message.class).getFrom()+ " msg: " + JsonUtils.fromJson(proto.getBody(), Message.class).getContent() );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.info("exceptionCaught", cause);
    }

}
