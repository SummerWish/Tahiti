package octoteam.tahiti.server.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupOperation;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupRespBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.User;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

import java.util.List;
import java.util.stream.Collectors;

@ChannelHandler.Sharable
public class GroupRequestHandler extends MessageHandler {

    public GroupRequestHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    private User.Builder getUserFromSession(Channel c) {
        Credential credential = (Credential) PipelineHelper.getSession(c).get("credential");
        if (credential == null) {
            credential = Credential.getGuestCredential();
        }
        return User.newBuilder()
                .setUID(credential.getUID())
                .setUsername(credential.getUsername());
    }

    private List<User> getUsersInGroup(String group) {
        return getExtendedContext().of(group).stream()
                .map(c -> getUserFromSession(c).build())
                .collect(Collectors.toList());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.GROUP_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }
        String group = msg.getGroupReq().getGroupId();
        GroupRespBody.Builder bodyBuilder = GroupRespBody.newBuilder();

        // Broadcast join / leave
        Message push = Message.newBuilder()
                .setService(Message.ServiceCode.GROUP_PUSH)
                .setDirection(Message.DirectionCode.PUSH)
                .setGroupPushBody(SocketMessageProtos.GroupPushBody.newBuilder()
                        .setGroupId(group)
                        .setOp(msg.getGroupReq().getOp())
                        .setUser(getUserFromSession(ctx.channel()))
                )
                .build();
        getExtendedContext().of(group).writeAndFlush(push);

        // Join/leave group based on OP
        if (msg.getGroupReq().getOp() == GroupOperation.JOIN) {
            getExtendedContext().join(ctx.channel(), group);
            bodyBuilder.addAllUser(getUsersInGroup(group));
        } else {
            getExtendedContext().leave(ctx.channel(), group);
        }

        // Response
        Message resp = ProtocolUtil
                .buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS)
                .setGroupResp(bodyBuilder)
                .build();
        ctx.channel().writeAndFlush(resp);
    }

}
