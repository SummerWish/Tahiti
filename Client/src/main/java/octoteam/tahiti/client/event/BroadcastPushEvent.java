package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;

public class BroadcastPushEvent {

    private String groupId;

    public BroadcastPushEvent(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupId", groupId)
                .toString();
    }

}
