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
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        final User user = event.getAuthor();
        final Message message = event.getMessage();
        final MessageChannel textChannel = event.getChannel();

        if (event.getAuthor().isBot()) return;

        log.info("[{} > {}] {}", user.getAsTag(), textChannel.getName(), message.getContentRaw());

        if (event.getMessage().getContentDisplay().equals("!rolereact") && event.getAuthor().getId().equals("696753471650660412")){
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(44,175,255))
                    .setTitle("**Choix des rôles**")
                    .setDescription("Afin d'éviter de faire des tag everyone et here sur le discord, vous pouvez choisir vos rôles pour recevoir les notifications qui vous intéressent.\n" +
                            "Vous pouvez d'ailleurs ajouter et retirer votre rôle à tout moment.")
                    .addField(botEmotes.getTwitchEmote().getAsMention(), "Les notifications Twitch", true)
                    .addField(botEmotes.getYoutubeEmote().getAsMention(), "Les notifications YouTube", true)
                    .addField(botEmotes.getTiktokEmote().getAsMention(), "Les notifications TikTok", true);
            Button twitchbtn = Button.primary("notif-twitch", Emoji.fromCustom(String.format(":%s:", botEmotes.getTwitchEmote().getName()), botEmotes.getTwitchEmote().getId(), false));
            Button youtubebtn = Button.primary("notif-youtube", Emoji.fromCustom(String.format(":%s:", botEmotes.getYoutubeEmote().getName()), botEmotes.getYoutubeEmote().getId(), false));
            Button tiktokbtn = Button.primary("notif-tiktok", Emoji.fromCustom(String.format(":%s:", botEmotes.getTiktokEmote().getName()), botEmotes.getTiktokEmote().getId(), false));

            event.getChannel().sendMessageEmbeds(embed.build()).addActionRow(twitchbtn, youtubebtn, tiktokbtn).queue();
            event.getMessage().delete().queue();
        }

        if (event.getMessage().getContentRaw().startsWith("!")) {
            String command = event.getMessage().getContentRaw().split(" ")[0].replace("!", "");

            for (CommandList cmdl : CommandList.values()) {
                if (cmdl.getName().equals(command)) {
                    for (EmbedCommands cmd : EmbedCommands.values()) {
                        if (cmd.getName().equals(command)) {
                            cmd.run(event, command);
                        }
                    }
                    for (Commands cmd : Commands.values()) {
                        if (cmd.getName().equals(command)) {
                            cmd.run(event);
                        }
                    }
                } else {
                    errcommand(event, command);
                }

            }
            setCount(0);
        }
    }

    private int Count = 0;

    public void errcommand(MessageReceivedEvent event, String command) {
        setCount((getCount() + 1));
        if (getCount() == CommandList.values().length) { // Command is invalid
            event.getChannel().sendMessage(String.format(":warning: La commande `%s` n'existe pas ! \n`!help` pour la liste des commandes.", command)).queue();
        }
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
    
}
