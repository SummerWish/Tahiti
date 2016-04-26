package octoteam.tahiti.shared.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import octoteam.tahiti.shared.event.MessageReceivedEvent;

public class ReceivedMessageLogger {

    /**
     * 日志生成器
     */
    private Logger logger;

    private final static String filename = "logs";

    private final static String pattern = "%d - Received Message:%n%msg%n";

    public ReceivedMessageLogger() {
        this(filename);
    }

    public ReceivedMessageLogger(String filename) {
        this(filename, pattern);
    }

    public ReceivedMessageLogger(String filename, String pattern) {

        LoggerContext context = new LoggerContext();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setImmediateFlush(true);
        encoder.setPattern(pattern);
        encoder.start();

        FileAppender fileAppender = new FileAppender();
        fileAppender.setContext(context);
        fileAppender.setFile(filename);
        fileAppender.setEncoder(encoder);
        fileAppender.start();

        logger = context.getLogger(ReceivedMessageLogger.class);
        logger.setAdditive(false);
        logger.addAppender(fileAppender);
    }

    public void log(MessageReceivedEvent event) {
            logger.info(event.toString());
    }
}
