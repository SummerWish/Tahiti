package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.event.ChatEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatMessage;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

import java.util.List;

public class ChatSyncResponseHandler extends MessageHandler {

    public ChatSyncResponseHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.CHAT_SYNC_REQUEST) {
            if (msg.getStatus() == Message.StatusCode.SUCCESS) {
                List<ChatMessage> chats = msg.getChatSyncResp().getMessageList();
                chats.stream().forEach(cm -> ctx.fireUserEventTriggered(new ChatEvent(
                        cm.getPayload(),
                        cm.getUser().getUsername(),
                        cm.getTimestamp()
                )));
            }
        }
        ctx.fireChannelRead(msg);
    }

}
