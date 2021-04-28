package com.cursosrecomendados.telegram.domain.Exchanges;

import com.cursosrecomendados.telegram.domain.Shared.Ticker;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExchangeById extends Exchanges{
    @JsonProperty("tickers")
    private List<Ticker> tickers;
    @JsonProperty("status_updates")
    private List<Object> statusUpdates;
	public List<Ticker> getTickers() {
		return tickers;
	}
	public void setTickers(List<Ticker> tickers) {
		this.tickers = tickers;
	}
	public List<Object> getStatusUpdates() {
		return statusUpdates;
	}
	public void setStatusUpdates(List<Object> statusUpdates) {
		this.statusUpdates = statusUpdates;
	}

}
