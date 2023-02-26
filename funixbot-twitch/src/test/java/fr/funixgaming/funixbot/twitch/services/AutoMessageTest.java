package fr.funixgaming.funixbot.twitch.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMessageTest {

    private AutoMessagesService autoMessagesService;

    public void testGettingJson() {
        System.out.println(autoMessagesService.getMessages().length + " messages");
        assertEquals(4, autoMessagesService.getMessages().length);
    }
}
