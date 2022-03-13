package fr.funixgaming.funixbot.twitch.exceptions;

public class FunixBotException extends Exception {
    public FunixBotException(String message) {
        super(message);
    }

    public FunixBotException(String message, Throwable cause) {
        super(message, cause);
    }
}
