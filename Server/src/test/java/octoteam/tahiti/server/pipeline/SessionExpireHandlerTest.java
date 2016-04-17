package octoteam.tahiti.server.pipeline;

import io.netty.channel.embedded.EmbeddedChannel;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;
import octoteam.tahiti.protocol.SocketMessageProtos.SessionExpiredPushBody;
//import octoteam.tahiti.quota.CapacityLimiter;
import wheellllll.license.License;
import octoteam.tahiti.server.event.RateLimitExceededEvent;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionExpireHandlerTest {

    /**
     * clear session once count of messages from client exceeded
     */

    @Test
    public void testSessionExpired() {
        EmbeddedChannel channel = new EmbeddedChannel(new RequestRateLimitHandler(
                Message.ServiceCode.PING_REQUEST,
                RateLimitExceededEvent.NAME_PER_SESSION,
                () -> new License(License.LicenseType.CAPACITY, 2)), new SessionExpireHandler());

        for (int i = 0; i < 3; i++) {
            Message pingRequest = Message.newBuilder()
                    .setSeqId(1)
                    .setDirection(Message.DirectionCode.REQUEST)
                    .setService(Message.ServiceCode.PING_REQUEST)
                    .build();
            channel.writeInbound(pingRequest);
            if (i == 2) {
                // only care about EVENT message
                Object response = channel.readOutbound();
                assertTrue(response instanceof Message);

                Message responseMsg = (Message) response;
                assertEquals(responseMsg.getDirection(), Message.DirectionCode.PUSH);
                assertEquals(responseMsg.getService(), Message.ServiceCode.SESSION_EXPIRED_PUSH);
                assertEquals(responseMsg.getSessionExpiredPush().getReason(),
                        SessionExpiredPushBody.Reason.EXPIRED);
            }
        }

    }

}