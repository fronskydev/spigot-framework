package org.example.spigotframework.logic.logging;

import java.util.logging.Level;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void severe(String message) {
        logger.log(Level.SEVERE, message);
    }
}
