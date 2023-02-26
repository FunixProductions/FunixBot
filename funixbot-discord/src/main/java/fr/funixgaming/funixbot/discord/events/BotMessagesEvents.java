package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.modules.RoleMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
@RequiredArgsConstructor
public class BotMessagesEvents extends ListenerAdapter {

    private final RoleMessageHandler roleMessageHandler;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel textChannel = event.getChannel();

        log.info("[{} > {}] {}", user.getAsTag(), textChannel.getName(), message.getContentRaw());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel textChannel = event.getChannel();

        log.info("UPDATE [{} > {}] {}", user.getAsTag(), textChannel.getName(), message.getContentRaw());
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        final User user = event.getUser();
        final MessageReaction reaction = event.getReaction();

        roleMessageHandler.manageRolesByReactions(user, reaction, true);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        final User user = event.getUser();
        final MessageReaction reaction = event.getReaction();

        roleMessageHandler.manageRolesByReactions(user, reaction, false);
    }
}
