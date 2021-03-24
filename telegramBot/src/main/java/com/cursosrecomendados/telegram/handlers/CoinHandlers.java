package com.cursosrecomendados.telegram.handlers;

import java.util.ArrayList;
import java.util.List;

import com.cursosrecomendados.telegram.mappers.PriceInfoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.cursosrecomendados.telegram.configuration.*;
import com.cursosrecomendados.telegram.database.DatabaseManager;
import com.cursosrecomendados.telegram.model.*;
import com.cursosrecomendados.telegram.service.*;

@Component
public class CoinHandlers extends TelegramLongPollingBot{
	private static final String LOGTAG = "CRYPTOHANDLERS";
	
	private static final int STARTSTATE = 0;
    private static final int COININFO = 2;
	
    @Autowired
	static PoloniexServiceImpl poloniex;

    @Autowired
    static PriceInfoConverter converter;
    
    public CoinHandlers() {
    	super();
    }
    
    @Override
	public void onUpdateReceived(Update update) {
    	try {
            if (update.hasMessage()) {
            	poloniex = new PoloniexServiceImpl();
                Message message = update.getMessage();
                if (message.hasText() || message.hasLocation()) {
                    handleIncomingMessage(message);
                }
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
	}

	@Override
	public String getBotUsername() {
		return BotConfig.CRYPTO_USER;
	}

	@Override
	public String getBotToken() {
		return BotConfig.CRYPTO_TOKEN;
	}
	
	private synchronized void handleIncomingMessage(Message message) throws TelegramApiException {
        final int state = DatabaseManager.getInstance().getCoinState(message.getFrom().getId(), message.getChatId());
        final String language = DatabaseManager.getInstance().getUserCoinOptions(message.getFrom().getId())[0];
        if (!message.isUserMessage() && message.hasText()) {
            if (isCommandForOther(message.getText())) {
                return;
            } else if (message.getText().startsWith(Commands.STOPCOMMAND)){
                sendHideKeyboard(message.getFrom().getId(), message.getChatId(), message.getMessageId());
                return;
            }
        }
        SendMessage sendMessageRequest = messageOnCoin(message, language);
       /*
        switch(state) {
            case COININFO:
                sendMessageRequest = messageOnCoin(message, language, state);
                break;
            default:
                sendMessageRequest = sendMessageDefault(message, language);
                break;
        }
        */
        execute(sendMessageRequest);
    }
	
	  private void sendHideKeyboard(Integer userId, Long chatId, Integer messageId) throws TelegramApiException {
	        SendMessage sendMessage = new SendMessage();
	        sendMessage.setChatId(chatId.toString());
	        sendMessage.enableMarkdown(true);
	        sendMessage.setReplyToMessageId(messageId);
	        sendMessage.setText("Menu");

	        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
	        replyKeyboardRemove.setSelective(true);
	        sendMessage.setReplyMarkup(replyKeyboardRemove);

	        execute(sendMessage);
	        DatabaseManager.getInstance().insertCoinState(userId, chatId, STARTSTATE);
	    }
	
    private static boolean isCommandForOther(String text) {
        boolean isSimpleCommand = text.equals("/start") || text.equals("/help") || text.equals("/stop");
        boolean isCommandForMe = text.equals("/start@CoinInfo21_bot") || text.equals("/help@CoinInfo21_bot") || text.equals("/stop@CoinInfo21_bot");
        return text.startsWith("/") && !isSimpleCommand && !isCommandForMe;
    }
    
    private static SendMessage messageOnCoin(Message message, String language) {
        SendMessage sendMessageRequest = onNewCoin(message, language);
        
        return sendMessageRequest;
    }
    
    private static SendMessage onNewCoin(Message message, String language) {
        //if (message.isReply()) {
            return onCoinReceived(message.getChatId(), message.getFrom().getId(), message.getMessageId(), message.getText(), language);
       /* } else {
        	return sendMessageDefault(message, language);
        }*/
    }
    
    private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Coin");
        keyboardFirstRow.add("Settings");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
    
  /*  
    private static String getSettingsCommand(String language) {
        return LanguagesService.getString("settings", language);
    }
    
    private static String getCoinCommand(String language) {
        return LanguagesService.getString("coin", language);
    }
    */
    private static SendMessage sendMessageDefault(Message message, String language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(language);
        DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(), 1);
        return sendHelpMessage(message.getChatId(), message.getMessageId(), replyKeyboardMarkup, language);
    }
    
    private static SendMessage onCoinReceived(Long chatId, Integer userId, Integer messageId, String text, String language) {
        PoloniexPair valor=null;
    	try {
        	valor = PoloniexPair.valueOf(text);
        }catch(Exception e) {
        	valor = PoloniexPair.BTC_PASC;
        }
    	
    	OrderBook coin = poloniex.getOrderBook(valor);
        //TODO
        // Aqui has de covertir OrderBook en PriceInfo, guardar el PriceInfo a la Base de dades i retornar el
        // valor
        PriceInfo priceInfo= converter.convert(valor, coin);

        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableMarkdown(true);
        sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(language));
        sendMessageRequest.setReplyToMessageId(messageId);
        sendMessageRequest.setText(priceInfo.toString());
        sendMessageRequest.setChatId(chatId.toString());
        DatabaseManager.getInstance().insertCoinState(userId, chatId, 1);
       return sendMessageRequest;
    }
    
    private static SendMessage sendHelpMessage(Long chatId, Integer messageId, ReplyKeyboardMarkup replyKeyboardMarkup, String language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        sendMessage.setText("Help");
        return sendMessage;
    }
    /*
    private static String getHelpMessage(String language) {
        String baseString = LanguagesService.getString("helpCoinMessage", language);
        return baseString;
    }
    
    private static SendMessage onCurrencyChosen(Integer userId, Long chatId, Integer messageId, String units, String language) {
        DatabaseManager.getInstance().putUserWeatherUnitsOption(userId, units);

        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableMarkdown(true);
        sendMessageRequest.setChatId(chatId.toString());
        sendMessageRequest.setText(LanguagesService.getString("currencyUpdated", language));
        sendMessageRequest.setReplyToMessageId(messageId);
        sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(language));

        DatabaseManager.getInstance().insertCoinState(userId, chatId, 1);
        return sendMessageRequest;
    }
    
    private static String getCancelCommand(String language) {
        return LanguagesService.getString("cancel", language);
    }
    */
}
