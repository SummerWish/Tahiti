package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.TahitiServer;
import octoteam.tahiti.server.event.RawMessageEvent;

public class RawHandler extends PipelineMessageHandler {

    public RawHandler(TahitiServer server) {
        super(server);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        this.server.getEventBus().post(new RawMessageEvent(msg));
        ctx.fireChannelRead(msg);
    }

}
