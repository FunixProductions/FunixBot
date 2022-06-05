package fr.funixgaming.funixbot.twitch.modules;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AutoMessageTest {

    @Autowired
    private AutoMessages autoMessages;

    @Test
    public void testGettingJson() {
        System.out.println(autoMessages.getMessages().length + " messages");
        assertEquals(4, autoMessages.getMessages().length);
    }
}
