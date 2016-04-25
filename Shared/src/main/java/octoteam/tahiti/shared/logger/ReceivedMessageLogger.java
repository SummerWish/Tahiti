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

    private final static String file = "logs";

    private final static String pattern = "%d - Received Message:%n%msg";

    public ReceivedMessageLogger() {
        this(file);
    }

    public ReceivedMessageLogger(String file) {
        this(file, pattern);
    }

    public ReceivedMessageLogger(String file, String pattern) {

        LoggerContext context = new LoggerContext();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setImmediateFlush(true);
        encoder.setPattern(pattern);
        encoder.start();

        FileAppender fileAppender = new FileAppender();
        fileAppender.setContext(context);
        fileAppender.setFile(file);
        fileAppender.setEncoder(encoder);
        fileAppender.start();

        logger = context.getLogger(ReceivedMessageLogger.class);
        logger.setAdditive(false);
        logger.addAppender(fileAppender);
    }

    public void log(MessageReceivedEvent event) {
        synchronized (ReceivedMessageLogger.class) {
            logger.info(event.toString());
        }
    }
}
