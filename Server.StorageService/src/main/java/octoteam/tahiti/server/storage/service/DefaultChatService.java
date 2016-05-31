package octoteam.tahiti.server.storage.service;

import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.server.shared.model.Chat;
import octoteam.tahiti.server.storage.repository.ChatRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class DefaultChatService extends UnicastRemoteObject implements IStorageServiceProvider {

    private ChatRepository chatRepository;

    public DefaultChatService(ChatRepository chatRepository) throws RemoteException {
        super();
        this.chatRepository = chatRepository;
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
