package fr.funixgaming.funixbot.core.utils;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DataFilesTests {

    private final DataFiles dataFiles = DataFiles.getInstance();
    private final File testFile = new File(dataFiles.getDataFolder(), "test.txt");

    public DataFilesTests() throws Exception {
        if (!testFile.exists() && !testFile.createNewFile()) {
            throw new IOException("err");
        }
    }

    @Test
    public void testSetInFile() throws Exception {
        Files.writeString(testFile.toPath(), "", StandardOpenOption.TRUNCATE_EXISTING);
        DataFiles.setInFile(testFile, "test");

        final String res = Files.readString(testFile.toPath());
        assertEquals("test", res);
    }

    @Test
    public void testAppendFile() throws Exception {
        Files.writeString(testFile.toPath(), "", StandardOpenOption.TRUNCATE_EXISTING);
        DataFiles.appendInFile(testFile, "test");
        final String res = Files.readString(testFile.toPath());
        assertEquals("test", res);

        DataFiles.appendInFile(testFile, "test");
        final String res2 = Files.readString(testFile.toPath());
        assertEquals("testtest", res2);
    }

    @Test
    public void testReadFileClassPath() throws Exception {
        final String res = DataFiles.readFileFromClasspath("/test.txt");
        assertEquals("test", res);
    }

    @Test
    public void testFailReadClasspath() {
        try {
            DataFiles.readFileFromClasspath("dsmsqslduhfqlshldf");
            fail("data find");
        } catch (FunixBotException ignored) {
        }
    }

}
