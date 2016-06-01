package octoteam.tahiti.server.pipeline;

import io.netty.channel.*;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupOperation;
import octoteam.tahiti.protocol.SocketMessageProtos.GroupRespBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.User;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.server.shared.microservice.rmi.IGroupServiceProvider;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

@ChannelHandler.Sharable
public class GroupRequestHandler extends MessageHandler {

    private IGroupServiceProvider groupService;

    public GroupRequestHandler(ExtendedContext extendedContext, IGroupServiceProvider groupService) {
        super(extendedContext);
        this.groupService = groupService;
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

    private List<User> getUsersInGroup(String group) throws RemoteException {
        return groupService.getChannelsInGroup(group).stream()
                .map(cid -> getExtendedContext().lookupManagedChannels(cid))
                .map(ch -> getUserFromSession(ch).build())
                .collect(Collectors.toList());
    }

    private int writeToGroup(String group, Object msg) throws RemoteException {
        int count = 0;
        List<ChannelId> channels = groupService.getChannelsInGroup(group);
        for (ChannelId channelId : channels) {
            Channel ch = getExtendedContext().lookupManagedChannels(channelId);
            if (ch != null) {
                ch.writeAndFlush(msg);
                count++;
            }
        }
        return count;
    }

    private final ChannelFutureListener remover = future -> {
        ChannelId id = future.channel().id();
        List<String> groups = groupService.getJoinedGroups(id);
        for (String group : groups) {
            groupService.leaveGroup(id, group);
        }
    };

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel ch = ctx.channel();
        ch.closeFuture().addListener(remover);
        getExtendedContext().addToManagedChannels(ctx.channel());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws RemoteException {
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
        writeToGroup(group, push);

        // Join/leave group based on OP
        if (msg.getGroupReq().getOp() == GroupOperation.JOIN) {
            groupService.joinGroup(ctx.channel().id(), group);
            bodyBuilder.addAllUser(getUsersInGroup(group));
        } else {
            groupService.leaveGroup(ctx.channel().id(), group);
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
