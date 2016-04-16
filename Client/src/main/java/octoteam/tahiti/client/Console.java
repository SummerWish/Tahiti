package octoteam.tahiti.client;

import com.google.common.eventbus.EventBus;
import octoteam.tahiti.client.configuration.ClientConfiguration;
import octoteam.tahiti.client.ui.Reactor;
import octoteam.tahiti.client.ui.Renderer;
import octoteam.tahiti.config.ConfigManager;
import octoteam.tahiti.config.loader.YamlAdapter;

import java.nio.file.Paths;

public class Console {

    public static void main(String[] args) throws Exception {

        // Load configuration
        ConfigManager configManager = new ConfigManager(new YamlAdapter(),
                "resource/tahiti_client.yaml",
                Paths.get(Console.class.getClass().getResource("/tahiti_client.yaml").toURI()).toString()
        );
        ClientConfiguration config = configManager.loadToBean(ClientConfiguration.class);

        // Create event bus
        EventBus clientEventBus = new EventBus();

        TahitiClient client = new TahitiClient(config, clientEventBus);
        Renderer renderer = new Renderer(clientEventBus);
        Reactor reactor = new Reactor(client, renderer);

        clientEventBus.register(reactor);
        clientEventBus.register(new IndexLogger(config.getLogFile(), 60));

        renderer.actionShowLoginDialog();

    }

}
