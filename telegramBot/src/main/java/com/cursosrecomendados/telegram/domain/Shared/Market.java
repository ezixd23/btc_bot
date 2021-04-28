package com.cursosrecomendados.telegram.domain.Shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Market {
    @JsonProperty("name")
    private String name;
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("has_trading_incentive")
    private boolean hasTradingIncentive;
	@Override
	public String toString() {
		return "Market [name=" + name + ", identifier=" + identifier + ", hasTradingIncentive=" + hasTradingIncentive
				+ "]";
	}
    
    
}
