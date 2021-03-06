package octoteam.tahiti.server.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import octoteam.tahiti.protocol.SocketMessageProtos.BroadcastPushBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.server.shared.microservice.rmi.IGroupServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

import java.rmi.RemoteException;
import java.util.List;

/**
 * 处理用户发送消息的请求, 即 CHAT_PUBLISH_REQUEST
 */
@ChannelHandler.Sharable
public class ChatPublishRequestHandler extends MessageHandler {

    private IStorageServiceProvider chatService;
    private IGroupServiceProvider groupService;

    public ChatPublishRequestHandler(
            ExtendedContext extendedContext,
            IStorageServiceProvider chatService,
            IGroupServiceProvider groupService
    ) {
        super(extendedContext);
        this.chatService = chatService;
        this.groupService = groupService;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws RemoteException {
        if (msg.getService() != Message.ServiceCode.CHAT_PUBLISH_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        ctx.fireUserEventTriggered(new MessageReceivedEvent(msg));

        // Identify user group
        String group = msg.getChatPublishReq().getGroupId();

        // Save message to database
        Credential currentCredential = (Credential) PipelineHelper.getSession(ctx.channel()).get("credential");
        if (currentCredential == null) {
            currentCredential = Credential.getGuestCredential();
        }
        chatService.addChat(
                currentCredential.getUID(),
                currentCredential.getUsername(),
                group,
                msg.getChatPublishReq().getPayload()
        );

        // Push message to group members
        Message.Builder pushMessage = Message
                .newBuilder()
                .setDirection(Message.DirectionCode.PUSH)
                .setService(Message.ServiceCode.CHAT_BROADCAST_PUSH)
                .setBroadcastPush(BroadcastPushBody.newBuilder()
                        .setGroupId(group)
                );
        List<ChannelId> channels = groupService.getChannelsInGroup(group);
        for (ChannelId channelId : channels) {
            Channel ch = getExtendedContext().lookupManagedChannels(channelId);
            if (ch != null) {
                ch.writeAndFlush(pushMessage);
            }
        }

        // Response OK
        Message.Builder resp = ProtocolUtil
                .buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS);
        ctx.writeAndFlush(resp.build());

        ctx.fireChannelRead(msg);
    }

}
