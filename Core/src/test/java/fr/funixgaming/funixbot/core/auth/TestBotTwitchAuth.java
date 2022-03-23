package fr.funixgaming.funixbot.core.auth;

import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.twitch.api.auth.TwitchAuth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestBotTwitchAuth {

    @Test
    public void testAuth() throws FunixBotException {
        final BotTwitchAuth auth = new BotTwitchAuth();
        final TwitchAuth twitchAuth = auth.getAuth();

        assertNotNull(twitchAuth.getAccessToken());
        assertNotNull(twitchAuth.getClientId());
        assertNotNull(twitchAuth.getClientSecret());
        assertNotNull(twitchAuth.getExpirationDate());
        assertNotNull(twitchAuth.getOauthCode());
        assertNotNull(twitchAuth.getRefreshToken());
        assertNotNull(twitchAuth.getUserId());
        assertNotNull(twitchAuth.getUserName());
    }

}
