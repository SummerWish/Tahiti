package octoteam.tahiti.shared.archive;

import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArchiveManager {
    public static void archive() {
        ScheduledExecutorService dailyArchiveService = Executors.newScheduledThreadPool(1);
        dailyArchiveService.scheduleAtFixedRate(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String date = sdf.format(new Date());
            ZipUtil.pack(new File("resource/tahiti_archive"), new File("resource/daily/tahiti_" + date + ".zip"));
            try {
                FileUtils.cleanDirectory(new File("resource/tahiti_archive"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 120, 86400, TimeUnit.SECONDS);

        ScheduledExecutorService weeklyArchiveService = Executors.newScheduledThreadPool(1);
        weeklyArchiveService.scheduleAtFixedRate(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String date;
            for (int i = 1; i < 8; i++) {
                calendar.add(Calendar.DATE, -i);
                date = sdf.format(calendar.getTime());
                ZipUtil.unpack(
                        new File("resource/daily/tahiti_" + date + ".zip"),
                        new File("resource/weekly/tahiti_" + date)
                );

            }
        }, 120, 7*24*60*60, TimeUnit.SECONDS);
    }
}
