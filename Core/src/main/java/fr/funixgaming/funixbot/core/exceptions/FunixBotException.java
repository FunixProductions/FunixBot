package fr.funixgaming.funixbot.core.exceptions;

import fr.funixgaming.funixbot.core.utils.FunixBotLog;

public class FunixBotException extends Exception {
    final FunixBotLog log = FunixBotLog.getInstance();

    public FunixBotException(String message) {
        super(message);
    }

    public FunixBotException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        log.logError(this);
    }
}
