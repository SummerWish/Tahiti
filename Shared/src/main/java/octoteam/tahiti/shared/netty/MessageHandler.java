package octoteam.tahiti.shared.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

/**
 * 处理消息（Message）的收发，所有消息处理队列中的 Handler 如果想处理消息类型（Message）的数据都应该继承这个类
 */
public abstract class MessageHandler extends ChannelDuplexHandler {

    private ExtendedContext extendedContext;

    public MessageHandler(ExtendedContext extendedContext) {
        this.extendedContext = extendedContext;
    }

    protected ExtendedContext getExtendedContext() {
        return extendedContext;
    }

    @Override
    final public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Message) {
            messageReceived(ctx, (Message) msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 当从外部收到消息时被调用，默认是向消息处理队列中的下一 Handler 传递消息，子类可以通过覆写这个方法实现自定义的消息处理行为
     *
     * @param ctx 当前 Handler 所处的队列环境，可向相同环境内的 Handler 传递消息
     * @param msg 收到的消息 （Message）
     * @throws Exception
     */
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    final public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Message) {
            messageSent(ctx, (Message) msg, promise);
        } else {
            ctx.write(msg, promise);
        }
    }

    /**
     * 当向外部发送消息时被调用，默认是向消息处理队列中的下一 Handler 传递消息，子类可以通过覆写这个方法实现自定义的消息处理行为
     *
     * @param ctx     当前 Handler 所处的队列环境，可向相同环境内的 Handler 传递消息
     * @param msg     发送的消息 （Message）
     * @param promise 消息成功发送的 Promise
     * @throws Exception
     */
    protected void messageSent(ChannelHandlerContext ctx, Message msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

}
