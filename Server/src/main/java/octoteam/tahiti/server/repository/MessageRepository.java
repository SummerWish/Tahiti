package octoteam.tahiti.server.repository;

import octoteam.tahiti.server.model.Message;

import java.util.List;

public interface MessageRepository {

    Message addMessage(Message msg);

    List<Message> getGroupMessagesSince(String groupId, long since);

}
