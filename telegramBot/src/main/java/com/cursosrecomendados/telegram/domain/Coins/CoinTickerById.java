package com.cursosrecomendados.telegram.domain.Coins;

import com.cursosrecomendados.telegram.domain.Shared.Ticker;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
public class CoinTickerById {
    @JsonProperty("name")
    private String name;
    @JsonProperty("tickers")
    private List<Ticker> tickers;
	@Override
	public String toString() {		
		return "CoinTickerById [name=" + name + ", tickers=" + tickers.toString() + "]";
	}
	
	public List<Ticker> getTickers() {
		return tickers;
	}
    
}
