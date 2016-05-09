package octoteam.tahiti.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.archiver.RollingArchivePacker;
import octoteam.tahiti.server.model.Account;
import octoteam.tahiti.server.repository.AccountRepository;
import octoteam.tahiti.server.repository.DatabaseAccountRepository;
import octoteam.tahiti.server.service.AccountService;
import octoteam.tahiti.server.service.DefaultAccountService;
import octoteam.tahiti.shared.event.BaseEvent;
import octoteam.tahiti.shared.logger.ReceivedMessageLogger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import wheellllll.config.Config;

import java.nio.file.Paths;


public class Console {

    public static void main(String[] args) throws Exception {

        // Commandline Options
        Options options = new Options();
        options.addOption("a", "add", false, "Add user");
        options.addOption("u", "username", true, "Username");
        options.addOption("p", "password", true, "Password");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Load configuration
        Config.setConfigName(Paths.get(Console.class.getClass().getResource("/tahiti_server.conf").toURI()).toString());
        Config config = Config.getConfig();

        // Open database connection
        ConnectionSource connectionSource = new JdbcConnectionSource(config.getString("database"));
        AccountRepository repository = new DatabaseAccountRepository(connectionSource);
        AccountService accountService = new DefaultAccountService(repository);

        if (cmd.hasOption("a")) {

            Account account = repository.lookupAccountByUsername(cmd.getOptionValue("u"));

            if (account != null) {
                System.out.println("Create user failed: Username exists.");
            } else {
                repository.createAccount(cmd.getOptionValue("u"), cmd.getOptionValue("p"));
                System.out.println("Create user succeeded.");
            }

            connectionSource.close();

        } else {
            // Create event bus
            EventBus serverEventBus = new EventBus();
            serverEventBus.register(new IndexLogger(
                    config.getString("log.logDir"),
                    config.getString("log.logFile"),
                    config.getString("log.archiveDir"),
                    config.getString("log.archiveFile")
            ));
            serverEventBus.register(new ReceivedMessageLogger(
                    config.getString("log.messageDirFile")
            ));
            serverEventBus.register(new Object() {
                @Subscribe
                public void listenAllEvent(BaseEvent event) {
                    System.out.println(event);
                }
            });

            // Initialize AES Encryptor
            Encryptor myEncryptor = new Encryptor(config.getString("archive.cipher"));

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
            TahitiServer server = new TahitiServer(config, serverEventBus, accountService);

            server.run();

        }
    }

}
