package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;

public class UIOnSendCommandEvent extends UIEvent {

    private String payload;

    public UIOnSendCommandEvent(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("payload", payload)
                .toString();
    }

}
