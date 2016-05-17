package octoteam.tahiti.server.pipeline;

import octoteam.tahiti.server.model.Message;
import octoteam.tahiti.server.repository.MessageRepository;

import java.util.List;

public class DummyMessageRepository implements MessageRepository {

    @Override
    public Message addMessage(Message msg) {
        return msg;
    }

    @Override
    public List<Message> getGroupMessagesSince(String groupId, long since) {
        return null;
    }

}
