package fr.funixgaming.funixbot.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableFeignClients(basePackages = "fr.funixgaming")
@SpringBootApplication(scanBasePackages = "fr.funixgaming")
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class);
    }
}
