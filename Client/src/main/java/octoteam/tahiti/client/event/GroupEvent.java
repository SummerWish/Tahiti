package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;
import octoteam.tahiti.shared.event.BaseEvent;

public class GroupEvent extends BaseEvent {

    private String group;

    private String username;

    private boolean isLeave;

    public GroupEvent(String group, String username, boolean isLeave) {
        this.group = group;
        this.username = username;
        this.isLeave = isLeave;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLeave() {
        return isLeave;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("group", group)
                .add("username", username)
                .add("isLeave", isLeave)
                .toString();
    }
}
