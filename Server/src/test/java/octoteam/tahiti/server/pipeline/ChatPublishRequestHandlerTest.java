package octoteam.tahiti.server.pipeline;

import io.netty.channel.embedded.EmbeddedChannel;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatPublishReqBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.service.DefaultChatService;
import octoteam.tahiti.shared.netty.ExtendedContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatPublishRequestHandlerTest {

    @Test
    public void testMessageRequest() {

        // ChatPublishRequestHandler should handle CHAT_PUBLISH_REQUEST

        EmbeddedChannel channel = new EmbeddedChannel(new ChatPublishRequestHandler(
                new ExtendedContext(),
                new DefaultChatService(new DummyChatRepository())
        ));

        Message msgRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_PUBLISH_REQUEST)
                .setChatPublishReq(ChatPublishReqBody.newBuilder()
                        .setPayload("foo")
                        .setTimestamp(333)
                )
                .build();

        channel.writeInbound(msgRequest);
        channel.finish();

        assertEquals(msgRequest, channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertTrue(responseMsg.isInitialized());
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.SUCCESS, responseMsg.getStatus());

    }

    @Test
    public void testOtherRequest() {

        // ChatPublishRequestHandler should not handle other request

        EmbeddedChannel channel = new EmbeddedChannel(new ChatPublishRequestHandler(
                new ExtendedContext(),
                new DefaultChatService(new DummyChatRepository())
        ));

        Message otherRequest = Message.newBuilder()
                .setSeqId(212)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.PING_REQUEST)
                .build();

        channel.writeInbound(otherRequest);
        channel.finish();

        assertEquals(otherRequest, channel.readInbound());
        assertNull(channel.readOutbound());

    }

}