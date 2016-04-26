package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.netty.MessageHandler;

public class MessageReceivedHandler extends MessageHandler{
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getService() == Message.ServiceCode.CHAT_BROADCAST_PUSH) {
            ctx.fireUserEventTriggered(new MessageReceivedEvent(msg));
        }
        ctx.fireChannelRead(msg);
    }
}
