package com.cursosrecomendados.telegram.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.cursosrecomendados.telegram.configuration.BotConfig;

public class CoinHandlers extends TelegramLongPollingBot{
	private static final String LOGTAG = "CRYPTOHANDLERS";
	
	private static final int STARTSTATE = 0;
    private static final int MAINMENU = 1;
    private static final int COININFO = 2;
    private static final int SETTINGS = 3;
    private static final int CURRENCY = 4;
	
    
    public CoinHandlers() {
    	super();
    }
    
    @Override
	public void onUpdateReceived(Update update) {
		
	}

	@Override
	public String getBotUsername() {
		return BotConfig.CRYPTO_USER;
	}

	@Override
	public String getBotToken() {
		return BotConfig.CRYPTO_TOKEN;
	}
	
	
}
