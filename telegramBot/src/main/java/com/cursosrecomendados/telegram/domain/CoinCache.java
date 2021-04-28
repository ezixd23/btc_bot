package com.cursosrecomendados.telegram.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.cursosrecomendados.telegram.api.CoinGeckoApiClient;
import com.cursosrecomendados.telegram.api.CoinGeckoApiClientImpl;
import com.cursosrecomendados.telegram.domain.Coins.CoinTickerById;
import com.cursosrecomendados.telegram.domain.Shared.Ticker;

public class CoinCache {
	
	private static Map<String, Ticker> values;
	static CoinGeckoApiClient client=new CoinGeckoApiClientImpl();
	
	public static synchronized Ticker getValue(String index) {
		Ticker tick = null;
		LocalDateTime stamp;
		String[] coin = index.split(", ");
		if (values.containsKey(coin[0])) {
			tick = values.get(coin[0]);
			stamp = LocalDateTime.parse(tick.getLastFetchAt(),DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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
	
	public static boolean anHourHasPassed(LocalDateTime t) {
		LocalDateTime now = LocalDateTime.now();
		Duration diff = Duration.between(t, now);
		return diff.toHours()>1;
	}
	
	public static Ticker getValueFromCoinGecko(String coinId) {
		CoinTickerById bitcoinTicker = null;
		String[] coin = coinId.split(", ");
		try {
			bitcoinTicker = client.getCoinTickerById(coin[0].toLowerCase());
		} catch (Exception e) {
			bitcoinTicker = client.getCoinTickerById("bitcoin");
		}
		for (Ticker ticker : bitcoinTicker.getTickers()) {
			if (ticker.getTarget().equals(coin[1].toUpperCase())) {
				return ticker;
			}
		}
		return null;
	}
}
