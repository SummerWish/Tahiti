package octoteam.tahiti.server.service;

import octoteam.tahiti.server.model.Chat;
import octoteam.tahiti.server.repository.ChatRepository;

import java.util.Date;
import java.util.List;

public class DefaultChatService implements ChatService {

    private ChatRepository chatRepository;

    public DefaultChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatRepository getChatRepository() {
        return chatRepository;
    }

    @Override
    public List<Chat> getGroupChatsSince(String groupId, long since) {
        return chatRepository.getGroupChatsSince(groupId, since);
    }

    @Override
    public Chat addChat(int UID, String username, String groupId, String content) {
        return chatRepository.addChat(new Chat(
                UID,
                username,
                groupId,
                content,
                new Date().getTime()
        ));
    }

}
