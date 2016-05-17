package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

/**
 * 该模块接收到下行的 HEARTBEAT_PUSH 后，会发送上行的 ACK。
 */
@ChannelHandler.Sharable
public class HeartbeatPushHandler extends MessageHandler {

    public HeartbeatPushHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() == Message.ServiceCode.HEARTBEAT_PUSH) {
            ctx.writeAndFlush(ProtocolUtil.buildAck(msg));
        }
        ctx.fireChannelRead(msg);
    }

}
