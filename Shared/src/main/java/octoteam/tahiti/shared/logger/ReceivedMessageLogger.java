package octoteam.tahiti.shared.logger;

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

    /**
     * 日志归档生成器
     */
    private Logger zipLogger;

    private final static String txtFileNamePattern = "received_messages_%d{yyyy_MM_dd}.log";

    private final static String zipFileNamePattern = "received_message_%d{yyyy_MM_dd}.zip";

    private final static String pattern = "%d - Received Message:%n%msg%n%n";

    public ReceivedMessageLogger() {
        this(txtFileNamePattern, zipFileNamePattern);
    }

    public ReceivedMessageLogger(String txtFileNamePattern, String zipFileNamePattern) {
        this(txtFileNamePattern, zipFileNamePattern, pattern);
    }

    public ReceivedMessageLogger(String txtFileNamePattern, String zipFileNamePattern, String pattern) {

        LoggerContext context = new LoggerContext();

        // init encoder
//        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
//        encoder.setContext(context);
//        encoder.setImmediateFlush(true);
//        encoder.setPattern(pattern);
//        encoder.start();

        // init fileAppender
//        RollingFileAppender fileAppender = new RollingFileAppender();
//        fileAppender.setContext(context);
//        fileAppender.setEncoder(encoder);

        // init txt logger
        RollingFileAppender txtFileAppender = getAppender(context, pattern);
        TimeBasedRollingPolicy txtPolicy = new TimeBasedRollingPolicy();
        txtPolicy.setContext(context);
        txtPolicy.setParent(txtFileAppender);
        txtPolicy.setFileNamePattern(txtFileNamePattern);

        txtFileAppender.setRollingPolicy(txtPolicy);

        txtPolicy.start();
        txtFileAppender.start();

        txtLogger = context.getLogger(ReceivedMessageLogger.class);
        txtLogger.setAdditive(false);
        txtLogger.addAppender(txtFileAppender);

        // init zip logger
        RollingFileAppender zipFileAppender = getAppender(context, pattern);
        TimeBasedRollingPolicy zipPolicy = new TimeBasedRollingPolicy();
        zipPolicy.setContext(context);
        zipPolicy.setParent(zipFileAppender);
        zipPolicy.setFileNamePattern(zipFileNamePattern);

        zipFileAppender.setRollingPolicy(zipPolicy);

        zipPolicy.start();
        zipFileAppender.start();

        zipLogger = context.getLogger(ReceivedMessageLogger.class);
        zipLogger.setAdditive(false);
        zipLogger.addAppender(zipFileAppender);
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
        zipLogger.info(event.toString());
    }
}
