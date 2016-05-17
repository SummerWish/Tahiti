package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.event.GroupEvent;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 处理用户加入组或离开组的事件
 */
public class GroupPushHandler extends MessageHandler {

    public GroupPushHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, SocketMessageProtos.Message msg) {
        if (msg.getService() == SocketMessageProtos.Message.ServiceCode.GROUP_PUSH) {
            ctx.fireUserEventTriggered(new GroupEvent(
                    msg.getGroupPushBody().getGroupId(),
                    msg.getGroupPushBody().getUser().getUsername(),
                    msg.getGroupPushBody().getOp() == SocketMessageProtos.GroupOperation.LEAVE
            ));
        }
        ctx.fireChannelRead(msg);
    }

}
