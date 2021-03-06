package octoteam.tahiti.client;

import com.google.common.eventbus.EventBus;
import octoteam.tahiti.client.ui.Reactor;
import octoteam.tahiti.client.ui.Renderer;
import octoteam.tahiti.shared.logger.ReceivedMessageLogger;
import wheellllll.config.Config;

import java.nio.file.Paths;

public class Console {

    public static void main(String[] args) throws Exception {

        // Load configuration
        Config.setConfigName(Paths.get(Console.class.getClass().getResource("/tahiti_client.conf").toURI()).toString());
        Config config = Config.getConfig();

        NaiveDb clientDb = new NaiveDb("resource/client.json");

        // Create event bus
        EventBus clientEventBus = new EventBus();

        TahitiClient client = new TahitiClient(config, clientEventBus);
        Renderer renderer = new Renderer(clientEventBus);
        Reactor reactor = new Reactor(clientDb, client, renderer);

        clientEventBus.register(reactor);
        clientEventBus.register(new IndexLogger(
                config.getString("log.logDir"),
                config.getString("log.logFile"),
                config.getString("log.archiveDir"),
                config.getString("log.archiveFile")
        ));
        clientEventBus.register(new ReceivedMessageLogger(
                config.getString("log.messageMaxFileSize"),
                config.getString("log.messageTotalSizeCap"),
                config.getString("log.messageDirFile")
        ));

        renderer.actionShowLoginDialog();

    }

}
