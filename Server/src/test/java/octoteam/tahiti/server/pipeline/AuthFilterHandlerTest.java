package octoteam.tahiti.server.pipeline;

import io.netty.channel.embedded.EmbeddedChannel;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.session.Credential;
import octoteam.tahiti.server.session.PipelineHelper;
import octoteam.tahiti.shared.netty.ExtendedContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthFilterHandlerTest {

    @Test
    public void testFilteringNotAuthenticatedEvent() {

        // AuthFilterHandler should filter events if user is not authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));

        Message chatEvent = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.PUSH)
                .setService(Message.ServiceCode.CHAT_BROADCAST_PUSH)
                .build();

        channel.writeOutbound(chatEvent);
        channel.finish();

        assertNull(channel.readInbound());
        assertNull(channel.readOutbound());

    }

    @Test
    public void testPassAuthenticatedEvent() {

        // AuthFilterHandler should pass events to the next handler when user is authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));
        PipelineHelper.getSession(channel).put("credential", new Credential(1, "foo", true));

        Message chatEvent = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.PUSH)
                .setService(Message.ServiceCode.CHAT_BROADCAST_PUSH)
                .build();

        channel.writeOutbound(chatEvent);
        channel.finish();

        assertEquals(chatEvent, channel.readOutbound());
        assertNull(channel.readInbound());

    }

    @Test
    public void testFilterAnonymousEvent() {

        // AuthFilterHandler should filter events when user is a guest

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));
        PipelineHelper.getSession(channel).put("credential", new Credential(1, "guest", false));

        Message chatEvent = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.PUSH)
                .setService(Message.ServiceCode.CHAT_BROADCAST_PUSH)
                .build();

        channel.writeOutbound(chatEvent);
        channel.finish();

        assertNull(channel.readInbound());
        assertNull(channel.readOutbound());

    }

    @Test
    public void testPassNotAuthenticatedResponse() {

        // AuthFilterHandler should pass responses to the next handler even if user is not authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));

        Message someResponse = Message.newBuilder()
                .setSeqId(345)
                .setDirection(Message.DirectionCode.RESPONSE)
                .build();

        channel.writeOutbound(someResponse);
        channel.finish();

        assertEquals(someResponse, channel.readOutbound());
        assertNull(channel.readInbound());

    }

    @Test
    public void testNotAuthenticatedAck() {

        // AuthFilterHandler should pass acks to the next handler even if user is not authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));

        Message someAck = Message.newBuilder()
                .setSeqId(321)
                .setDirection(Message.DirectionCode.ACK)
                .build();

        channel.writeInbound(someAck);
        channel.finish();

        assertEquals(someAck, channel.readInbound());
        assertNull(channel.readOutbound());

    }

    @Test
    public void testNotAuthenticatedRequest() {

        // AuthFilterHandler should filter requests if user is not authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));

        Message chatRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_PUBLISH_REQUEST)
                .build();

        channel.writeInbound(chatRequest);
        channel.finish();

        assertNull(channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.NOT_AUTHENTICATED, responseMsg.getStatus());

    }

    @Test
    public void testAuthenticatedRequest() {

        // AuthFilterHandler should pass requests to the next handler if user is authenticated

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));
        PipelineHelper.getSession(channel).put("credential", new Credential(1, "foo", true));

        Message chatRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_PUBLISH_REQUEST)
                .build();

        channel.writeInbound(chatRequest);
        channel.finish();

        assertEquals(chatRequest, channel.readInbound());
        assertNull(channel.readOutbound());

    }

    @Test
    public void testAnonymousRequest() {

        // AuthFilterHandler should filter requests if user is a guest

        EmbeddedChannel channel = new EmbeddedChannel(new AuthFilterHandler(
                new ExtendedContext()
        ));
        PipelineHelper.getSession(channel).put("credential", new Credential(1, "guest", false));

        Message chatRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_PUBLISH_REQUEST)
                .build();

        channel.writeInbound(chatRequest);
        channel.finish();

        assertNull(channel.readInbound());

        Object response = channel.readOutbound();
        assertTrue(response instanceof Message);

        Message responseMsg = (Message) response;
        assertEquals(123, responseMsg.getSeqId());
        assertEquals(Message.DirectionCode.RESPONSE, responseMsg.getDirection());
        assertEquals(Message.StatusCode.NOT_AUTHENTICATED, responseMsg.getStatus());

    }

}