package octoteam.tahiti.server.pipeline;

import octoteam.tahiti.server.model.Chat;
import octoteam.tahiti.server.repository.ChatRepository;

import java.util.List;

public class DummyChatRepository implements ChatRepository {

    @Override
    public Chat addChat(Chat msg) {
        return msg;
    }

    @Override
    public List<Chat> getGroupChatsSince(String groupId, long since) {
        return null;
    }

}
