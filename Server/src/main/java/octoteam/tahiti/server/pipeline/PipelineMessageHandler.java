package octoteam.tahiti.server.pipeline;

import io.netty.channel.SimpleChannelInboundHandler;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.TahitiServer;

public abstract class PipelineMessageHandler extends SimpleChannelInboundHandler<Message> {

    protected final TahitiServer server;

    public PipelineMessageHandler(TahitiServer server) {
        this.server = server;
    }

}
