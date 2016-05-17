package octoteam.tahiti.server.service;

import octoteam.tahiti.server.model.Message;
import octoteam.tahiti.server.repository.MessageRepository;

import java.util.Date;
import java.util.List;

public class DefaultMessageService implements MessageService {

    private MessageRepository messageRepository;

    public DefaultMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    @Override
    public List<Message> getGroupMessagesSince(String groupId, long since) {
        return messageRepository.getGroupMessagesSince(groupId, since);
    }

    @Override
    public Message addMessage(int UID, String groupId, String content) {
        return messageRepository.addMessage(new Message(
                UID,
                groupId,
                content,
                new Date().getTime()
        ));
    }

}
