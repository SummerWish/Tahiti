package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.TahitiServer;

public class FinalHandler extends PipelineMessageHandler {

    public FinalHandler(TahitiServer server) {
        super(server);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        // Not handled message here
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof DecoderException)) {
            cause.printStackTrace();
        }
        ctx.close();
    }

}
