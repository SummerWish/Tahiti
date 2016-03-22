package octoteam.tahiti.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {

    public static Logger logger = LogManager.getLogger(Logging.class.getName());

    public boolean loggerInfo() {
        logger.entry();   //trace级别的信息，在某个方法或者程序逻辑开始的时候调用

        logger.info("blabla");    //info级别的信息

        logger.exit();    //和entry()对应的结束方法
        return false;
    }

    public static void main(String[] args) throws Exception {

        logger.info("Entry");

        Logging logging=new Logging();

        logging.loggerInfo();

        logger.info("Exit");
    }
}
