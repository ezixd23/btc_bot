package com.cursosrecomendados.telegram;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;
import org.telegram.telegrambots.meta.logging.BotsFileHandler;

import com.cursosrecomendados.telegram.configuration.BuildVars;
import com.cursosrecomendados.telegram.handlers.CoinHandlers;
import com.cursosrecomendados.telegram.handlers.WebHookExampleHandlers;

public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new CoinHandlers());
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi;
        if (!BuildVars.useWebHook) {
            telegramBotsApi = createLongPollingTelegramBotsApi();
        } else if (!BuildVars.pathToCertificatePublicKey.isEmpty()) {
            telegramBotsApi = createSelfSignedTelegramBotsApi();
            telegramBotsApi.registerBot(new WebHookExampleHandlers());
        } else {
            telegramBotsApi = createNoSelfSignedTelegramBotsApi();
            telegramBotsApi.registerBot(new WebHookExampleHandlers());
        }
        return telegramBotsApi;
    }

    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }

    private static TelegramBotsApi createSelfSignedTelegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(BuildVars.pathToCertificateStore, BuildVars.certificateStorePassword, BuildVars.EXTERNALWEBHOOKURL, BuildVars.INTERNALWEBHOOKURL, BuildVars.pathToCertificatePublicKey);
    }

    private static TelegramBotsApi createNoSelfSignedTelegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(BuildVars.pathToCertificateStore, BuildVars.certificateStorePassword, BuildVars.EXTERNALWEBHOOKURL, BuildVars.INTERNALWEBHOOKURL);
    }
}
