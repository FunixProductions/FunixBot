package fr.funixgaming.funixbot.twitch.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunixBotConfigTests {

    @Test
    public void testArgsLocal() throws Exception {
        final String[] args = new String[] {
                "--profile=local"
        };
        FunixBotConfiguration.init(args);
        final FunixBotConfiguration configuration = FunixBotConfiguration.getInstance();

        assertEquals("testfunix", configuration.getBotProperties().getBotUsername());
        assertEquals("funixbot", configuration.getBotProperties().getStreamerUsername());
    }

    @Test
    public void testArgsProd() throws Exception {
        final String[] args = new String[] {
                "--profile=production"
        };
        FunixBotConfiguration.init(args);
        final FunixBotConfiguration configuration = FunixBotConfiguration.getInstance();

        assertEquals("funixbot", configuration.getBotProperties().getBotUsername());
        assertEquals("funixgaming", configuration.getBotProperties().getStreamerUsername());
    }

}
