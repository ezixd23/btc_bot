package com.cursosrecomendados.telegram1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.logging.BotLogger;
import org.telegram.telegrambots.meta.logging.BotsFileHandler;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

@SpringBootApplication(scanBasePackageClasses = {
        SpringBootTelegramBot.class // Scan everything local
})
public class SpringBootTelegramBot {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }
        SpringApplication.run(SpringBootTelegramBot.class, args);
    }
}
