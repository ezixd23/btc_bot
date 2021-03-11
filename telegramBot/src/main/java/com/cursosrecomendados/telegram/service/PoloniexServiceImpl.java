package com.cursosrecomendados.telegram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cursosrecomendados.telegram.model.OrderCoin;
import com.cursosrecomendados.telegram.model.PoloniexPair;

@Service("poloniexService")
public class PoloniexServiceImpl  {
	
	private static Logger logger = LoggerFactory.getLogger(PoloniexServiceImpl.class);
	@Value("${poloniex.public.timeout}")
	Integer poloniexTimeout;	
	
	@Value("${poloniex.public.url}")
	String poloniexPublicUrl;
	
	@Value("${poloniex.public.orderbook}")
	String poloniexPublicOrderBookCommand;
	RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
	 
	private ClientHttpRequestFactory getClientHttpRequestFactory() {
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
	      = new HttpComponentsClientHttpRequestFactory();
	    clientHttpRequestFactory.setConnectTimeout(poloniexTimeout==null? 5000: poloniexTimeout);
	    return clientHttpRequestFactory;
	}
	
	
	public OrderCoin getOrderBook(PoloniexPair pair) {
		try {
			ResponseEntity<OrderCoin> response=restTemplate.getForEntity(poloniexPublicUrl+poloniexPublicOrderBookCommand+pair.toString(), OrderCoin.class);
			return response.getBody();
		}
		catch(Exception ex) {
			logger.error("Error connecting to poloniex:"+ex.getMessage());
			return null;
		}
	}
	
}
