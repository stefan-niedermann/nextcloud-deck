package it.niedermann.nextcloud.deck.exceptions;

import it.niedermann.nextcloud.deck.DeckLog;

public class TraceableException extends RuntimeException {

    private TraceableException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void makeTraceableIfFails(Runnable runnable, Object... args) {
        try {
            runnable.run();
        } catch (TraceableException t) {
            throw t;
        } catch (Throwable t) {
            final StringBuilder message = new StringBuilder("Sorry, a wild error appeared!\n\n" +
                    "⚠️ If you want to tell us about the following issue, " +
                    "please make sure to censor sensitive data beforehand! ⚠️\n\n" +
                    "Failed to run traceable code");
            if (args != null && args.length > 0) {
                message.append(" with arguments:\n");
                for (Object arg : args) {
                    message.append(arg == null ? "null" : arg.toString()).append("\n");
                }
            } else {
                message.append(":\n");
            }

            message.append("Cause: ").append(t.getLocalizedMessage());
            TraceableException ex = new TraceableException(message.toString(), t);
            DeckLog.logError(ex);
            throw ex;
        }
    }
}
