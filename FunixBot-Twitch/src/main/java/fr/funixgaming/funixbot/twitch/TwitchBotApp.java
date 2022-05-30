package fr.funixgaming.funixbot.twitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(basePackages = "fr.funixgaming")
@SpringBootApplication(scanBasePackages = "fr.funixgaming")
public class TwitchBotApp {
    public static void main(final String[] args) {
        SpringApplication.run(TwitchBotApp.class, args);
    }
}
