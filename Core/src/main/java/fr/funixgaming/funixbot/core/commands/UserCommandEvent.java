package fr.funixgaming.funixbot.core.commands;

import fr.funixgaming.twitch.api.chatbot_irc.entities.ChatMember;
import lombok.NonNull;

public interface UserCommandEvent {

    void onUserCommand(final @NonNull ChatMember user,
                       final @NonNull String command,
                       final @NonNull String[] args);

}
