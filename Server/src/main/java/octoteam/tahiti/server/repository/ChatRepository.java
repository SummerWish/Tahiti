package octoteam.tahiti.server.repository;

import octoteam.tahiti.server.model.Chat;

import java.util.List;

public interface ChatRepository {

    Chat addChat(Chat msg);

    List<Chat> getGroupChatsSince(String groupId, long since);

}
