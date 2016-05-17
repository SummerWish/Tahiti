package octoteam.tahiti.server.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupOperation;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupRespBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.User;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class GroupRequestHandler extends MessageHandler {

    public GroupRequestHandler(ExtendedContext extendedContext) {
        super(extendedContext);
    }

    private List<User> getUsersInGroup(String group) {
        ArrayList<User> users = new ArrayList<>();
        for (Channel c : getExtendedContext().of(group)) {
            Credential credential = (Credential) PipelineHelper.getSession(c).get("credential");
            users.add(User.newBuilder()
                    .setUID(credential.getUID())
                    .setUsername(credential.getUsername())
                    .build()
            );
        }
        return users;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.GROUP_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }
        String group = msg.getGroupReq().getGroupId();
        GroupRespBody.Builder bodyBuilder = GroupRespBody.newBuilder();
        if (msg.getGroupReq().getOp() == GroupOperation.JOIN) {
            getExtendedContext().join(ctx.channel(), group);
            bodyBuilder.addAllUser(getUsersInGroup(group));
        } else {
            getExtendedContext().leave(ctx.channel(), group);
        }
        Message resp = ProtocolUtil
                .buildResponse(msg)
                .setStatus(Message.StatusCode.SUCCESS)
                .setGroupResp(bodyBuilder)
                .build();
        ctx.channel().writeAndFlush(resp);
    }

}
