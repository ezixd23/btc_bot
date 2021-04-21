package com.cursosrecomendados.telegram.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.cursosrecomendados.telegram.configuration.BotConfig;
import com.cursosrecomendados.telegram.configuration.Commands;
import com.cursosrecomendados.telegram.database.DatabaseManager;
import com.cursosrecomendados.telegram.mappers.PriceInfoConverter;
import com.cursosrecomendados.telegram.model.OrderBook;
import com.cursosrecomendados.telegram.model.PoloniexPair;
import com.cursosrecomendados.telegram.model.PriceInfo;
import com.cursosrecomendados.telegram.service.LanguagesService;
import com.cursosrecomendados.telegram.service.PoloniexServiceImpl;

@Component
public class CoinHandlers extends TelegramLongPollingBot {
	private static final String LOGTAG = "CRYPTOHANDLERS";

	private static final int STARTSTATE = 0;
	private static final int MAINMENU = 1;
	private static final int COININFO = 2;
	private static final int NEWCOININFO = 3;
	private static final int SETTINGS = 4;
	private static final int LANGUAGE = 5;

	@Autowired
	static PoloniexServiceImpl poloniex;

	@Autowired
	static PriceInfoConverter converter;

	public CoinHandlers() {
		super();
		converter = new PriceInfoConverter();
		poloniex = new PoloniexServiceImpl();
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
	
    private static SendMessage onCancelCommand(Long chatId, Integer userId, Integer messageId, ReplyKeyboard replyKeyboard, String language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText(LanguagesService.getString("backToMainMenu", language));

        DatabaseManager.getInstance().insertCoinState(userId, chatId, MAINMENU);
        return sendMessage;
    }

	private synchronized void handleIncomingMessage(Message message) throws TelegramApiException {
		final int state = DatabaseManager.getInstance().getCoinState(message.getFrom().getId(), message.getChatId());
		final String language = DatabaseManager.getInstance().getUserCoinOptions(message.getFrom().getId())[0];
		if (!message.isUserMessage() && message.hasText()) {
			if (isCommandForOther(message.getText())) {
				return;
			} else if (message.getText().startsWith(Commands.STOPCOMMAND)) {
				sendHideKeyboard(message.getFrom().getId(), message.getChatId(), message.getMessageId());
				return;
			}
		}
		SendMessage sendMessageRequest;
		switch (state) {
		case MAINMENU:
			sendMessageRequest = messageOnMainMenu(message, language);
			break;
		case COININFO:
		case NEWCOININFO:
			sendMessageRequest = messageOnCoin(message, language, state);
			break;
		case SETTINGS:
			sendMessageRequest = messageOnSetting(message, language);
			break;
		case LANGUAGE:
			sendMessageRequest = messageOnLanguage(message, language);
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
		boolean isCommandForMe = text.equals("/start@CoinInfo21_bot") || text.equals("/help@CoinInfo21_bot")
				|| text.equals("/stop@CoinInfo21_bot");
		return text.startsWith("/") && !isSimpleCommand && !isCommandForMe;
	}

	private static SendMessage messageOnCoin(Message message, String language, int state) {
		 SendMessage sendMessageRequest = null;
	        switch(state) {
	        case COININFO:
	        	sendMessageRequest = onCoin(message, language);
	        	break;
	        case NEWCOININFO:
	        	sendMessageRequest = onNewCoin(message, language);
	        	break;
	        }
		return sendMessageRequest;
	}
	
	private static SendMessage onCoin(Message message, String language) {
        SendMessage sendMessageRequest = null;
        if (message.hasText()) {
            if (message.getText().equals(getNewCommand(language))) {
                sendMessageRequest = onNewForecastWeatherCommand(message.getChatId(), message.getFrom().getId(), message.getMessageId(), language);
            }else if(message.getText().equals(getNews(language))) {
            	sendMessageRequest = onNews(message.getChatId(), message.getFrom().getId(), message.getMessageId(), language);
            }else{
                sendMessageRequest = onCancelCommand(message.getChatId(), message.getFrom().getId(), message.getMessageId(),
                        getMainMenuKeyboard(language), language);
            }
        }
        return sendMessageRequest;
    }
	
	private static SendMessage onNewForecastWeatherCommand(Long chatId, Integer userId, Integer messageId, String language) {
        ForceReplyKeyboard forceReplyKeyboard = getForceReply();

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(forceReplyKeyboard);
        sendMessage.setText(LanguagesService.getString("onCoinNewCommand", language));

        DatabaseManager.getInstance().insertCoinState(userId, chatId, NEWCOININFO);
        return sendMessage;
    }
	
	private static SendMessage onNews(Long chatId, Integer userId, Integer messageId, String language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(getMainMenuKeyboard(language));
        String url = "https://t.co/4HIiSgmKpD";
        sendMessage.setText(LanguagesService.getString("newsInfo", language)+"\n "+url);

        DatabaseManager.getInstance().insertCoinState(userId, chatId, MAINMENU);
        return sendMessage;
    }
	
	private static SendMessage onNewCoin(Message message, String language) {
		if (message.isReply()) {
			return onCoinReceived(message.getChatId(), message.getFrom().getId(), message.getMessageId(),
					message.getText(), language);
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

	private static ReplyKeyboardMarkup getSettingsKeyboard(String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		keyboardFirstRow.add(getLanguagesCommand(language));
		keyboardFirstRow.add(getBackCommand(language));
		keyboard.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboard);

		return replyKeyboardMarkup;
	}

	private static ReplyKeyboardMarkup getRecentsKeyboard(Integer userId, String language) {
        return getRecentsKeyboard(userId, language, true);
    }
	
	private static ReplyKeyboardMarkup getRecentsKeyboard(Integer userId, String language, boolean allowNew) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String recentCoin : DatabaseManager.getInstance().getRecentCoin(userId)) {
            KeyboardRow row = new KeyboardRow();
            row.add(recentCoin);
            keyboard.add(row);
        }

        KeyboardRow row = new KeyboardRow();
        if (allowNew) {
            row.add(getNewCommand(language));
            keyboard.add(row);

            row = new KeyboardRow();
        }
        row.add(getNews(language));
        row.add(getCancelCommand(language));
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
	
	private static ReplyKeyboardMarkup getLanguagesKeyboard(String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboard = new ArrayList<>();
		for (String languageName : LanguagesService.getSupportedLanguages().stream()
				.map(LanguagesService.Language::getName).collect(Collectors.toList())) {
			KeyboardRow row = new KeyboardRow();
			row.add(languageName);
			keyboard.add(row);
		}

		KeyboardRow row = new KeyboardRow();
		row.add(getCancelCommand(language));
		keyboard.add(row);
		replyKeyboardMarkup.setKeyboard(keyboard);

		return replyKeyboardMarkup;
	}
	
	 private static ForceReplyKeyboard getForceReply() {
	        ForceReplyKeyboard forceReplyKeyboard = new ForceReplyKeyboard();
	        forceReplyKeyboard.setSelective(true);
	        return forceReplyKeyboard;
	    }

	private static SendMessage sendMessageDefault(Message message, String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(language);
		DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(), 1);
		return sendHelpMessage(message.getChatId(), message.getMessageId(), replyKeyboardMarkup, language);
	}

	private static SendMessage messageOnMainMenu(Message message, String language) {
		SendMessage sendMessageRequest;
		if (message.hasText()) {
			if (message.getText().equals(getCoinCommand(language))) {
				sendMessageRequest = onCoinChoosen(message, language);
			} else if (message.getText().equals(getSettingsCommand(language))) {
				sendMessageRequest = onSettingsChoosen(message, language);
			} else {
				sendMessageRequest = sendChooseOptionMessage(message.getChatId(), message.getMessageId(),
						getMainMenuKeyboard(language), language);
			}
		} else {
			sendMessageRequest = sendChooseOptionMessage(message.getChatId(), message.getMessageId(),
					getMainMenuKeyboard(language), language);
		}

		return sendMessageRequest;
	}

	private static SendMessage onSettingsChoosen(Message message, String language) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		ReplyKeyboardMarkup replyKeyboardMarkup = getSettingsKeyboard(language);
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		sendMessage.setReplyToMessageId(message.getMessageId());
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(getSettingsMessage(language));

		DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(), SETTINGS);
		return sendMessage;
	}

	private static SendMessage onCoinChoosen(Message message, String language) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		ReplyKeyboardMarkup replyKeyboardMarkup = getRecentsKeyboard(message.getFrom().getId(), language);
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		sendMessage.setReplyToMessageId(message.getMessageId());
		sendMessage.setChatId(message.getChatId());
		
		sendMessage.setText(LanguagesService.getString("onCoinInfo", language));
		
		DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(),
				COININFO);
		return sendMessage;
	}

	private static SendMessage onCoinReceived(Long chatId, Integer userId, Integer messageId, String text,
			String language) {
		PoloniexPair valor = null;
		try {
			valor = PoloniexPair.valueOf(text);
		} catch (Exception e) {
			valor = PoloniexPair.BTC_LTC;
		}

		OrderBook coin = poloniex.getOrderBook(valor);
		// TODO
		// Aqui has de covertir OrderBook en PriceInfo, guardar el PriceInfo a la Base
		// de dades i retornar el
		// valor
		PriceInfo priceInfo = converter.convert(valor, coin);

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.enableMarkdown(true);
		sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(language));
		sendMessageRequest.setReplyToMessageId(messageId);
		sendMessageRequest.setText(priceInfo.toString());
		sendMessageRequest.setChatId(chatId.toString());
		DatabaseManager.getInstance().insertCoinState(userId, chatId, 1);
		return sendMessageRequest;
	}

	private static SendMessage messageOnSetting(Message message, String language) {
		SendMessage sendMessageRequest = null;
		if (message.hasText()) {
			if (message.getText().startsWith(getLanguagesCommand(language))) {
				sendMessageRequest = onLanguageCommand(message, language);
			} else if (message.getText().startsWith(getBackCommand(language))) {
				sendMessageRequest = sendMessageDefault(message, language);
			} else {
				sendMessageRequest = sendChooseOptionMessage(message.getChatId(), message.getMessageId(),
						getSettingsKeyboard(language), language);
			}
		}
		return sendMessageRequest;
	}

	private static SendMessage messageOnLanguage(Message message, String language) {
		SendMessage sendMessageRequest = null;
		if (message.hasText()) {
			if (message.getText().trim().equals(getCancelCommand(language))) {
				sendMessageRequest = onBackLanguageCommand(message, language);
			} else if (LanguagesService.getLanguageByName(message.getText().trim()) != null) {
				sendMessageRequest = onLanguageChosen(message.getFrom().getId(), message.getChatId(),
						message.getMessageId(), message.getText().trim());
			} else {
				sendMessageRequest = onLanguageError(message.getChatId(), message.getMessageId(), language);
			}
		}
		return sendMessageRequest;
	}

	private static SendMessage onBackLanguageCommand(Message message, String language) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		ReplyKeyboardMarkup replyKeyboardMarkup = getSettingsKeyboard(language);
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		sendMessage.setReplyToMessageId(message.getMessageId());
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(getSettingsMessage(language));

		DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(), SETTINGS);
		return sendMessage;
	}

	private static SendMessage onLanguageError(Long chatId, Integer messageId, String language) {
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.enableMarkdown(true);
		sendMessageRequest.setChatId(chatId.toString());
		sendMessageRequest.setReplyMarkup(getLanguagesKeyboard(language));
		sendMessageRequest.setText(LanguagesService.getString("errorLanguageNotFound", language));
		sendMessageRequest.setReplyToMessageId(messageId);

		return sendMessageRequest;
	}

	private static SendMessage onLanguageChosen(Integer userId, Long chatId, Integer messageId, String language) {
		String languageCode = LanguagesService.getLanguageCodeByName(language);
		DatabaseManager.getInstance().putUserCoinLanguageOption(userId, languageCode);

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.enableMarkdown(true);
		sendMessageRequest.setChatId(chatId.toString());
		sendMessageRequest.setText(LanguagesService.getString("languageUpdated", languageCode));
		sendMessageRequest.setReplyToMessageId(messageId);
		sendMessageRequest.setReplyMarkup(getMainMenuKeyboard(languageCode));

		DatabaseManager.getInstance().insertCoinState(userId, chatId, 1);
		return sendMessageRequest;
	}

	private static SendMessage sendHelpMessage(Long chatId, Integer messageId, ReplyKeyboardMarkup replyKeyboardMarkup,
			String language) {
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

	private static SendMessage sendChooseOptionMessage(Long chatId, Integer messageId, ReplyKeyboard replyKeyboard,
			String language) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(chatId.toString());
		sendMessage.setReplyToMessageId(messageId);
		sendMessage.setReplyMarkup(replyKeyboard);
		sendMessage.setText(LanguagesService.getString("chooseOption", language));

		return sendMessage;
	}

	private static SendMessage onLanguageCommand(Message message, String language) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		sendMessage.setReplyToMessageId(message.getMessageId());
		sendMessage.setChatId(message.getChatId());
		sendMessage.setReplyMarkup(getLanguagesKeyboard(language));
		sendMessage.setText(getLanguageMessage(language));

		DatabaseManager.getInstance().insertCoinState(message.getFrom().getId(), message.getChatId(), LANGUAGE);
		return sendMessage;
	}

	private static String getSettingsCommand(String language) {
		return LanguagesService.getString("settings", language);
	}

	private static String getCoinCommand(String language) {
		return LanguagesService.getString("coin", language);
	}
	
	private static String getNewCommand(String language) {
		return LanguagesService.getString("new", language);
	}
	
	private static String getNews(String language) {
		return LanguagesService.getString("news", language);
	}
	
	private static String getSettingsMessage(String language) {
		String baseString = LanguagesService.getString("onSettingsCommand", language);
		return baseString;
	}

	private static String getLanguageMessage(String language) {
		String baseString = LanguagesService.getString("selectLanguage", language);
		baseString = String.format(baseString, language);
		return baseString;
	}

	private static String getHelpMessage(String language) {
		String baseString = LanguagesService.getString("helpCoinMessage", language);
		return baseString;
	}

	private static String getLanguagesCommand(String language) {
		String baseString = LanguagesService.getString("languages", language);
		return baseString;
	}

	private static String getCancelCommand(String language) {
		String baseString = LanguagesService.getString("cancel", language);
		return baseString;
	}

	private static String getBackCommand(String language) {
		String baseString = LanguagesService.getString("back", language);
		return baseString;
	}

}
