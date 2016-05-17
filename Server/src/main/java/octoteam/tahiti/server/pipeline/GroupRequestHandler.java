package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupOperation;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

@ChannelHandler.Sharable
public class GroupRequestHandler extends MessageHandler {

    public GroupRequestHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.GROUP_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg.getGroupReq().getOp() == GroupOperation.JOIN) {
            getExtendedContext().join(ctx.channel(), msg.getGroupReq().getGroupId());
        } else {
            getExtendedContext().leave(ctx.channel(), msg.getGroupReq().getGroupId());
        }
        Message resp = ProtocolUtil
                .buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS)
                .build();
        ctx.channel().writeAndFlush(resp);
    }

}
