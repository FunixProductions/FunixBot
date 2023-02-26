package fr.funixgaming.funixbot.core.utils;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Getter
public class DataFiles {
    private static final DataFiles instance = new DataFiles();
    private final File dataFolder = new File("data");

    private DataFiles() {
        try {
            if (!dataFolder.exists() && !dataFolder.mkdir()) {
                throw new FunixBotException("The data folder can't be created.");
            }
        } catch (Exception e) {
            new FunixBotException("Une erreur est survenue lors de la création de la classe DataFiles.", e).printStackTrace();
        }
    }

    public static void setInFile(final File file, final String data) throws IOException {
        Files.writeString(file.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void appendInFile(final File file, final String data) throws IOException {
        Files.writeString(file.toPath(), data, StandardOpenOption.APPEND);
    }

    public static String readFileFromClasspath(final String path) throws FunixBotException {
        final InputStream inputStream = DataFiles.class.getResourceAsStream(path);

        try {
            if (inputStream == null) {
                throw new FunixBotException("Le ficher classpath " + path + " est introuvable.");
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FunixBotException("Impossible de lire le ficher classpath " + path, e);
        }
    }

    public static BufferedImage getImageFromClasspath(final String path) throws IOException {
        final InputStream inputStream = DataFiles.class.getResourceAsStream(path);

        if (inputStream != null) {
            return ImageIO.read(inputStream);
        } else {
            throw new IOException("L'image n'existe pas.");
        }
    }

    public static DataFiles getInstance() {
        return instance;
    }
}
