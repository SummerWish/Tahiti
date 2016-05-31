package octoteam.tahiti.server.storage;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.RegistryConfig;
import octoteam.tahiti.server.storage.repository.ChatRepository;
import octoteam.tahiti.server.storage.repository.DatabaseChatRepository;
import octoteam.tahiti.server.storage.service.DefaultChatService;
import wheellllll.config.Config;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Console {

    public static void main(String[] args) throws Exception {
        Config.setConfigName("./resource/tahiti_server.conf");
        Config config = Config.getConfig();

        ConnectionSource connectionSource = new JdbcConnectionSource(config.getString("database"));
        ChatRepository chatRepository = new DatabaseChatRepository(connectionSource);
        IStorageServiceProvider chatService = new DefaultChatService(chatRepository);

        try {
            LocateRegistry.createRegistry(RegistryConfig.StorageServiceBindPort);
            Naming.rebind(
                    "rmi://" + RegistryConfig.StorageServiceBindIp + ":" + RegistryConfig.StorageServiceBindPort + "/StorageService",
                    chatService
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}