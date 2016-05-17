package octoteam.tahiti.server.pipeline;

import io.netty.channel.embedded.EmbeddedChannel;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.UserSignInReqBody;
import octoteam.tahiti.server.model.Account;
import octoteam.tahiti.server.repository.MemoryAccountRepository;
import octoteam.tahiti.server.service.AccountService;
import octoteam.tahiti.server.service.DefaultAccountService;
import octoteam.tahiti.shared.netty.ExtendedContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthRequestHandlerTest {

    private MemoryAccountRepository repository;
    private AccountService accountService;

    @Before
    public void initialize() {
        repository = new MemoryAccountRepository();
        accountService = new DefaultAccountService(repository);

        repository.createAccount(new Account(
                "testUser",
                "password123"
        ));    // ID = 1
    }

    @Test
    public void testUsernameNotFoundSignInRequest() {

        // AuthRequestHandler should handle username_not_found login request

        EmbeddedChannel channel = new EmbeddedChannel(new AuthRequestHandler(
                new ExtendedContext(),
                accountService
        ));

        Message loginRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_SIGN_IN_REQUEST)
                .setUserSignInReq(UserSignInReqBody.newBuilder()
                        .setUsername("notfound")
                        .setPassword("123")
                )
                .build();

        channel.writeInbound(loginRequest);
        channel.finish();

        assertEquals(loginRequest, channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.USERNAME_NOT_FOUND, responseMsg.getStatus());

    }

    @Test
    public void testWrongPasswordSignInRequest() {

        // AuthRequestHandler should handle wrong_password login request

        EmbeddedChannel channel = new EmbeddedChannel(new AuthRequestHandler(
                new ExtendedContext(),
                accountService
        ));

        Message loginRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_SIGN_IN_REQUEST)
                .setUserSignInReq(UserSignInReqBody.newBuilder()
                        .setUsername("testUser")
                        .setPassword("wrongpass")
                )
                .build();

        channel.writeInbound(loginRequest);
        channel.finish();

        assertEquals(loginRequest, channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.PASSWORD_INCORRECT, responseMsg.getStatus());

    }

    @Test
    public void testSuccessSignInRequest() {

        // AuthRequestHandler should handle success login request

        EmbeddedChannel channel = new EmbeddedChannel(new AuthRequestHandler(
                new ExtendedContext(),
                accountService
        ));

        Message loginRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_SIGN_IN_REQUEST)
                .setUserSignInReq(UserSignInReqBody.newBuilder()
                        .setUsername("testUser")
                        .setPassword("password123")
                )
                .build();

        channel.writeInbound(loginRequest);
        channel.finish();

        assertEquals(loginRequest, channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.SUCCESS, responseMsg.getStatus());
        assertEquals(Message.BodyCase.USERSIGNINRESP, responseMsg.getBodyCase());
        assertEquals(1, responseMsg.getUserSignInResp().getUID());

    }

}