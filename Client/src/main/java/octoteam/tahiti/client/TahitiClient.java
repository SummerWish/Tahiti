package octoteam.tahiti.client;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import octoteam.tahiti.client.configuration.ChatServiceConfiguration;
import octoteam.tahiti.client.configuration.ClientConfiguration;
import octoteam.tahiti.client.event.ConnectErrorEvent;
import octoteam.tahiti.client.event.ConnectedEvent;
import octoteam.tahiti.client.event.DisconnectedEvent;
import octoteam.tahiti.client.pipeline.ResponseHandler;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TahitiClient {

    private EventBus eventBus;

    private ClientConfiguration config;

    private static AtomicLong msgSequence;

    private volatile EventLoopGroup workerGroup;

    private volatile Bootstrap bootstrap;

    private volatile Channel channel;

    private ConcurrentHashMap<Long, Function<Message, Void>> callbacks;

    public TahitiClient(ClientConfiguration config) {
        this.eventBus = new EventBus();
        this.config = config;
        this.init();
    }

    public Channel getChannel() {
        return channel;
    }

    private void init() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        msgSequence = new AtomicLong();
        callbacks = new ConcurrentHashMap<>();

        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufEncoder())
                                .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                                .addLast(new ResponseHandler(TahitiClient.this));
                    }
                });
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public long getNextSequence() {
        return msgSequence.incrementAndGet();
    }

    public long getNextSequence(Function<Message, Void> r) {
        long seq = getNextSequence();
        callbacks.put(seq, r);
        return seq;
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

    public void resolveCallback(long seqId, Message msg) {
        if (callbacks.containsKey(seqId)) {
            Function<Message, Void> r = callbacks.get(seqId);
            callbacks.remove(seqId);
            r.apply(msg);
        }
    }

    public void login(String username, String password, Function<Message, Void> callback) {
        Message.Builder req = Message
                .newBuilder()
                .setSeqId(getNextSequence(callback))
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_SIGN_IN_REQUEST)
                .setUserSignInReq(SocketMessageProtos.UserSignInReqBody.newBuilder()
                        .setUsername(username)
                        .setPassword(password)
                );
        channel.writeAndFlush(req.build());
    }

    public void sendMessage(String message) {
        Message.Builder req = Message
                .newBuilder()
                .setSeqId(getNextSequence())
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST)
                .setChatMessageReq(SocketMessageProtos.ChatMessageReqBody.newBuilder()
                        .setPayload(message)
                );
        Message msg = req.build();
        channel.writeAndFlush(msg);
    }
/*
    //add receive message part
    public void receive(String sendername, Function<Message, Void> callback){
        Message.Builder req = Message
                .newBuilder()
                .setSeqId(getNextSequence(callback))
                .setDirection(Message.DirectionCode.RESPONSE)
                .setService(Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST)
                //.getChatMessageReqBuilder()
        channel.read();

    }
*/
}
