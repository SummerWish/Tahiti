package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.shared.event.BaseEvent;

/**
 * 用户发送消息事件 (包括自己和其他人)
 */
public class ChatEvent extends BaseEvent {

    /**
     * 消息内容
     */
    private String payload;

    /**
     * 发送者用户名
     */
    private String username;

    /**
     * 发送时间
     */
    private long timestamp;

    public ChatEvent(String payload, String username, long timestamp) {
        this.payload = payload;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("payload", payload)
                .add("username", username)
                .add("timestamp", timestamp)
                .toString();
    }

}
