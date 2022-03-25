package fr.funixgaming.funixbot.core.utils;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import org.apache.log4j.*;

import java.io.File;

public class FunixBotLog {
    private static final FunixBotLog instance = new FunixBotLog();

    private final Logger logger;

    private FunixBotLog() {
        final File logFolder = new File(DataFiles.getInstance().getDataFolder(), "logs");
        if (!logFolder.exists() && !logFolder.mkdir()) {
            new FunixBotException("Erreur lors de la création du dossier logs").printStackTrace();
        }

        final PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern("[%p] %d - %m%n");

        final DailyRollingFileAppender rollingFileAppender = new DailyRollingFileAppender();
        rollingFileAppender.setFile(new File(logFolder, "funixbot.log").getPath());
        rollingFileAppender.setDatePattern("'_'dd-MM-yyyy'.log'");
        rollingFileAppender.setLayout(patternLayout);
        rollingFileAppender.activateOptions();

        BasicConfigurator.configure(new ConsoleAppender(patternLayout));

        this.logger = Logger.getLogger(FunixBotLog.class);
        this.logger.addAppender(rollingFileAppender);
    }

    public void logInfo(final String message) {
        logger.info(message);
    }

    public void logError(final String message) {
        logger.error(message);
    }

    public void logError(final Exception exception) {
        logger.error(exception);
    }

    public static FunixBotLog getInstance() {
        return instance;
    }
}
