package fr.funixgaming.funixbot.twitch.commands;

import fr.funixgaming.api.client.funixbot.dtos.FunixBotUserExperienceDTO;
import fr.funixgaming.funixbot.core.commands.entities.BotCommand;
import fr.funixgaming.funixbot.core.exceptions.FunixBotException;
import fr.funixgaming.funixbot.twitch.FunixBot;
import fr.funixgaming.funixbot.twitch.modules.ChatExperience;
import fr.funixgaming.funixbot.twitch.utils.TwitchEmotes;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandGiveaway extends BotCommand {
    private static volatile CommandGiveaway instance = null;

    private final FunixBot bot;

    private boolean active = false;
    private boolean subOnly = false;
    private int minimumChatLevel = 0;
    private String triggerWord = null;

    private final List<ChatMember> participants = new ArrayList<>();
    private final List<ChatMember> bans = new ArrayList<>();

    public CommandGiveaway(final FunixBot funixBot) {
        super("giveaway");
        this.bot = funixBot;
        instance = this;
    }

    @Override
    public void onUserCommand(@NonNull ChatMember user, @NonNull String command, @NonNull String[] args) {
        if (user.getBadges().isModerator() || user.getBadges().isStreamer()) {
            if (args.length > 0) {
                final String action = args[0];

                if (action.equalsIgnoreCase("start")) {
                    endGiveaway();
                    startGiveaway(args);
                } else if (action.equalsIgnoreCase("roll")) {
                    final ChatMember winner = rollWinner();

                    if (winner == null) {
                        bot.sendChatMessage(user.getChannelName(), "Pas de participants pour le giveaway.");
                    } else {
                        bot.sendChatMessage(winner.getChannelName(), String.format(
                                "%s %s a gagné le giveaway !",
                                TwitchEmotes.GIFT,
                                winner.getDisplayName()
                        ));
                        log.info("{} a gagné le giveaway.", winner.getDisplayName());
                    }
                } else if (action.equalsIgnoreCase("stop") || action.equalsIgnoreCase("end")) {
                    endGiveaway();
                    bot.sendChatMessage(user.getChannelName(), String.format(
                            "%s Fin du giveaway !",
                            TwitchEmotes.GIFT
                    ));
                } else if (action.equalsIgnoreCase("help")) {
                    bot.sendChatMessage(user.getChannelName(), "Help -> !giveaway start|stop|roll [triggerWord] [subOnly oui non, non défaut] [minimumChatLevel >= 0, 0 défaut]");
                }
            }
        }
    }

    public void onUserChat(final ChatMessage chatMessage) throws FunixBotException {
        if (active && triggerWord != null) {
            final String message = chatMessage.getMessage();
            final ChatMember chatMember = chatMessage.getOwner();

            if (message.equalsIgnoreCase(triggerWord) && notInList(chatMember) && notBanned(chatMember) && canParticipate(chatMember)) {
                participants.add(chatMember);
                bot.sendChatMessage(chatMember.getChannelName(), String.format(
                        "%s %s a été ajouté aux participants du giveaway.",
                        TwitchEmotes.GIFT,
                        chatMember.getDisplayName()
                ));
                log.info("{} a été ajouté aux participants du giveaway.", chatMember.getDisplayName());
            }
        }
    }

    /**
     * !giveaway start [triggerWord] [subOnly oui non, non défaut] [minimumChatLevel >= 0, 0 défaut]
     * @param args command args
     */
    private void startGiveaway(final String[] args) {
        this.active = true;

        try {
            if (args.length >= 2) {
                this.triggerWord = args[1];
            }

            if (args.length >= 3) {
                final String subOnly = args[2];

                this.subOnly = subOnly.equalsIgnoreCase("oui") ||
                        subOnly.equalsIgnoreCase("true") ||
                        subOnly.equalsIgnoreCase("o") ||
                        subOnly.equalsIgnoreCase("y");
            }

            if (args.length >= 4) {
                try {
                    final int minimumLevel = Integer.parseInt(args[3]);

                    if (minimumLevel < 0) {
                        throw new NumberFormatException();
                    } else {
                        this.minimumChatLevel = minimumLevel;
                    }
                } catch (NumberFormatException numberFormatException) {
                    throw new FunixBotException("Le niveau entré est invalide. Veuillez entrer un nombre positif.");
                }
            }

            bot.sendChatMessage(bot.getTwitchConfig().getStreamerUsername(), String.format(
                    "%s Début du giveaway ! Mot clé -> %s",
                    TwitchEmotes.GIFT,
                    this.triggerWord
            ));

        } catch (FunixBotException e) {
            endGiveaway();
            bot.sendChatMessage(bot.getTwitchConfig().getStreamerUsername(), String.format(
                    "%s Giveaway erreur -> %s",
                    TwitchEmotes.GIFT,
                    e.getMessage()
            ));
        }
    }

    @Nullable
    private ChatMember rollWinner() {
        if (this.participants.isEmpty()) {
            return null;
        }

        final SecureRandom secureRandom = new SecureRandom();
        final int participantChosen = secureRandom.nextInt(this.participants.size());
        final ChatMember winner = this.participants.get(participantChosen);

        this.participants.remove(winner);
        this.bans.add(winner);
        return winner;
    }

    private void endGiveaway() {
        active = false;
        subOnly = false;
        minimumChatLevel = 0;
        triggerWord = null;
        participants.clear();
        bans.clear();
    }

    private boolean canParticipate(final ChatMember chatMember) throws FunixBotException {
        if (this.subOnly && !chatMember.getBadges().isSubscriber()) {
            bot.sendChatMessage(chatMember.getChannelName(), String.format(
                    "%s %s Ce giveaway est réservé aux abonnés twitch (!sub).",
                    chatMember.getDisplayName(),
                    TwitchEmotes.GIFT
            ));
            return false;
        }

        if (this.minimumChatLevel > 0) {
            final FunixBotUserExperienceDTO userLevel = ChatExperience.getInstance().findExpByUserId(Integer.toString(chatMember.getUserId()));

            if (userLevel == null || userLevel.getLevel() < this.minimumChatLevel) {
                bot.sendChatMessage(chatMember.getChannelName(), String.format(
                        "%s %s Ce giveaway est réservé aux membres de niveau %d (!level).",
                        TwitchEmotes.GIFT,
                        chatMember.getDisplayName(),
                        this.minimumChatLevel
                ));
                return false;
            }
        }

        return true;
    }

    private boolean notInList(final ChatMember chatMember) {
        for (final ChatMember participant : participants) {
            if (participant.getUserId() == chatMember.getUserId()) {
                return false;
            }
        }
        return true;
    }

    private boolean notBanned(final ChatMember chatMember) {
        for (final ChatMember banned : this.bans) {
            if (banned.getUserId() == chatMember.getUserId()) {
                return false;
            }
        }
        return true;
    }

    public static CommandGiveaway getInstance() throws FunixBotException {
        if (instance == null) {
            throw new FunixBotException("CommandGiveaway not loaded.");
        }
        return instance;
    }
}
