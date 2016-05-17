package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.event.LoginAttemptEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块处理下行消息中的登录响应 (USER_SIGN_IN_REQUEST RESPONSE)
 * 收到该响应时, 会产生 LoginAttemptEvent
 */
@ChannelHandler.Sharable
public class LoginResponseHandler extends MessageHandler {

    public LoginResponseHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.USER_SIGN_IN_REQUEST) {
            ctx.fireUserEventTriggered(new LoginAttemptEvent(msg.getStatus() == Message.StatusCode.SUCCESS));
        }
        ctx.fireChannelRead(msg);
    }

}
