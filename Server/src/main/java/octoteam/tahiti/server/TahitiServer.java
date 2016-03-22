package octoteam.tahiti.server;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.configuration.ChatServiceConfiguration;
import octoteam.tahiti.server.configuration.ServerConfiguration;
import octoteam.tahiti.server.pipeline.*;

import java.util.concurrent.TimeUnit;

public class TahitiServer {

    public final static AttributeKey<Session> ATTR_KEY_SESSION = new AttributeKey<>("session");

    private EventBus eventBus;

    private ServerConfiguration config;

    public TahitiServer(ServerConfiguration config) {
        this.eventBus = new EventBus();
        this.config = config;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                                    .addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS))
                                    .addLast(new HeartbeatHandler(TahitiServer.this))
                                    .addLast(new RawHandler(TahitiServer.this))
                                    .addLast(new PingRequestHandler(TahitiServer.this))
                                    .addLast(new AuthRequestHandler(TahitiServer.this, config.getAccounts()))
                                    .addLast(new RateLimitHandler(TahitiServer.this))
                                    .addLast(new MessageRequestHandler(TahitiServer.this))
                                    .addLast(new FinalHandler(TahitiServer.this));
                        }
                    });

            ChatServiceConfiguration serviceConfig = this.config.getChatService();
            ChannelFuture cf = serverBootstrap.bind(serviceConfig.getBindHost(), serviceConfig.getBindPort()).sync();
            System.out.println(String.format(
                    "Tahiti server listening at %s:%s",
                    serviceConfig.getBindHost(),
                    serviceConfig.getBindPort()
            ));
            cf.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
