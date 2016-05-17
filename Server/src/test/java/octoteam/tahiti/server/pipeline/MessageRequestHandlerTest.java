package octoteam.tahiti.server.pipeline;

import io.netty.channel.embedded.EmbeddedChannel;
import octoteam.tahiti.protocol.SocketMessageProtos.ChatSendMessageReqBody;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.server.service.DefaultMessageService;
import octoteam.tahiti.shared.netty.ExtendedContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageRequestHandlerTest {

    @Test
    public void testMessageRequest() {

        // MessageRequestHandler should handle CHAT_SEND_MESSAGE_REQUEST

        EmbeddedChannel channel = new EmbeddedChannel(new MessageRequestHandler(
                new ExtendedContext(),
                new DefaultMessageService(new DummyMessageRepository())
        ));

        Message msgRequest = Message.newBuilder()
                .setSeqId(123)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST)
                .setChatSendMessageReq(ChatSendMessageReqBody.newBuilder()
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

        // MessageRequestHandler should not handle other request

        EmbeddedChannel channel = new EmbeddedChannel(new MessageRequestHandler(
                new ExtendedContext(),
                new DefaultMessageService(new DummyMessageRepository())
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

    /*
    // TODO: To be fixed
    @Test
    public void testMessageForward() {

        // MessageForwardHandler should construct CHAT_BROADCAST_EVENT message only when received CHAT_SEND_MESSAGE_REQUEST message

        EmbeddedChannel channelSender = new EmbeddedChannel(new NaiveChannelId(0), new MessageForwardHandler());
        EmbeddedChannel channelReceivers[] = new EmbeddedChannel[3];
        for (int i = 0; i < 3; ++i) {
            channelReceivers[i] = new EmbeddedChannel(new NaiveChannelId(i + 1), new MessageForwardHandler());
        }

        Message msgGroup1Req = Message.newBuilder()
                .setSeqId(1037)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_GROUPING_REQUEST)
                .setUserGroupingReq(SocketMessageProtos.UserGroupingReqBody.newBuilder()
                        .setAction(SocketMessageProtos.UserGroupingReqBody.Action.JOIN)
                        .setGroupId("1")
                ).build();

        Message msgGroup2Req = Message.newBuilder()
                .setSeqId(1037)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.USER_GROUPING_REQUEST)
                .setUserGroupingReq(SocketMessageProtos.UserGroupingReqBody.newBuilder()
                        .setAction(SocketMessageProtos.UserGroupingReqBody.Action.JOIN)
                        .setGroupId("1")
                ).build();

        Message msgRequest = Message.newBuilder()
                .setSeqId(1038)
                .setDirection(Message.DirectionCode.REQUEST)
                .setService(Message.ServiceCode.CHAT_SEND_MESSAGE_REQUEST)
                .setChatSendMessageReq(ChatSendMessageReqBody.newBuilder()
                        .setPayload("HELLO :P")
                        .setTimestamp(Calendar.getInstance().getTimeInMillis())
                ).build();

        // group
        channelSender.writeInbound(msgGroup1Req);
        channelReceivers[0].writeInbound(msgGroup1Req);
        channelReceivers[1].writeInbound(msgGroup2Req);

        channelSender.writeInbound(msgRequest);
        channelSender.finish();

        assertEquals(msgGroup1Req, channelSender.readInbound());
        assertNotNull(channelSender.readOutbound());


        for (int i = 0; i < 2; ++i) {
            channelReceivers[i].finish();

            assertNotNull(channelReceivers[i].readInbound());

            Queue<Object> msgs = channelReceivers[i].outboundMessages();
            assertTrue(msgs.size() == 2);
            Object response = msgs.poll();
            assertTrue(response instanceof Message);
            Message responseMsg = (Message) response;
            assertEquals(Message.ServiceCode.USER_GROUPING_REQUEST, responseMsg.getService());

            response = msgs.poll();
            assertTrue(response instanceof Message);

            responseMsg = (Message) response;
            assertEquals(Message.DirectionCode.PUSH, responseMsg.getDirection());
            assertEquals(Message.ServiceCode.CHAT_BROADCAST_PUSH, responseMsg.getService());
            assertEquals(Message.BodyCase.CHATBROADCASTPUSH, responseMsg.getBodyCase());
            assertEquals("HELLO :P", responseMsg.getChatBroadcastPush().getPayload());
            assertEquals("Guest", responseMsg.getChatBroadcastPush().getSenderUsername());
        }
        assertTrue(null == channelReceivers[2].readOutbound());
    }
    */

}