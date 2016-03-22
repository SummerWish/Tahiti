package octoteam.tahiti.server;

import com.google.common.eventbus.Subscribe;
import octoteam.tahiti.server.configuration.ServerConfiguration;
import octoteam.tahiti.server.event.BaseEvent;
import org.yaml.snakeyaml.Yaml;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.InputStream;

public class Console {

    public static void main(String[] args) throws Exception {

        Yaml yaml = new Yaml();
        InputStream in = Console.class.getClass().getResourceAsStream("/tahiti_server.yaml");
        ServerConfiguration config = yaml.loadAs(in, ServerConfiguration.class);

        TahitiServer server = new TahitiServer(config);
        server.getEventBus().register(new Object() {
            @Subscribe
            public void listenAllEvent(BaseEvent event) {
                System.out.println(event);
            }
        });

        server.run();

    }

}
