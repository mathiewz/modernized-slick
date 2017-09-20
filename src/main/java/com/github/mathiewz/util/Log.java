package com.github.mathiewz.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A simple central logging system
 *
 * @author kevin
 */
public final class Log {
    /** True if we're doing verbose logging INFO and DEBUG */
    private static boolean verbose = true;
    /** true if activated by the system property "com.github.mathiewz.forceVerboseLog" */
    private static boolean forcedVerbose = false;

    /**
     * The debug property which can be set via JNLP or startup parameter to switch
     * logging mode to verbose for games that were released without verbose logging
     * value must be "true"
     */
    private static final String FORCE_VERBOSE_PROPERTY = "com.github.mathiewz.forceVerboseLog";

    /**
     * the verbose property must be set to "true" to switch on verbose logging
     */
    private static final String FORCE_VERBOSE_PROPERTY_ON_VALUE = "true";

    /** The log system plugin in use */
    private static LogSystem logSystem = new DefaultLogSystem();

    private Log() {
        // to avoid instantiation
    }

    /**
     * Indicate that we want verbose logging.
     * The call is ignored if verbose logging is forced by the system property
     * "com.github.mathiewz.forceVerboseLog"
     *
     * @param v
     *            True if we want verbose logging (INFO and DEBUG)
     */
    public static void setVerbose(boolean v) {
        if (forcedVerbose) {
            return;
        }
        verbose = v;
    }

    /**
     * Check if the system property com.github.mathiewz.verboseLog is set to true.
     * If this is the case we activate the verbose logging mode
     */
    public static void checkVerboseLogSetting() {
        try {
            PrivilegedAction<Object> action = () -> {
                String val = System.getProperty(Log.FORCE_VERBOSE_PROPERTY);
                if (val != null && val.equalsIgnoreCase(Log.FORCE_VERBOSE_PROPERTY_ON_VALUE)) {
                    forcedVerbose = true;
                    verbose = true;
                }
                return null;
            };
            AccessController.doPrivileged(action);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Log an error
     *
     * @param obj
     *            The message describing the error
     * @param e
     *            The exception causing the error
     */
    public static void error(Object obj, Throwable e) {
        logSystem.error(obj.toString(), e);
    }

    /**
     * Log an error
     *
     * @param e
     *            The exception causing the error
     */
    public static void error(Throwable e) {
        logSystem.error(e);
    }

    /**
     * Log an error
     *
     * @param obj
     *            The obj describing the error
     */
    public static void error(Object obj) {
        logSystem.error(obj.toString());
    }

    /**
     * Log a warning
     *
     * @param obj
     *            The message describing the warning
     */
    public static void warn(Object obj) {
        logSystem.warn(obj.toString());
    }

    /**
     * Log a warning
     *
     * @param obj
     *            The message describing the warning
     * @param e
     *            The issue causing the warning
     */
    public static void warn(Object obj, Throwable e) {
        logSystem.warn(obj.toString(), e);
    }

    /**
     * Log a debug message
     *
     * @param obj
     *            The message describing the debug
     */
    public static void debug(Object obj) {
        if (verbose || forcedVerbose) {
            logSystem.debug(obj.toString());
        }
    }

    public static void info(Object obj) {
        if (verbose || forcedVerbose) {
            logSystem.info(obj.toString());
        }
    }
}
