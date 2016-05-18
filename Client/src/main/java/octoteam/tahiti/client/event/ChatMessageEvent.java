package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.shared.event.BaseEvent;

/**
 * @deprecated 其他用户发送消息事件
 */
public class ChatMessageEvent extends BaseEvent {

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

    public ChatMessageEvent(String payload, String username, long timestamp) {
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
