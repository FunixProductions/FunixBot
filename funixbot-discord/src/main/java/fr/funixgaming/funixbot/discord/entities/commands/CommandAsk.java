package fr.funixgaming.funixbot.discord.entities.commands;

import com.funixproductions.core.integrations.openai.chatgpt.enums.ChatGptModel;
import com.funixproductions.core.integrations.openai.chatgpt.services.ChatGptService;
import com.google.common.base.Strings;
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

        try {
            final String response = this.chatGptService.sendGptRequest(
                    ChatGptModel.GPT_4o,
                    "Tu es FunixBot, le fidèle compagnon numérique de FunixGaming sur Discord. Ton rôle est de répondre aux questions des viewers du stream de FunixGaming avec humour, sarcasme et une bonne dose de mauvaise foi contrôlée.\n" +
                            "\n" +
                            "Tu connais l’univers de FunixGaming : un streamer passionné, un peu trop caféiné, toujours à fond sur ses projets comme Pacifista. Tes réponses doivent être divertissantes, parfois piquantes, mais toujours dans l’esprit fun de la communauté.\n" +
                            "\n" +
                            "Tu peux te moquer gentiment des questions bêtes, faire des blagues, utiliser un ton ironique, mais tu restes respectueux et jamais blessant. Tu es là pour amuser, répondre avec style et surtout faire marrer tout le monde.\n" +
                            "\n" +
                            "Ne fais pas de réponses trop longues : une ou deux phrases bien senties valent mieux qu’un pavé chiant. Et surtout : pas de réponse sérieuse à 100%. Si c’est trop sérieux, tu balances une vanne ou tu trolles un peu pour équilibrer.",
                    questionText
            );

            if (Strings.isNullOrEmpty(response)) {
                interactionEvent.reply("Je n'ai pas pu trouver de réponse à ta question.").setEphemeral(true).queue();
            } else {
                interactionEvent.reply(response.replace("@", "")).queue();
            }
        } catch (Exception e) {
            interactionEvent.reply("Une erreur est survenue lors de la récupération de la réponse.").setEphemeral(true).queue();
            log.error("Erreur lors de l'envoi de la requête à l'API ChatGPT", e);
        }
    }

}
