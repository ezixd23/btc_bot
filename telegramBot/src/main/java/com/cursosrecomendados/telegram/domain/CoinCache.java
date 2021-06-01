package com.cursosrecomendados.telegram.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.cursosrecomendados.telegram.api.CoinGeckoApiClient;
import com.cursosrecomendados.telegram.api.CoinGeckoApiClientImpl;
import com.cursosrecomendados.telegram.domain.Coins.CoinTickerById;
import com.cursosrecomendados.telegram.domain.Shared.Ticker;

public class CoinCache {
	public static final String SEPARATOR_REGEX = ",";
	private static Map<String, Ticker> values=new  HashMap<String, Ticker>();
	static CoinGeckoApiClient client=new CoinGeckoApiClientImpl();
	
	public static synchronized Ticker getValue(String index) {
		Ticker tick = null;
		ZonedDateTime stamp;
		String[] coin = index.split(SEPARATOR_REGEX);
		if (values.containsKey(coin[0])) {
			tick = values.get(coin[0]);
			stamp = ZonedDateTime.parse(tick.getLastFetchAt(),DateTimeFormatter.ISO_DATE_TIME);
			if(anHourHasPassed(stamp)) {
				tick = getValueFromCoinGecko(index);
				values.replace(coin[0],tick);
			}
		}else {
			tick = getValueFromCoinGecko(index);
			values.put(coin[0], tick);
		}	
		return tick;	
	}
	
	public static boolean anHourHasPassed(ZonedDateTime t) {
		ZonedDateTime now = ZonedDateTime.now();
		Duration diff = Duration.between(t, now);
		return diff.toHours()>1;
	}
	
	public static Ticker getValueFromCoinGecko(String coinId) {
		CoinTickerById bitcoinTicker = null;
		String[] coin = coinId.split(SEPARATOR_REGEX);
		try {
			bitcoinTicker = client.getCoinTickerById(coin[0].trim().toLowerCase());
		} catch (Exception e) {
			bitcoinTicker = client.getCoinTickerById("bitcoin");
		}
		for (Ticker ticker : bitcoinTicker.getTickers()) {
			if (ticker.getTarget().equals(coin[1].trim().toUpperCase())) {
				return ticker;
			}
		}
		return null;
	}
}
