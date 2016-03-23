package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos;
import octoteam.tahiti.server.TahitiServer;

public class RateLimitHandler extends InboundMessageHandler {

    public RateLimitHandler(TahitiServer server) {
        super(server);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SocketMessageProtos.Message msg) {
        // TODO
        ctx.fireChannelRead(msg);
    }

}
