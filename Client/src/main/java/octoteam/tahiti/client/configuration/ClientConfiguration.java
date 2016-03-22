package octoteam.tahiti.client.configuration;

import com.google.common.base.MoreObjects;

public class ClientConfiguration {

    private ChatServiceConfiguration chatService;

    public ChatServiceConfiguration getChatService() {
        return chatService;
    }

    public void setChatService(ChatServiceConfiguration chatService) {
        this.chatService = chatService;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("chatService", chatService)
                .toString();
    }
}
