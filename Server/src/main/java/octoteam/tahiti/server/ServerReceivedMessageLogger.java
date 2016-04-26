package octoteam.tahiti.server;

import octoteam.tahiti.shared.logger.ReceivedMessageLogger;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerReceivedMessageLogger extends ReceivedMessageLogger{

    public ServerReceivedMessageLogger(String tahitiDir, String messageDirFile, String archiveDir) {
        super(messageDirFile);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
                    String date = sdf.format(new Date());
                    ZipUtil.pack(new File(tahitiDir), new File(archiveDir + "/tahiti_" + date + ".zip"));
                },
                120,
                86400,
                TimeUnit.SECONDS
        );
    }
}
