package octoteam.tahiti.client;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import octoteam.tahiti.client.event.ConnectErrorEvent;
import octoteam.tahiti.client.event.ConnectedEvent;
import octoteam.tahiti.client.event.DisconnectedEvent;
import octoteam.tahiti.client.pipeline.*;
import octoteam.tahiti.protocol.SocketMessageProtos.*;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.pipeline.UserEventToEventBusHandler;
import wheellllll.config.Config;

import java.util.Date;
import java.util.function.Consumer;

public class TahitiClient {

    private EventBus eventBus;

    private Config config;

    private CallbackRepository callbackRepo;

    private volatile EventLoopGroup workerGroup;

    private volatile Bootstrap bootstrap;

    private volatile Channel channel;

    private final ExtendedContext extendedContext = new ExtendedContext();

    public TahitiClient(Config config, EventBus eventBus) {
        this.config = config;
        this.eventBus = eventBus;
        this.init();
    }

    public Channel getChannel() {
        return channel;
    }

    private void init() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        callbackRepo = new CallbackRepository();

        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                                .addLast(new HeartbeatPushHandler(extendedContext))
                                .addLast(new ResponseCallbackHandler(extendedContext, callbackRepo))
                                .addLast(new LoginResponseHandler(extendedContext))
                                .addLast(new SessionExpiredPushHandler(extendedContext))
                                .addLast(new MessageReceivedHandler(extendedContext))
                                .addLast(new GroupPushHandler(extendedContext))
                                .addLast(new BroadcastPushHandler(extendedContext))
                                .addLast(new ChatSyncResponseHandler(extendedContext))
                                .addLast(new SendMessageFilterHandler(extendedContext))
                                .addLast(new UserEventToEventBusHandler(eventBus))
                        ;
                    }
                });
    }

    public void shutdown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public ChannelFuture connectAsync() {
        ChannelFuture connectFuture = bootstrap.connect(config.getString("chatService.host"), config.getInt("chatService.port"));
        connectFuture.addListener(connFuture -> {
            if (!connectFuture.isSuccess()) {
                eventBus.post(new ConnectErrorEvent());
                return;
            }
            channel = connectFuture.channel();
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    eventBus.post(new DisconnectedEvent());
                    future.channel().disconnect();
                }
            });
            eventBus.post(new ConnectedEvent());
        });

        return connectFuture;
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    public ChannelFuture disconnectAsync() {
        if (channel != null) {
            return channel.close();
        } else {
            return null;
        }
    }

    private Message.Builder buildRequest(Consumer<Message> callback) {
        return Message
                .newBuilder()
                .setSeqId(callbackRepo.getNextSequence(callback))
                .setDirection(Message.DirectionCode.REQUEST);
    }

    private Message.Builder buildRequest() {
        return buildRequest(null);
    }

    public void login(String username, String password, Consumer<Message> callback) {
        Message.Builder req = buildRequest(callback)
                .setService(Message.ServiceCode.USER_SIGN_IN_REQUEST)
                .setUserSignInReq(UserSignInReqBody.newBuilder()
                        .setUsername(username)
                        .setPassword(password)
                );
        channel.writeAndFlush(req.build());
    }

    public void login(String username, String password) {
        login(username, password, null);
    }

    public void sendMessage(String group, String message, Consumer<Message> callback) {
        Message.Builder req = buildRequest(callback)
                .setService(Message.ServiceCode.CHAT_PUBLISH_REQUEST)
                .setChatPublishReq(ChatPublishReqBody.newBuilder()
                        .setPayload(message)
                        .setGroupId(group)
                        .setTimestamp(new Date().getTime())
                );
        Message msg = req.build();
        channel.writeAndFlush(msg);
    }

    public void sendMessage(String group, String message) {
        sendMessage(group, message, null);
    }

    public void joinGroup(String group, Consumer<Message> callback) {
        Message.Builder req = buildRequest(callback)
                .setService(Message.ServiceCode.GROUP_REQUEST)
                .setGroupReq(GroupReqBody.newBuilder()
                        .setOp(GroupOperation.JOIN)
                        .setGroupId(group)
                );
        Message msg = req.build();
        channel.writeAndFlush(msg);
    }

    public void joinGroup(String group) {
        joinGroup(group, null);
    }

    public void syncGroupMessage(String group, long since, Consumer<Message> callback) {
        Message.Builder req = buildRequest(callback)
                .setService(Message.ServiceCode.CHAT_SYNC_REQUEST)
                .setChatSyncReq(ChatSyncReqBody.newBuilder()
                        .setGroupId(group)
                        .setSince(since)
                );
        Message msg = req.build();
        channel.writeAndFlush(msg);
    }

    public void syncGroupMessage(String group, long since) {
        syncGroupMessage(group, since, null);
    }

}
