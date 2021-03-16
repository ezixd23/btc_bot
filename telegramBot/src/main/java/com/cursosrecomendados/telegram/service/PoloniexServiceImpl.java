package com.cursosrecomendados.telegram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cursosrecomendados.telegram.model.OrderBook;
import com.cursosrecomendados.telegram.model.PoloniexPair;

@Service("poloniexService")
public class PoloniexServiceImpl  {
	
	private static Logger logger = LoggerFactory.getLogger(PoloniexServiceImpl.class);

	@Value("${poloniex.public.timeout:5000}")
	Integer poloniexTimeout;	
	
	@Value("${poloniex.public.url}")
	String poloniexPublicUrl;
	
	@Value("${poloniex.public.orderbook}")
	String poloniexPublicOrderBookCommand;
	
	RestTemplate restTemplate = restTemplate();
	
	public static final String DOLLAR = "dollar";
    public static final String EURO = "euro";

    public PoloniexServiceImpl() {}

	public PoloniexServiceImpl(Integer poloniexTimeout,String poloniexPublicUrl,String poloniexPublicOrderBookCommand) {
    	this.poloniexTimeout = poloniexTimeout;
    	this.poloniexPublicUrl = poloniexPublicUrl;
    	this.poloniexPublicOrderBookCommand = poloniexPublicOrderBookCommand;
	}

	@Bean
    public RestTemplate restTemplate() {
     
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        return restTemplate;
    }
     
    @Bean
	public ClientHttpRequestFactory getClientHttpRequestFactory() {
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
	      = new HttpComponentsClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(poloniexTimeout==null? 5000: poloniexTimeout);
	    return clientHttpRequestFactory;
	}
	
	public OrderBook getOrderBook(PoloniexPair pair) {
		try {
			ResponseEntity<OrderBook> response=restTemplate.getForEntity(poloniexPublicUrl+poloniexPublicOrderBookCommand+pair.toString(), OrderBook.class);
			return response.getBody();
		}
		catch(Exception ex) {
			logger.error("Error connecting to poloniex:"+ex.getMessage());
			return null;
		}
	}
	
}
