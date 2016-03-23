package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

public class MessageEvent extends BaseEvent {

    Message message;

    public MessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
    
}
