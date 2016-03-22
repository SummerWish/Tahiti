package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;

public class UIOnSendCommandEvent extends UIEvent {

    private String message;

    public UIOnSendCommandEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }

}
