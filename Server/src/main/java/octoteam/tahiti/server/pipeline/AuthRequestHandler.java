package octoteam.tahiti.server.pipeline;

import io.netty.channel.ChannelHandlerContext;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInReqBody;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInRespBody;
import octoteam.tahiti.server.Session;
import octoteam.tahiti.server.TahitiServer;
import octoteam.tahiti.server.configuration.AccountConfiguration;
import octoteam.tahiti.server.event.LoginAttemptEvent;
import octoteam.tahiti.server.event.MessageEvent;

import java.util.List;
import java.util.UUID;

public class AuthRequestHandler extends PipelineMessageHandler {

    // TODO: Replace with database based
    List<AccountConfiguration> accounts;

    public AuthRequestHandler(TahitiServer server, List<AccountConfiguration> accounts) {
        super(server);
        this.accounts = accounts;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {

        Boolean authenticated = ctx.channel().attr(TahitiServer.ATTR_KEY_SESSION).get() != null;
        this.server.getEventBus().post(new MessageEvent(authenticated, msg));

        // Already authenticated: pass everything to next handler
        if (authenticated) {
            this.server.getEventBus().post(new MessageEvent(true, msg));
            ctx.fireChannelRead(msg);
            return;
        }

        // Only allows USER_SIGN_IN_REQUEST, otherwise, emit NOT_AUTHENTICATED
        if (msg.getService() != Message.ServiceCode.USER_SIGN_IN_REQUEST) {
            Message.Builder resp = Message
                    .newBuilder()
                    .setSeqId(msg.getSeqId())
                    .setDirection(Message.DirectionCode.RESPONSE)
                    .setStatus(Message.StatusCode.NOT_AUTHENTICATED);
            ctx.writeAndFlush(resp.build());
            return;
        }

        UserSignInReqBody body = msg.getUserSignInReq();
        Message.Builder resp = Message
                .newBuilder()
                .setSeqId(msg.getSeqId())
                .setDirection(Message.DirectionCode.RESPONSE);

        Boolean found = false;
        for (AccountConfiguration account : accounts) {
            if (account.getUsername().equals(body.getUsername())) {
                if (account.getPassword().equals(body.getPassword())) {
                    // correct username, correct password
                    Session sess = new Session();
                    sess.setSessionId(UUID.randomUUID().toString());
                    sess.setUsername(account.getUsername());
                    ctx.channel().attr(TahitiServer.ATTR_KEY_SESSION).set(sess);

                    resp
                            .setStatus(Message.StatusCode.SUCCESS)
                            .setUserSignInResp(UserSignInRespBody
                                    .newBuilder()
                                    .setSessionId(sess.getSessionId())
                                    .setClientId(body.getUsername()) // TODO
                            );
                } else {
                    // correct username, incorrect password
                    resp.setStatus(Message.StatusCode.PASSWORD_INCORRECT);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            resp.setStatus(Message.StatusCode.USERNAME_NOT_FOUND);
        }

        this.server.getEventBus().post(new LoginAttemptEvent(
                resp.getStatus() == Message.StatusCode.SUCCESS,
                body.getUsername()
        ));
        ctx.writeAndFlush(resp.build());
    }

}
