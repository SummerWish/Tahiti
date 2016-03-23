package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.server.TahitiServer;

public class MessageRequestHandler extends PipelineMessageHandler {

    public MessageRequestHandler(TahitiServer server) {
        super(server);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SocketMessageProtos.Message msg) {
        if (msg.getService() != SocketMessageProtos.Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        SocketMessageProtos.Message.Builder resp = SocketMessageProtos.Message
                .newBuilder()
                .setSeqId(msg.getSeqId())
                .setDirection(SocketMessageProtos.Message.DirectionCode.RESPONSE)
                .setStatus(SocketMessageProtos.Message.StatusCode.SUCCESS);

        ctx.writeAndFlush(resp.build());

    }

}
