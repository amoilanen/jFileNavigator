package filebrowser;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public class LoggingSupport {

    public static final String MESSAGE_PATTERN = "%d [%t] %-5p %c - %m%n";
    
    public static final String LOG_FILE = "execution.log";
    
    private static WriterAppender configureAppender(WriterAppender appender) {
        appender.setLayout(new PatternLayout(MESSAGE_PATTERN)); 
        appender.setThreshold(Level.ERROR);
        appender.activateOptions();
        Logger.getRootLogger().addAppender(appender);
        return appender;
    }
    
    public static void configure() {
        FileAppender fileAppender = new FileAppender();

        fileAppender.setAppend(true);
        fileAppender.setFile(LOG_FILE);
        configureAppender(fileAppender);
        configureAppender(new ConsoleAppender());
    }
}
