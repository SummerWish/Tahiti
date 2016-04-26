package octoteam.tahiti.shared.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.shared.event.MessageReceivedEvent;
import octoteam.tahiti.shared.netty.MessageHandler;

public class MessageReceivedHandler extends MessageHandler{
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, SocketMessageProtos.Message msg) throws Exception {
        ctx.fireUserEventTriggered(new MessageReceivedEvent(msg));
        ctx.fireChannelRead(msg);
    }
}
