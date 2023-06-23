package fr.funixgaming.funixbot.discord.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import fr.funixgaming.funixbot.core.utils.DataFiles;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
@Component
public class BotConfigGenerated {
    private static final String ERROR_MESSAGE = "Impossible de créer le fichier botConfig.json";
    private final File configFile = new File(DataFiles.getInstance().getDataFolder(), "botConfig.json");
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting().
            excludeFieldsWithoutExposeAnnotation()
            .create();

    public BotConfigGenerated(BotConfig botConfig) throws IOException {
        if (!configFile.exists() && !configFile.createNewFile()) {
            throw new IOException(ERROR_MESSAGE);
        }
        final String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

        if (configString.length() > 0) {
            final BotConfigGenerated tmp = gson.fromJson(configString, BotConfigGenerated.class);

            this.messageRolesChoiceId = tmp.getMessageRolesChoiceId();
        }
    }

    @Expose
    private String messageRolesChoiceId;

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public void saveConfig() {
        try {
            if (!configFile.exists() && !configFile.createNewFile()) {
                throw new IOException(ERROR_MESSAGE);
            }

            DataFiles.setInFile(configFile, gson.toJson(this, BotConfigGenerated.class));
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde de la conf perso du bot. {}", e.getMessage());
        }
    }
}
