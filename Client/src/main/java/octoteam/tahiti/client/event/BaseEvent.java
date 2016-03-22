package octoteam.tahiti.client.event;

import com.google.common.base.MoreObjects;

public class BaseEvent {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }

}
