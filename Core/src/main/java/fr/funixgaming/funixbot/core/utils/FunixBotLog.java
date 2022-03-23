package fr.funixgaming.funixbot.core.utils;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FunixBotLog {
    private static final FunixBotLog instance = new FunixBotLog();
    private static final File logFolder = new File(DataFiles.getInstance().getDataFolder(), "logs");

    private final Logger logger;

    private FunixBotLog() {
        this.logger = LoggerFactory.getLogger(FunixBotLog.class);

        if (!logFolder.exists() && !logFolder.mkdir()) {
            new FunixBotException("Erreur lors de la création du dossier logs").printStackTrace();
        }
    }

    public void logInfo(final String message, final Object... args) {
        logger.info(message, args);
        logFile("[INFO] " + message);
    }

    public void logError(final String message, final Object... args) {
        logger.error(message, args);
        logFile("[ERROR] " + message);
    }

    public void logError(final Exception exception) {
        logger.error("[APP ERROR]", exception);
        logFile(Arrays.toString(exception.getStackTrace()));
    }

    private void logFile(final String message) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        final LocalDateTime now = LocalDateTime.now();
        final String nameLog = formatter.format(now) + ".log";
        final File file = new File(logFolder, nameLog);

        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new FunixBotException("Impossible de créer le fichier de log.");
            }
            DataFiles.appendInFile(file, message);
        } catch (Exception e) {
            new FunixBotException("Une erreur est survenue lors du log en fichier.", e).printStackTrace();
        }
    }

    public static FunixBotLog getInstance() {
        return instance;
    }
}
