package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import octoteam.tahiti.client.TahitiClient;
import octoteam.tahiti.client.event.MessageEvent;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

public class ResponseHandler extends SimpleChannelInboundHandler<Message> {

    private TahitiClient client;

    public ResponseHandler(TahitiClient client) {
        this.client = client;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        client.resolveCallback(msg.getSeqId(), msg);
        client.getEventBus().post(new MessageEvent(msg));
    }

}
