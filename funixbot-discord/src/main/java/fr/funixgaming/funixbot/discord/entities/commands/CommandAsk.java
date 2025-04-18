package fr.funixgaming.funixbot.discord.entities.commands;

import com.funixproductions.core.integrations.openai.chatgpt.enums.ChatGptModel;
import com.funixproductions.core.integrations.openai.chatgpt.services.ChatGptService;
import fr.funixgaming.funixbot.discord.entities.commands.utils.DiscordCommand;
import kotlin.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Getter
@Slf4j(topic = "AskCommand")
public class CommandAsk extends DiscordCommand {

    private final String name = "ask";
    private final String description = "Pose une question à l'IA de FunixBot !";

    private final ChatGptService chatGptService;

    public CommandAsk(JDA jda, ChatGptService chatGptService) {
        super(jda, new Pair<>("question", "La question à poser à FunixBot"));
        this.chatGptService = chatGptService;
    }

    @Override
    public void runCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        final OptionMapping question = interactionEvent.getOption("question");
        if (question == null) {
            interactionEvent.reply("Veuillez poser une question.").setEphemeral(true).queue();
            return;
        }
        final String questionText = question.getAsString();

        interactionEvent.deferReply().queue();

        try {
            final String response = this.chatGptService.sendGptRequest(
                    ChatGptModel.GPT_3_5_TURBO,
                    "Tu est le bot de FunixGaming, nommé FunixBot. Tu répoonds aux questions des viewers du stream de FunixGaming de manière drole et sarcastique.",
                    questionText
            );
            interactionEvent.getHook().sendMessage(response).queue();
        } catch (Exception e) {
            interactionEvent.getHook().sendMessage("Une erreur est survenue lors de la récupération de la réponse.").setEphemeral(true).queue();
            log.error("Erreur lors de l'envoi de la requête à l'API ChatGPT", e);
        }
    }

}
