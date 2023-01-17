package fr.funixgaming.funixbot.twitch.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMessageTest {

    private AutoMessages autoMessages;

    public void testGettingJson() {
        System.out.println(autoMessages.getMessages().length + " messages");
        assertEquals(4, autoMessages.getMessages().length);
    }
}
