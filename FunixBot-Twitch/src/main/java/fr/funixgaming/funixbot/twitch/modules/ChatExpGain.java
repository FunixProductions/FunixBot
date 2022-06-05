package fr.funixgaming.funixbot.twitch.modules;

import feign.FeignException;
import fr.funixgaming.api.client.funixbot.clients.FunixBotUserExperienceClient;
import fr.funixgaming.api.client.funixbot.dtos.FunixBotUserExperienceDTO;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.config.TwitchBotConfig;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.exceptions.TwitchApiException;
import fr.funixgaming.twitch.api.reference.TmiTwitchApi;
import fr.funixgaming.twitch.api.reference.entities.responses.twitch.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatExpGain {

    private final TwitchStreamStatus streamStatus;
    private final TwitchBotConfig twitchBotConfig;
    private final FunixBotUserExperienceClient funixBotUserExperienceClient;

    private final TmiTwitchApi tmiTwitchApi = new TmiTwitchApi();
    private final Set<FunixBotUserExperienceDTO> userExperienceCache = new HashSet<>();

    public void userChatExp(final ChatMember user) {
        try {
            if (streamStatus.getStream() != null) {
                final FunixBotUserExperienceDTO experience = findExpByUserId(Integer.toString(user.getUserId()));

                if (experience == null) {
                    createNewExp(user.getUserId());
                } else {
                    final long actualSeconds = Instant.now().getEpochSecond();

                    if (actualSeconds - experience.getLastMessageDateSeconds() >= 300) {
                        experience.setLastMessageDateSeconds(actualSeconds);
                        addExp(experience, user.getDisplayName(), 15);
                    }
                }
            }
        } catch (FunixBotException e) {
            log.error("Une erreur est survenue lors du gain d'exp message user: {} Erreur: {}", user.getDisplayName(), e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void gainLurkExp() {
        try {
            if (streamStatus.getStream() != null) {
                final FunixBot funixBot = FunixBot.getInstance();
                final Set<String> usernames = new HashSet<>(tmiTwitchApi.getUsernamesConnectedOnChat(twitchBotConfig.getStreamerUsername()));
                final Set<User> ids = funixBot.getTwitchApi().getUsersByUserName(usernames);

                for (final User user : ids) {
                    if (!user.getName().equalsIgnoreCase(twitchBotConfig.getStreamerUsername()) &&
                            !user.getName().equalsIgnoreCase(twitchBotConfig.getBotUsername())) {
                        addExp(findExpByUserId(user.getId()), user.getDisplayName(), 5);
                    }
                }
            }
        } catch (TwitchApiException | FunixBotException e) {
            log.error("Erreur lors du gain d'exp lurk {}.", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void saveExp() {
        final List<FunixBotUserExperienceDTO> toSave = this.userExperienceCache.stream().toList();
        this.funixBotUserExperienceClient.update(toSave);
    }

    @Async
    public void createNewExp(final int twitchUserId) {
        FunixBotUserExperienceDTO funixBotUserExperienceDTO = new FunixBotUserExperienceDTO();
        funixBotUserExperienceDTO.setXp(0);
        funixBotUserExperienceDTO.setLevel(0);
        funixBotUserExperienceDTO.setXpNextLevel(50);
        funixBotUserExperienceDTO.setTwitchUserId(Integer.toString(twitchUserId));
        funixBotUserExperienceDTO.setLastMessageDateSeconds(Instant.now().getEpochSecond());

        try {
            funixBotUserExperienceDTO = this.funixBotUserExperienceClient.create(funixBotUserExperienceDTO);
            this.userExperienceCache.add(funixBotUserExperienceDTO);
        } catch (FeignException e) {
            log.error("Erreur lors de la création d'une instance d'exp pour user id {}. Erreur {} Code {}", twitchUserId, e.contentUTF8(), e.status());
        }
    }

    @Nullable
    public FunixBotUserExperienceDTO findExpByUserId(final String twitchUserId) {
        for (final FunixBotUserExperienceDTO search : this.userExperienceCache) {
            if (search.getId() != null && search.getTwitchUserId().equals(twitchUserId)) {
                return search;
            }
        }

        final List<FunixBotUserExperienceDTO> search = funixBotUserExperienceClient.search(String.format("twitchUserId:%s", twitchUserId), "0", "1");

        if (search.isEmpty()) {
            return null;
        } else {
            final FunixBotUserExperienceDTO experience = search.get(0);
            this.userExperienceCache.add(experience);
            return experience;
        }
    }

    private void addExp(@Nullable final FunixBotUserExperienceDTO experience, final String username, int expToAdd) throws FunixBotException {
        if (experience != null) {
            experience.setXp(experience.getXp() + expToAdd);

            if (experience.getXp() >= experience.getXpNextLevel()) {
                experience.setLevel(experience.getLevel() + 1);
                experience.setXpNextLevel(experience.getXpNextLevel() + 50);
                experience.setXp(0);

                log.info("Level UP pour {} (Niveau {})", username, experience.getLevel());
                if (experience.getLevel() % 5 == 0) {
                    FunixBot.getInstance().sendChatMessage(
                            this.twitchBotConfig.getStreamerUsername(),
                            String.format("%s Level UP pour %s (Niveau %d)", TwitchEmotes.TWITCH_LOGO, username, experience.getLevel())
                    );
                }
            }
        }
    }

}
