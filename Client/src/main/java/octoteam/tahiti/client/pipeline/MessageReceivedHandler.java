package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * @deprecated
 */
public class MessageReceivedHandler extends MessageHandler {

    public MessageReceivedHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        // TODO: Replace CHAT_BROADCAST_PUSH
        if (msg.getService() == Message.ServiceCode.CHAT_BROADCAST_PUSH) {
            ctx.fireUserEventTriggered(new MessageReceivedEvent(msg));
        }
        ctx.fireChannelRead(msg);
    }

}
