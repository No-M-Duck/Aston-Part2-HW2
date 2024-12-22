package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {

    private LoggerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Logger getLogger(Class<?> clazz){
        return LogManager.getLogger(clazz);
    }

}
