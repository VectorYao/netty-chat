package wiki.tony.chat.comet.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import wiki.tony.chat.base.bean.AuthToken;
import wiki.tony.chat.base.bean.Message;
import wiki.tony.chat.base.util.JsonUtils;
import wiki.tony.chat.comet.bean.Proto;
import wiki.tony.chat.comet.codec.TcpProtoCodec;
import wiki.tony.chat.comet.handler.ChatClientHandler;

import java.util.Scanner;

/**
 * Created by Tony on 4/14/16.
 */
public class ChatClientOne {
    private final String host;
    private final int port;

    public ChatClientOne(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TcpProtoCodec());
                            ch.pipeline().addLast(new ChatClientHandler());
                        }
                    });

            ChannelFuture f = b.connect().sync();

            AuthToken auth = new AuthToken();
            auth.setUserId(1);
            auth.setToken("Client One");
            Proto proto = new Proto();
            proto.setVersion((short) 1);
            proto.setOperation(0);
            proto.setBody(JsonUtils.toJson(auth).getBytes());
            f.channel().writeAndFlush(proto);



            Scanner sc = new Scanner(System.in);
            while(sc.hasNext()){
                //控制台输入的格式为: 消息接收方id:消息内容content
                String[] in = sc.nextLine().split(":");
                Message msg = new Message();
                msg.setTo(Long.valueOf(in[0]));
                msg.setContent(in[1]);

                proto = new Proto();
                proto.setVersion((short) 1);
                proto.setOperation(2);
                proto.setBody(JsonUtils.toJson(msg).getBytes());
                f.channel().writeAndFlush(proto);

                System.out.print(in);
            }

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new ChatClientOne("127.0.0.1", 9090).start();
    }
}
