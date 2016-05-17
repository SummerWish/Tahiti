package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import octoteam.tahiti.client.event.SendMessageEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块过滤 CHAT_PUBLISH_REQUEST 上行消息. 当准备发送
 * CHAT_PUBLISH_REQUEST 消息时, 会触发 SendMessageEvent.
 */
@ChannelHandler.Sharable
public class SendMessageFilterHandler extends MessageHandler {

    public SendMessageFilterHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageSent(ChannelHandlerContext ctx, Message msg, ChannelPromise promise) throws Exception {
        if (msg.getService() == Message.ServiceCode.CHAT_PUBLISH_REQUEST) {
            ctx.fireUserEventTriggered(new SendMessageEvent(
                    msg.getChatPublishReq().getPayload(),
                    msg.getChatPublishReq().getTimestamp()
            ));
        }
        super.messageSent(ctx, msg, promise);
    }

}
