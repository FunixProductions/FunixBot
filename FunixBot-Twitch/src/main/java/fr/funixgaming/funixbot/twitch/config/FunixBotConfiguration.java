package fr.funixgaming.funixbot.twitch.config;

import fr.funixgaming.funixbot.core.auth.BotTwitchAuth;
import fr.funixgaming.funixbot.core.enums.BotProfile;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class FunixBotConfiguration {
    private static volatile FunixBotConfiguration instance = null;

    private final BotProperties botProperties;
    private final BotTwitchAuth twitchAuth;

    private FunixBotConfiguration(final String[] args) throws FunixBotException {
        this.botProperties = new BotProperties(getProfileFromArgs(args));
        this.twitchAuth = new BotTwitchAuth();
    }

    private BotProfile getProfileFromArgs(final String[] args) {
        final String value = getValueFromArgs(args, "profile");

        if (value != null) {
            for (final BotProfile profile : BotProfile.values()) {
                if (profile.getMode().equalsIgnoreCase(value)) {
                    return profile;
                }
            }
        }
        return BotProfile.PRODUCTION;
    }

    @Nullable
    private String getValueFromArgs(final String[] args, final String argToGet) {
        for (final String arg : args) {
            final String[] data = arg.split("=");

            if (data.length == 2 && data[0].equalsIgnoreCase("--" + argToGet)) {
                return data[1];
            }
        }
        return null;
    }

    public static void init(final String[] args) throws FunixBotException {
        instance = new FunixBotConfiguration(args);
    }

    public static FunixBotConfiguration getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("La config du bot n'est pas chargée.");
        }
        return instance;
    }
}
