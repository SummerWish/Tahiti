package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatMessage;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatSyncRespBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.User;
import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.server.shared.model.Chat;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理用户同步消息的请求
 */
@ChannelHandler.Sharable
public class ChatSyncRequestHandler extends MessageHandler {

    private IStorageServiceProvider chatService;

    public ChatSyncRequestHandler(ExtendedContext extendedContext, IStorageServiceProvider chatService) {
        super(extendedContext);
        this.chatService = chatService;
    }

    private List<ChatMessage> getGroupHistoryMessages(String group, long since) throws RemoteException {
        List<Chat> chats = chatService.getGroupChatsSince(group, since);
        return chats.stream()
                .map(m -> ChatMessage.newBuilder()
                        .setGroupId(m.getGroupId())
                        .setPayload(m.getContent())
                        .setTimestamp(m.getSendAt())
                        .setUser(User.newBuilder()
                                .setUID(m.getUserId())
                                .setUsername(m.getUsername())
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws RemoteException {
        if (msg.getService() != Message.ServiceCode.CHAT_SYNC_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        String group = msg.getChatSyncReq().getGroupId();
        List<ChatMessage> historyMessages = getGroupHistoryMessages(group, msg.getChatSyncReq().getSince());

        Message.Builder resp = ProtocolUtil.buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS)
                .setChatSyncResp(ChatSyncRespBody.newBuilder()
                        .setGroupId(group)
                        .setTime(new Date().getTime())
                        .addAllMessage(historyMessages)
                );
        ctx.writeAndFlush(resp.build());

        ctx.fireChannelRead(msg);
    }

}
