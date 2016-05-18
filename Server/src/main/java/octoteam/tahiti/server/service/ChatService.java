package octoteam.tahiti.server.service;

import octoteam.tahiti.server.model.Chat;
import octoteam.tahiti.server.repository.ChatRepository;

import java.util.List;

public interface ChatService {

    ChatRepository getChatRepository();

    List<Chat> getGroupChatsSince(String groupId, long since);

    Chat addChat(int UID, String username, String groupId, String content);

}
