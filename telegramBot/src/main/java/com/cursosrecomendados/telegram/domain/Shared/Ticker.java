package com.cursosrecomendados.telegram.domain.Shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
    @JsonProperty("base")
    private String base;
    @JsonProperty("target")
    private String target;
    @JsonProperty("market")
    private Market market;
    @JsonProperty("last")
    private double last;
    @JsonProperty("volume")
    private double volume;
    @JsonProperty("converted_last")
    private Map<String, String> convertedLast;
    @JsonProperty("converted_volume")
    private Map<String, String> convertedVolume;
    @JsonProperty("trust_score")
    private String trustScore;
    @JsonProperty("bid_ask_spread_percentage")
    private double bidAskSpreadPercentage;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("last_traded_at")
    private String lastTradedAt;
    @JsonProperty("last_fetch_at")
    private String lastFetchAt;
    @JsonProperty("is_anomaly")
    private boolean isAnomaly;
    @JsonProperty("is_stale")
    private boolean isStale;
    @JsonProperty("trade_url")
    private String tradeUrl;
    @JsonProperty("coin_id")
    private String coinId;
	@Override
	
	public String toString() {
		return "Ticker [base=" + base + ", target=" + target + ", last=" + last + ", volume="
				+ volume + ", bidAskSpreadPercentage=" + bidAskSpreadPercentage + ", timestamp=" + timestamp + "]";
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
    
    
}
