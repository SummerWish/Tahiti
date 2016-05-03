package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInReqBody;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInRespBody;
import octoteam.tahiti.server.event.LoginAttemptEvent;
import octoteam.tahiti.server.model.Account;
import octoteam.tahiti.server.service.AccountNotFoundException;
import octoteam.tahiti.server.service.AccountNotMatchException;
import octoteam.tahiti.server.service.AccountService;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.MessageHandler;
import octoteam.tahiti.shared.protocol.ProtocolUtil;

/**
 * 该模块处理下行的登录请求 (Request)，通过 `AccountService` 进行验证。
 * 无论成功或者失败，该模块都会发送该请求的响应 (Response) 消息。
 * 响应消息会处于三种状态中的一种：成功、用户名未找到、用户名或密码错误。
 * 对于成功的情况，该模块还会标记当前连接状态为已登录。
 */
@ChannelHandler.Sharable
public class AuthRequestHandler extends MessageHandler {

    private final AccountService accountService;

    /**
     * @param accountService 账户服务模块
     */
    public AuthRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) {
        if (msg.getService() != Message.ServiceCode.USER_SIGN_IN_REQUEST) {
            ctx.fireChannelRead(msg);
            return;
        }

        UserSignInReqBody body = msg.getUserSignInReq();
        Message.Builder resp = ProtocolUtil.buildResponse(msg);

        try {
            Account account = accountService.getMatchedAccount(body.getUsername(), body.getPassword());
            PipelineHelper.clearSession(ctx);
            PipelineHelper.getSession(ctx).put("credential", new Credential(account));
            resp
                    .setStatus(Message.StatusCode.SUCCESS)
                    .setUserSignInResp(UserSignInRespBody
                            .newBuilder()
                            .setUID(account.getId())
                    );
        } catch (AccountNotMatchException e) {
            resp.setStatus(Message.StatusCode.PASSWORD_INCORRECT);
        } catch (AccountNotFoundException e) {
            resp.setStatus(Message.StatusCode.USERNAME_NOT_FOUND);
        }

        ctx.fireUserEventTriggered(new LoginAttemptEvent(
                resp.getStatus() == Message.StatusCode.SUCCESS,
                body.getUsername()
        ));

        ctx.writeAndFlush(resp.build());
        ctx.fireChannelRead(msg);
    }

}
