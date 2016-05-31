package octoteam.tahiti.server.storage.repository;

import octoteam.tahiti.server.shared.model.Chat;

import java.util.List;

public interface ChatRepository {

    Chat addChat(Chat msg);

    List<Chat> getGroupChatsSince(String groupId, long since);

}
