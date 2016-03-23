package octoteam.tahiti.server;

import java.util.TimerTask;

public class LoggingPerMinTask extends TimerTask {

    @Override
    public void run() {
        Logging.loggingForServer();
    }
}
