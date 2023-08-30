package fr.funixgaming.funixbot.twitch.modules;

import com.funixproductions.api.twitch.reference.client.dtos.responses.TwitchDataResponseDTO;
import com.funixproductions.api.twitch.reference.client.dtos.responses.channel.chat.TwitchChannelChattersDTO;
import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.crud.enums.SearchOperation;
import feign.FeignException;
import fr.funixgaming.api.funixbot.client.clients.FunixBotUserExperienceClient;
import fr.funixgaming.api.funixbot.client.dtos.FunixBotUserExperienceDTO;
import fr.funixgaming.api.twitch.client.clients.FunixGamingTwitchStreamClient;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.core.utils.TwitchStatus;
import fr.funixgaming.funixbot.twitch.config.BotConfig;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.TwitchBot;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ChatExperience {
    private static volatile ChatExperience instance = null;

    private final BotConfig botConfig;
    private final TwitchBot twitchBot;
    private final TwitchStatus twitchStatus;
    private final FunixGamingTwitchStreamClient funixGamingTwitchStreamClient;
    private final FunixBotUserExperienceClient funixBotUserExperienceClient;

    private final List<FunixBotUserExperienceDTO> userExperienceCache = new ArrayList<>();

    public ChatExperience(BotConfig botConfig,
                          TwitchBot twitchBot,
                          FunixGamingTwitchStreamClient twitchChatClient,
                          TwitchStatus twitchStatus,
                          FunixBotUserExperienceClient funixBotUserExperienceClient) {
        this.botConfig = botConfig;
        this.funixGamingTwitchStreamClient = twitchChatClient;
        this.funixBotUserExperienceClient = funixBotUserExperienceClient;
        this.twitchBot = twitchBot;
        this.twitchStatus = twitchStatus;

        instance = this;
    }

    public void userChatExp(final ChatMember user) {
        if (user.getDisplayName().equalsIgnoreCase(botConfig.getStreamerUsername()) ||
                user.getDisplayName().equalsIgnoreCase(botConfig.getBotUsername())) {
            return;
        }

        try {
            if (twitchStatus.getFunixStreamInfo() != null) {
                final FunixBotUserExperienceDTO experience = findExpByUserId(Integer.toString(user.getUserId()));

                if (experience == null) {
                    createNewExp(user.getUserId(), user.getLoginName());
                } else {
                    final long actualSeconds = Instant.now().getEpochSecond();

                    if (actualSeconds - experience.getLastMessageDateSeconds() >= 300) {
                        experience.setLastMessageDateSeconds(actualSeconds);
                        addExp(experience, user.getLoginName(), 15);
                    }
                }
            }
        } catch (FunixBotException e) {
            log.error("Une erreur est survenue lors du gain d'exp message user: {} Erreur: {}", user.getDisplayName(), e.getMessage());
        }
    }

    public void giveUserExp(final String twitchUserId, final String userName, final int expToGive) throws FunixBotException {
        if (userName.equalsIgnoreCase(botConfig.getStreamerUsername()) ||
                userName.equalsIgnoreCase(botConfig.getBotUsername())) {
            return;
        }

        final FunixBotUserExperienceDTO experienceDTO = findExpByUserId(twitchUserId);
        if (experienceDTO == null) {
            createNewExp(Integer.parseInt(twitchUserId), userName);
        } else {
            addExp(experienceDTO, userName, expToGive);
        }
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void gainLurkExp() {
        if (twitchStatus.getFunixStreamInfo() == null) return;

        try {
            final TwitchDataResponseDTO<TwitchChannelChattersDTO> chatters = this.funixGamingTwitchStreamClient.getChatters(botConfig.getStreamerUsername());

            for (final TwitchChannelChattersDTO user : chatters.getData()) {
                if (!user.getUserLogin().equalsIgnoreCase(botConfig.getStreamerUsername()) && !user.getUserLogin().equalsIgnoreCase(botConfig.getBotUsername())) {
                    final FunixBotUserExperienceDTO experienceDTO = findExpByUserId(user.getUserId());

                    if (experienceDTO != null) {
                        addExp(experienceDTO, user.getUserLogin(), 5);
                    }
                }
            }
        } catch (FeignException | FunixBotException e) {
            log.error("Erreur lors du gain d'exp lurk.", e);
        }
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void saveExp() {
        try {
            final List<FunixBotUserExperienceDTO> toSave = this.userExperienceCache;

            if (!toSave.isEmpty()) {
                this.funixBotUserExperienceClient.update(toSave);
            }
        } catch (FeignException e) {
            log.error("Erreur lors de la sauvegarde des exp. Erreur {} Code {}", e.contentUTF8(), e.status());
        }
    }

    @Scheduled(fixedRate = 12, timeUnit = TimeUnit.HOURS)
    public void saveAndFlushMemory() {
        saveExp();
        this.userExperienceCache.clear();
    }

    @Async
    public void createNewExp(final int twitchUserId, final String twitchUsername) {
        FunixBotUserExperienceDTO funixBotUserExperienceDTO = new FunixBotUserExperienceDTO();
        funixBotUserExperienceDTO.setXp(0);
        funixBotUserExperienceDTO.setLevel(0);
        funixBotUserExperienceDTO.setXpNextLevel(50);
        funixBotUserExperienceDTO.setTwitchUserId(Integer.toString(twitchUserId));
        funixBotUserExperienceDTO.setTwitchUsername(twitchUsername);
        funixBotUserExperienceDTO.setLastMessageDateSeconds(Instant.now().getEpochSecond());

        try {
            funixBotUserExperienceDTO = this.funixBotUserExperienceClient.create(funixBotUserExperienceDTO);
            this.userExperienceCache.add(funixBotUserExperienceDTO);
        } catch (FeignException e) {
            log.error("Erreur lors de la création d'une instance d'exp pour user id {}. Erreur {} Code {}", twitchUserId, e.contentUTF8(), e.status());
        }
    }

    @Nullable
    public FunixBotUserExperienceDTO findExpByUserId(@NonNull final String twitchUserId) {
        for (final FunixBotUserExperienceDTO search : this.userExperienceCache) {
            if (search.getId() != null && search.getTwitchUserId().equals(twitchUserId)) {
                return search;
            }
        }

        try {
            final PageDTO<FunixBotUserExperienceDTO> search = funixBotUserExperienceClient.getAll("0", "1", String.format("twitchUserId:%s:%s", SearchOperation.EQUALS.getOperation(), twitchUserId), "");

            if (search.getContent().isEmpty()) {
                return null;
            } else {
                final FunixBotUserExperienceDTO experience = search.getContent().get(0);
                this.userExperienceCache.add(experience);
                return experience;
            }
        } catch (FeignException e) {
            log.error("Erreur lors de la recherche d'exp pour user id {}. Erreur {} Code {}", twitchUserId, e.contentUTF8(), e.status());
            return null;
        }
    }

    private void addExp(@NonNull final FunixBotUserExperienceDTO experience, @NonNull final String username, int expToAdd) throws FunixBotException {
        experience.setTwitchUsername(username);
        experience.setXp(experience.getXp() + expToAdd);

        if (experience.getXp() >= experience.getXpNextLevel()) {
            experience.setLevel(experience.getLevel() + 1);
            experience.setXpNextLevel(experience.getXpNextLevel() + 50);
            experience.setXp(0);

            log.info("Level UP pour {} (Niveau {})", username, experience.getLevel());

            if (experience.getLevel() % 5 == 0 && userNotInLurk(experience)) {
                twitchBot.sendMessageToChannel(
                        botConfig.getStreamerUsername(),
                        String.format("%s Level UP pour %s (Niveau %d)", TwitchEmotes.TWITCH_LOGO, username, experience.getLevel())
                );
            }
        }
    }

    private boolean userNotInLurk(final FunixBotUserExperienceDTO experienceDTO) {
        final Instant now = Instant.now();
        final Instant lastMessage = Instant.ofEpochSecond(experienceDTO.getLastMessageDateSeconds());

        return lastMessage.plus(5, ChronoUnit.MINUTES).isAfter(now);
    }

    public static ChatExperience getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("Chat Experience pas chargé.");
        }
        return instance;
    }
}
