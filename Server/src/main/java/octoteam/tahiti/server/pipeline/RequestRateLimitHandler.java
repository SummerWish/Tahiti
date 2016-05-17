package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.event.RateLimitExceededEvent;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.ExtendedContext;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;
import wheellllll.license.License;

import java.util.concurrent.Callable;

/**
 * 该模块可以限制管道中的下行消息. 若下行消息超过了限制, 会产生 RateLimitExceededEvent 事件,
 * 并产生状态为 LIMIT_EXCEEDED 的上行消息.
 * 超出限制的消息会被丢弃.
 */
@ChannelHandler.Sharable
public class RequestRateLimitHandler extends MessageHandler {

    private final Message.ServiceCode serviceCode;
    private final String name;
    private final String sessionKey;
    private final Callable<License> quotaLimiterFactory;

    /**
     * @param serviceCode 要限制的消息类别, 只有这个参数指定的消息会被限制
     * @param name        产生的事件中所包含的名称
     * @param factory     一个返回新限流器实例的 Callable 对象, 将会使用该限流器进行限流
     */
    public RequestRateLimitHandler(
            ExtendedContext extendedContext,
            Message.ServiceCode serviceCode,
            String name,
            Callable<License> factory
    ) {
        super(extendedContext);
        this.serviceCode = serviceCode;
        this.name = name;
        this.sessionKey = "limiter_" + name;
        this.quotaLimiterFactory = factory;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getDirection() != Message.DirectionCode.REQUEST || msg.getService() != serviceCode) {
            ctx.fireChannelRead(msg);
            return;
        }
        License quotaLimiter = (License) PipelineHelper.getSession(ctx).get(sessionKey);
        if (quotaLimiter == null) {
            quotaLimiter = this.quotaLimiterFactory.call();
            PipelineHelper.getSession(ctx).put(sessionKey, quotaLimiter);
        }
        if (quotaLimiter.use() == License.Availability.AVAILABLE) {
            ctx.fireChannelRead(msg);
        } else {
            RateLimitExceededEvent evt = new RateLimitExceededEvent(name);
            ctx.fireUserEventTriggered(evt);
            ctx.writeAndFlush(buildExceededMsg(msg));
        }
    }

    /**
     * @param req 超出限额的消息
     * @return message 状态为 LIMIT_EXCEEDED 的下行消息
     */
    private Message buildExceededMsg(Message req) {
        Message.Builder resp = ProtocolUtil
                .buildResponse(req)
                .setStatus(Message.StatusCode.LIMIT_EXCEEDED);
        return resp.build();
    }

}
