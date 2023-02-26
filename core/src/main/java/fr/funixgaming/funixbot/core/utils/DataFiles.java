package fr.funixgaming.funixbot.core.utils;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class DataFiles {

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

}
