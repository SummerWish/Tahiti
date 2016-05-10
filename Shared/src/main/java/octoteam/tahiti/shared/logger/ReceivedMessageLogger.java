package octoteam.tahiti.shared.logger;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.google.common.eventbus.Subscribe;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import octoteam.tahiti.shared.event.MessageReceivedEvent;

public class ReceivedMessageLogger {

    /**
     * 日志生成器
     */
    private Logger txtLogger;

    private final static String maxFileSize = "100MB";

    private final static String totalSizeCap = "10GB";

    private final static String txtFileNamePattern = "received_messages_%d{yyyy_MM_dd}.%i.log";

    private final static String pattern = "%d - Received Message:%n%msg%n%n";

    public ReceivedMessageLogger() {
        this(maxFileSize, totalSizeCap, txtFileNamePattern);
    }

    public ReceivedMessageLogger(String maxFileSize, String totalSizeCap, String txtFileNamePattern) {
        this(maxFileSize, totalSizeCap, txtFileNamePattern, pattern);
    }

    public ReceivedMessageLogger(String maxFileSize, String totalSizeCap, String txtFileNamePattern, String pattern) {

        LoggerContext context = new LoggerContext();

        // init txt logger
        RollingFileAppender txtFileAppender = getAppender(context, pattern);
        SizeAndTimeBasedRollingPolicy txtPolicy = new SizeAndTimeBasedRollingPolicy();
        txtPolicy.setContext(context);
        txtPolicy.setParent(txtFileAppender);
        txtPolicy.setFileNamePattern(txtFileNamePattern);
        txtPolicy.setMaxFileSize(maxFileSize);
        txtPolicy.setTotalSizeCap(FileSize.valueOf(totalSizeCap));

        txtFileAppender.setRollingPolicy(txtPolicy);

        txtPolicy.start();
        txtFileAppender.start();

        txtLogger = context.getLogger(ReceivedMessageLogger.class);
        txtLogger.setAdditive(false);
        txtLogger.addAppender(txtFileAppender);
    }

    private RollingFileAppender getAppender(LoggerContext loggerContext, String pattern) {
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(loggerContext);
        fileAppender.setEncoder(getEncoder(loggerContext, pattern));
        return fileAppender;
    }

    private PatternLayoutEncoder getEncoder(LoggerContext loggerContext, String pattern) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setImmediateFlush(true);
        encoder.setPattern(pattern);
        encoder.start();
        return encoder;
    }

    @Subscribe
    public void log(MessageReceivedEvent event) {
        txtLogger.info(event.toString());
    }
}
