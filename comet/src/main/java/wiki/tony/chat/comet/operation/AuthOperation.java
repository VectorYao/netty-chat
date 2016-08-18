package wiki.tony.chat.comet.operation;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wiki.tony.chat.base.bean.AuthToken;
import wiki.tony.chat.base.bean.MQTopics;
import wiki.tony.chat.base.mq.MQConsumer;
import wiki.tony.chat.base.mq.MQMessage;
import wiki.tony.chat.base.mq.MQMessageListener;
import wiki.tony.chat.base.mq.impl.test.TestMQConsumer;
import wiki.tony.chat.base.service.AuthService;
import wiki.tony.chat.base.util.JsonUtils;
import wiki.tony.chat.comet.bean.Proto;

/**
 * 客户端连接认证
 *
 * Created by Tony on 4/14/16.
 */
@Component
public class AuthOperation extends AbstractOperation {

    private final Logger logger = LoggerFactory.getLogger(AuthOperation.class);

    public static final int OP = 0;
    public static final int OP_REPLY = 1;

    @Value("${server.id}")
    private int serverId;
    @Autowired
    private AuthService authService;
    private MQConsumer mqConsumer = new TestMQConsumer();

    @Override
    public Integer op() {
        return OP;
    }

    @Override
    public void action(Channel ch, Proto proto) throws Exception {
        AuthToken auth = JsonUtils.fromJson(proto.getBody(), AuthToken.class);

        // 校验token值
        if (authService.auth(serverId, auth)) {
            // 设置该channel的用户token值
            setAuthToken(ch, auth);
            // 绑定消息事件监听器
            addConsumerListener(ch, auth.getUserId());
            logger.debug("auth ok");
        } else {
            logger.debug("auth fail");
        }
        //返回给客户端操作成功的消息
        proto.setOperation(OP_REPLY);
        ch.writeAndFlush(proto);
    }

    private void addConsumerListener(final Channel ch, final Long userId) {
        mqConsumer.addListener(MQTopics.MESSAGE, userId + "", new MQMessageListener() {
            @Override
            public void onMessage(MQMessage message) {
                Proto proto = new Proto();
                proto.setOperation(MessageOperation.OP);
                proto.setBody(message.getData());
                ch.writeAndFlush(proto);

                //打印出接收到客户端的消息内容
                logger.info("consumer: {}", proto);
            }
        });
    }

}
