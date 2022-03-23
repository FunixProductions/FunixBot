package fr.funixgaming.funixbot.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotProfile {
    LOCAL("local"),
    PRODUCTION("production");

    private final String mode;
}
