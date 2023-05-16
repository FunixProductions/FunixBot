package fr.funixgaming.funixbot.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(basePackages = {"fr.funixgaming", "com.funixproductions.api.client"})
@SpringBootApplication(scanBasePackages = "fr.funixgaming", exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class DiscordBotApp {
    public static void main(final String[] args) {
        final SpringApplication app = new SpringApplication(DiscordBotApp.class);

        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
