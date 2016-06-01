package octoteam.tahiti.server;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.Message.ServiceCode;
import octoteam.tahiti.server.event.RateLimitExceededEvent;
import octoteam.tahiti.server.pipeline.*;
import octoteam.tahiti.server.shared.microservice.rmi.IAuthServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.IGroupServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.pipeline.UserEventToEventBusHandler;
import wheellllll.config.Config;
import wheellllll.license.License;

import java.util.concurrent.TimeUnit;

/**
 * 服务端
 * 初始化客户端链接，提供用户验证、收发消息及生成日志服务
 */
public class TahitiServer {

    private final EventBus eventBus;

    private final Config config;

    private final IAuthServiceProvider accountService;

    private final IStorageServiceProvider chatService;

    private final IGroupServiceProvider groupService;

    private final ExtendedContext extendedContext = new ExtendedContext();

    /**
     * 根据参数构造并初始化服务端
     *
     * @param config         服务端相关配置参数
     * @param eventBus       服务端事件总线
     * @param accountService 用户服务
     */
    public TahitiServer(
            Config config,
            EventBus eventBus,
            IAuthServiceProvider accountService,
            IStorageServiceProvider chatService,
            IGroupServiceProvider groupService
    ) {
        this.eventBus = eventBus;
        this.config = config;
        this.accountService = accountService;
        this.chatService = chatService;
        this.groupService = groupService;
    }

    /**
     * 启动服务端，初始化每个客户端链接，为链接添加消息处理队列
     *
     * @throws Exception
     */
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
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                                    .addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS))
                                    .addLast(new HeartbeatHandler(extendedContext))
                                    .addLast(new PingRequestHandler(extendedContext))
                                    .addLast(new AuthRequestHandler(extendedContext, accountService))
                                    .addLast(new AuthFilterHandler(extendedContext))
                                    .addLast(new RequestRateLimitHandler(
                                            extendedContext,
                                            ServiceCode.CHAT_PUBLISH_REQUEST,
                                            RateLimitExceededEvent.NAME_PER_SECOND,
                                            () -> new License(License.LicenseType.THROUGHPUT,
                                                    config.getInt("rateLimit.perSecond")))
                                    )
                                    .addLast(new RequestRateLimitHandler(
                                            extendedContext,
                                            ServiceCode.CHAT_PUBLISH_REQUEST,
                                            RateLimitExceededEvent.NAME_PER_SESSION,
                                            () -> new License(License.LicenseType.CAPACITY,
                                                    config.getInt("rateLimit.perSession")))
                                    )
                                    .addLast(new SessionExpireHandler(extendedContext))
                                    .addLast(new GroupRequestHandler(extendedContext, groupService))
                                    .addLast(new ChatPublishRequestHandler(extendedContext, chatService, groupService))
                                    .addLast(new ChatSyncRequestHandler(extendedContext, chatService))
                                    .addLast(new UserEventToEventBusHandler(eventBus))
                            ;
                        }
                    });

            ChannelFuture cf = serverBootstrap.bind(config.getString("chatService.bindHost"), config.getInt("chatService.bindPort")).sync();
            System.out.println(String.format(
                    "Tahiti server listening at %s:%s",
                    config.getString("chatService.bindHost"),
                    config.getInt("chatService.bindPort")
            ));
            cf.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
