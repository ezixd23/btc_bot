package com.cursosrecomendados.telegram.domain.Events;

import com.cursosrecomendados.telegram.domain.ExchangeRates.Rate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRates {
    @JsonProperty("rates")
    private Map<String, Rate> rates;

}