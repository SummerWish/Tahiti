package octoteam.tahiti.server.model;

import com.google.common.base.MoreObjects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messages")
public class Message {

    /**
     * 消息唯一 id
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * 所属组
     */
    @DatabaseField(canBeNull = false, indexName = "group_sendat_idx")
    private String groupId;

    /**
     * 发送时间
     */
    @DatabaseField(canBeNull = false, indexName = "group_sendat_idx")
    private long sendAt;

    /**
     * 消息内容
     */
    @DatabaseField(canBeNull = false)
    private String content;

    /**
     * 发送者 UID
     */
    @DatabaseField(canBeNull = false)
    private int userId;

    public Message() {

    }

    public Message(int userId, String groupId, String content, long sendAt) {
        this.userId = userId;
        this.groupId = groupId;
        this.content = content;
        this.sendAt = sendAt;
    }

    public void assignId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getSendAt() {
        return sendAt;
    }

    public void setSendAt(long sendAt) {
        this.sendAt = sendAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("groupId", groupId)
                .add("content", content)
                .add("userId", userId)
                .add("sendAt", sendAt)
                .toString();
    }
}
