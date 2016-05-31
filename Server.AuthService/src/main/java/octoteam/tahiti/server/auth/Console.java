package octoteam.tahiti.server.auth;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import octoteam.tahiti.server.auth.repository.AccountRepository;
import octoteam.tahiti.server.auth.repository.DatabaseAccountRepository;
import octoteam.tahiti.server.auth.service.DefaultAccountService;
import octoteam.tahiti.server.shared.microservice.rmi.IAuthServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.RegistryConfig;
import octoteam.tahiti.server.shared.model.Account;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import wheellllll.config.Config;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Console {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("a", "add", false, "Add user");
        options.addOption("u", "username", true, "Username");
        options.addOption("p", "password", true, "Password");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Config.setConfigName("./resource/tahiti_server.conf");
        Config config = Config.getConfig();

        ConnectionSource connectionSource = new JdbcConnectionSource(config.getString("database"));
        AccountRepository accountRepository = new DatabaseAccountRepository(connectionSource);
        IAuthServiceProvider accountService = new DefaultAccountService(accountRepository);

        if (cmd.hasOption("a")) {
            Account account = accountRepository.lookupAccountByUsername(cmd.getOptionValue("u"));
            if (account != null) {
                System.out.println("Create user failed: Username exists.");
            } else {
                accountRepository.createAccount(new Account(
                        cmd.getOptionValue("u"),
                        cmd.getOptionValue("p")
                ));
                System.out.println("Create user succeeded.");
            }
            connectionSource.close();
        } else {
            try {
                LocateRegistry.createRegistry(RegistryConfig.AuthServiceBindPort);
                Naming.rebind(
                        "rmi://" + RegistryConfig.AuthServiceBindIp + ":" + RegistryConfig.AuthServiceBindPort + "/AuthService",
                        accountService
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}