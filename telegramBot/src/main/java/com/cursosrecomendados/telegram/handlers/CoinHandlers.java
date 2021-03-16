package com.cursosrecomendados.telegram.handlers;

import java.util.ArrayList;
import java.util.List;

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
    private static final int CURRENCY = 3;
    private static final int LANGUAGE = 4;
	
    @Autowired
	static
    PoloniexServiceImpl poloniex;

    
    public CoinHandlers() {
    	super();
    }
    
    @Override
	public void onUpdateReceived(Update update) {
    	try {
            if (update.hasMessage()) {
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
        SendMessage sendMessageRequest=null;
        switch(state) {
            case COININFO:
                sendMessageRequest = messageOnCoin(message, language, state);
                break;
            case CURRENCY:
               // sendMessageRequest = messageOnCurrency(message, language);
                break;
            case LANGUAGE:
                //sendMessageRequest = messageOnLanguage(message, language);
                break;
            default:
                sendMessageRequest = sendMessageDefault(message, language);
                break;
        }

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
        boolean isCommandForMe = text.equals("/start@weatherbot") || text.equals("/help@weatherbot") || text.equals("/stop@weatherbot");
        return text.startsWith("/") && !isSimpleCommand && !isCommandForMe;
    }
    
    private static SendMessage messageOnCoin(Message message, String language, int state) {
        SendMessage sendMessageRequest = null;
        sendMessageRequest = onNewCoin(message, language);
        
        return sendMessageRequest;
    }
    
    private static SendMessage onNewCoin(Message message, String language) {
        if (message.isReply()) {
            return onCoinReceived(message.getChatId(), message.getFrom().getId(), message.getMessageId(), message.getText(), language);
        } else {
        	return sendMessageDefault(message, language);
        }
    }
    
    private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(getCoinCommand(language));
        keyboardFirstRow.add(getSettingsCommand(language));
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
    
   /* 
    * TODO
    * private static ReplyKeyboardMarkup getSettingsKeyboard(String language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(getLanguagesCommand(language));
        keyboardFirstRow.add(getUnitsCommand(language));
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(getAlertsCommand(language));
        keyboardSecondRow.add(getBackCommand(language));
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }*/
    
    private static String getSettingsCommand(String language) {
        return String.format(LanguagesService.getString("settings", language));
    }
    
    private static String getCoinCommand(String language) {
        return String.format(LanguagesService.getString("coin", language));
    }
    
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
    	
    	PriceInfo coin = poloniex.getOrderBook(valor);
        
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableMarkdown(true);
        sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(language));
        sendMessageRequest.setReplyToMessageId(messageId);
        sendMessageRequest.setText(coin.toString());
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
        sendMessage.setText(getHelpMessage(language));
        return sendMessage;
    }
    
    private static String getHelpMessage(String language) {
        String baseString = LanguagesService.getString("helpWeatherMessage", language);
        return baseString;
    }
    
    /*
     * 
     * TODO
    private static SendMessage messageOnUnits(Message message, String language) {
        SendMessage sendMessageRequest = null;
        if (message.hasText()) {
            if (message.getText().trim().equals(getCancelCommand(language))) {
                sendMessageRequest = onBackCurrencyCommand(message, language);
            } else if (message.getText().trim().equals(LanguagesService.getString("dollar", language))) {
                sendMessageRequest = onCurrencyChosen(message.getFrom().getId(), message.getChatId(),
                        message.getMessageId(), PoloniexServiceImpl.DOLLAR, language);
            } else if (message.getText().trim().equals(LanguagesService.getString("euro", language))) {
                sendMessageRequest = onCurrencyChosen(message.getFrom().getId(), message.getChatId(),
                        message.getMessageId(), PoloniexServiceImpl.EURO, language);
            } else {
                sendMessageRequest = onCurrencyError(message.getChatId(), message.getMessageId(), language);
            }
        }
        return sendMessageRequest;
    }
    
    private static SendMessage onBackCurrencyCommand(Message message, String language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        ReplyKeyboardMarkup replyKeyboardMarkup = getSettingsKeyboard(language);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(getSettingsMessage(language));

        DatabaseManager.getInstance().insertWeatherState(message.getFrom().getId(), message.getChatId(), SETTINGS);
        return sendMessage;
    }
	
    private static SendMessage onCurrencyError(Long chatId, Integer messageId, String language) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableMarkdown(true);
        sendMessageRequest.setChatId(chatId.toString());
        sendMessageRequest.setReplyMarkup(getCurrencyKeyboard(language));
        sendMessageRequest.setText(LanguagesService.getString("errorUnitsNotFound", language));
        sendMessageRequest.setReplyToMessageId(messageId);

        return sendMessageRequest;
    }
	*/
    private static SendMessage onCurrencyChosen(Integer userId, Long chatId, Integer messageId, String units, String language) {
        DatabaseManager.getInstance().putUserWeatherUnitsOption(userId, units);

        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableMarkdown(true);
        sendMessageRequest.setChatId(chatId.toString());
        sendMessageRequest.setText(LanguagesService.getString("unitsUpdated", language));
        sendMessageRequest.setReplyToMessageId(messageId);
        sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(language));

        DatabaseManager.getInstance().insertCoinState(userId, chatId, 1);
        return sendMessageRequest;
    }
    
    private static String getCancelCommand(String language) {
        return LanguagesService.getString("cancel", language);
    }
    
}
