package fr.funixgaming.funixbot.discord.events;

import fr.funixgaming.funixbot.discord.modules.RoleMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
<<<<<<< HEAD
import net.dv8tion.jda.api.entities.MessageChannel;
=======
>>>>>>> 62313d3c8452c31bc1c4f62e78f0860ff1a0a106
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
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

        log.info("[{} > {}] {}", user.getName(), textChannel.getName(), message.getContentRaw());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel textChannel = event.getChannel();

        log.info("UPDATE [{} > {}] {}", user.getName(), textChannel.getName(), message.getContentRaw());
    }
}
