package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.service.MessageService;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

/**
 * 判断消息类型，如果属于CHAT_PUBLISH_REQUEST类型则回复成功。
 */
@ChannelHandler.Sharable
public class MessageRequestHandler extends MessageHandler {

    private MessageService messageService;

    public MessageRequestHandler(ExtendedContext extendedContext, MessageService messageService) {
        super(extendedContext);
        this.messageService = messageService;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.CHAT_PUBLISH_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        ctx.fireUserEventTriggered(new MessageReceivedEvent(msg));

        // Identify user group
        // TODO: message should carry group id
        String[] joinedGroups = getExtendedContext().getJoinedGroups(ctx.channel());
        String group = joinedGroups[0];

        // Save message to database
        Credential currentCredential = (Credential) PipelineHelper.getSession(ctx.channel()).get("credential");
        messageService.addMessage(
                currentCredential.getUID(),
                group,
                msg.getChatPublishReq().getPayload()
        );

        // Push message to group members
        Message.Builder pushMessage = Message
                .newBuilder()
                .setDirection(Message.DirectionCode.PUSH)
                .setService(Message.ServiceCode.CHAT_BROADCAST_PUSH);
        getExtendedContext().of(group).writeAndFlush(pushMessage);

        // Response OK
        Message.Builder resp = ProtocolUtil
                .buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS);
        ctx.writeAndFlush(resp.build());

        ctx.fireChannelRead(msg);
    }

}
