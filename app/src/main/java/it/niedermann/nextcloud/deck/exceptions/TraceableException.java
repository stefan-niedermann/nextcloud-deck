package it.niedermann.nextcloud.deck.exceptions;

import it.niedermann.nextcloud.deck.DeckLog;

public class TraceableException extends RuntimeException {

    private TraceableException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void makeTraceableIfFails(Runnable runnable, Object... args) {
        try {
            runnable.run();
        } catch (Throwable t) {
            String message = "Sorry, a wild error appeared!\n" +
                    "### If you want to tell us about the following issue, " +
                    "please make sure to censor sensitive data beforehand! ###\n" +
                    "Failed to run traceable code";
            if (args != null && args.length > 0) {
                message += " with arguments:\n";
                for (Object arg : args) {
                    message += (arg == null ? "null" : arg.toString())+"\n";
                }
            } else {
                message += ":\n";
            }

            message += "Cause: " + t.getLocalizedMessage();
            TraceableException ex = new TraceableException(message, t);
            DeckLog.logError(ex);
            throw ex;
        }
    }
}
