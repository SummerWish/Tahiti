package octoteam.tahiti.client.event;

import octoteam.tahiti.protocol.SocketMessageProtos.UserGroupingReqBody.Action;

public class UIOnGroupCommandEvent extends UIEvent {

    /**
     * Action.JOIN or Action.LEAVE
     */
    private Action action;

    /**
     * groupId
     */
    private String groupId;

    public UIOnGroupCommandEvent(String command) {
        this.action = command.split(" ")[0].equals("join") ? Action.JOIN : Action.LEAVE;
        this.groupId = command.split(" ")[1];
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
