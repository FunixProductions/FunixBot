package fr.funixgaming.funixbot.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(basePackages = {
        "fr.funixgaming",
        "com.funixproductions.core.integrations.openai"
})
@SpringBootApplication(scanBasePackages = {
        "fr.funixgaming", "com.funixproductions"
})
public class DiscordBotApp {
    public static void main(final String[] args) {
        SpringApplication.run(DiscordBotApp.class, args);
    }
}
