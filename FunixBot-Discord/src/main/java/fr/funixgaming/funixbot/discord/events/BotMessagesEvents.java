package fr.funixgaming.funixbot.discord.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BotMessagesEvents extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final TextChannel textChannel = event.getTextChannel();

        log.info("[{} > {}] {}", user.getAsTag(), textChannel.getName(), message.getContentRaw());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final TextChannel textChannel = event.getTextChannel();

        log.info("UPDATE [{} > {}] {}", user.getAsTag(), textChannel.getName(), message.getContentRaw());
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
    }

    @Override
    public void onMessageReactionRemoveAll(@NotNull MessageReactionRemoveAllEvent event) {
    }

    @Override
    public void onMessageReactionRemoveEmote(@NotNull MessageReactionRemoveEmoteEvent event) {
    }
}
