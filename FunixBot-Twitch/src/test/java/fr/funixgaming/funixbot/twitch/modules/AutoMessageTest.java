package fr.funixgaming.funixbot.twitch.modules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMessageTest {

    @Test
    public void testGettingJson() throws Exception {
        final AutoMessages autoMessages = new AutoMessages();
        assertEquals(4, autoMessages.getMessages().length);
    }
}
