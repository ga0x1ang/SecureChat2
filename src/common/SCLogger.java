package common;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/23/13
 * Time: 2:01 AM
 */
public class SCLogger {

    private Handler handler;

    public SCLogger(String logFileName) throws IOException {

        this.handler = new FileHandler(logFileName);
        this.handler.setFormatter(new XMLFormatter());
    }

    public final void log(String classname, String logmsg) {

        Logger logger = Logger.getLogger(classname);
        logger.setLevel(Level.INFO);
        logger.addHandler(handler);

        logger.log(Level.INFO, logmsg);
        //handler.close();
    }
}
