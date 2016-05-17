package octoteam.tahiti.server.service;

import octoteam.tahiti.server.model.Message;
import octoteam.tahiti.server.repository.MessageRepository;

import java.util.List;

public interface MessageService {

    MessageRepository getMessageRepository();

    List<Message> getGroupMessagesSince(String groupId, long since);

    Message addMessage(int UID, String groupId, String content);

}
