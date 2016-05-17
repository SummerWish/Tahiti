package octoteam.tahiti.client.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.client.CallbackRepository;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;

/**
 * 该模块处理服务端对请求的响应. 若发送对应请求时指定了回调函数,
 * 则会调用该回调函数, 实现回调式 Request - Response
 */
@ChannelHandler.Sharable
public class ResponseCallbackHandler extends MessageHandler {

    private CallbackRepository callbackRepository;

    public ResponseCallbackHandler(ExtendedContext extendedContext, CallbackRepository callbackRepository) {
        super(extendedContext);
        this.callbackRepository = callbackRepository;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getDirection() == Message.DirectionCode.RESPONSE) {
            callbackRepository.resolveCallback(msg.getSeqId(), msg);
        }
        ctx.fireChannelRead(msg);
    }

}
