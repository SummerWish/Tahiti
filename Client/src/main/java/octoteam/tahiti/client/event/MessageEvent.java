package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.protocol.SocketMessageProtos.Message;

public class MessageEvent extends BaseEvent {

    Message msg;

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public MessageEvent(Message msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("msg", msg)
                .toString();
    }

}
