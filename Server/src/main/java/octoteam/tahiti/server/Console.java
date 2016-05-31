package octoteam.tahiti.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.archiver.RollingArchivePacker;
import octoteam.tahiti.server.shared.microservice.rmi.IAuthServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.IStorageServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.RegistryConfig;
import octoteam.tahiti.shared.event.BaseEvent;
import octoteam.tahiti.shared.logger.ReceivedMessageLogger;
import wheellllll.config.Config;

import java.rmi.Naming;


public class Console {

    public static void main(String[] args) throws Exception {

        // Load configuration
        Config.setConfigName("./resource/tahiti_server.conf");
        Config config = Config.getConfig();

        IAuthServiceProvider accountService = (IAuthServiceProvider) Naming.lookup("rmi://" + RegistryConfig.AuthServiceIp + ":" + RegistryConfig.AuthServicePort + "/AuthService");
        IStorageServiceProvider chatService = (IStorageServiceProvider) Naming.lookup("rmi://" + RegistryConfig.StorageServiceIp + ":" + RegistryConfig.StorageServicePort + "/StorageService");

        // Create event bus
        EventBus serverEventBus = new EventBus();
        serverEventBus.register(new IndexLogger(
                config.getString("log.logDir"),
                config.getString("log.logFile"),
                config.getString("log.archiveDir"),
                config.getString("log.archiveFile")
        ));
        serverEventBus.register(new ReceivedMessageLogger(
                config.getString("log.messageMaxFileSize"),
                config.getString("log.messageTotalSizeCap"),
                config.getString("log.messageDirFile")
        ));
        serverEventBus.register(new Object() {
            @Subscribe
            public void listenAllEvent(BaseEvent event) {
                System.out.println(event);
            }
        });

        // Start daily packing
        String[] dailySourceFilePatterns = new String[]{
                config.getString("log.archiveDir") + "/" + config.getString("log.archiveFile") + "%d{yyyy_MM_dd}.zip",
                config.getString("log.archiveDir") + "/" + "tahiti_client_%d{yyyy_MM_dd}.zip",
                config.getString("log.messageDirFile"),
                "resource/tahiti/message/client_message_%d{yyyy_MM_dd}.log"
        };
        String dailyDestFilePattern = config.getString("log.dailyPackDestPattern");
        new RollingArchivePacker(dailySourceFilePatterns, dailyDestFilePattern).start();

        // Start weekly packing
        String[] weeklySourceFilePatterns = new String[]{dailyDestFilePattern};
        String weeklyDestFilePattern = config.getString("log.weeklyPackDestPattern");
        new RollingArchivePacker(weeklySourceFilePatterns, weeklyDestFilePattern).start();

        // Create server
        TahitiServer server = new TahitiServer(
                config,
                serverEventBus,
                accountService,
                chatService
        );

        server.run();

    }

}
