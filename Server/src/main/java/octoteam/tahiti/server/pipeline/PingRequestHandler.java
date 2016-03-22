package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.TahitiServer;

public class PingRequestHandler extends PipelineMessageHandler {

    public PingRequestHandler(TahitiServer server) {
        super(server);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.PING_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        Message.Builder resp = Message
                .newBuilder()
                .setSeqId(msg.getSeqId())
                .setDirection(Message.DirectionCode.RESPONSE)
                .setStatus(Message.StatusCode.SUCCESS)
                .setPingPong(msg.getPingPong());

        ctx.writeAndFlush(resp.build());
    }

}
