package octoteam.tahiti.client;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import octoteam.tahiti.client.configuration.ChatServiceConfiguration;
import octoteam.tahiti.client.configuration.ClientConfiguration;
import octoteam.tahiti.client.event.ConnectErrorEvent;
import octoteam.tahiti.client.event.ConnectedEvent;
import octoteam.tahiti.client.event.DisconnectedEvent;
import octoteam.tahiti.client.pipeline.*;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatMessageReqBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInReqBody;
import octoteam.tahiti.shared.netty.pipeline.UserEventToEventBusHandler;

import java.util.Date;

public class TahitiClient {

    private EventBus eventBus;

    private ClientConfiguration config;

    private CallbackRepository callbackRepo;

    private volatile EventLoopGroup workerGroup;

    private volatile Bootstrap bootstrap;

    private volatile Channel channel;

    public TahitiClient(ClientConfiguration config, EventBus eventBus) {
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
                                .addLast(new HeartbeatEventHandler())
                                .addLast(new ResponseCallbackHandler(callbackRepo))
                                .addLast(new LoginResponseHandler())
                                .addLast(new SessionExpireEventHandler())
                                .addLast(new BroadcastEventHandler())
                                .addLast(new SendMessageFilterHandler())
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
        ChatServiceConfiguration serviceConfig = config.getChatService();
        ChannelFuture connectFuture = bootstrap.connect(serviceConfig.getHost(), serviceConfig.getPort());
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

    private Message.Builder buildRequest(Function<Message, Void> callback) {
        return Message
                .newBuilder()
                .setSeqId(callbackRepo.getNextSequence(callback))
                .setDirection(Message.DirectionCode.REQUEST);
    }

    private Message.Builder buildRequest() {
        return buildRequest(null);
    }

    public void login(String username, String password, Function<Message, Void> callback) {
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

    public void sendMessage(String message) {
        Message.Builder req = buildRequest()
                .setService(Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST)
                .setChatMessageReq(ChatMessageReqBody.newBuilder()
                        .setPayload(message)
                        .setTimestamp(new Date().getTime())
                );
        Message msg = req.build();
        channel.writeAndFlush(msg);
    }

}
