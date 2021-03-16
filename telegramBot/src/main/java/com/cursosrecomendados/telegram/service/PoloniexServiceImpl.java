package com.cursosrecomendados.telegram.service;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cursosrecomendados.telegram.model.PriceInfo;
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
	
	@Autowired
	CloseableHttpClient httpClient;
	
	RestTemplate restTemplate = restTemplate();
	
	public static final String DOLLAR = "dollar";
    public static final String EURO = "euro";
	
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
	    clientHttpRequestFactory.setHttpClient(httpClient);
	    return clientHttpRequestFactory;
	}
	
	public PriceInfo getOrderBook(PoloniexPair pair) {
		try {
			ResponseEntity<PriceInfo> response=restTemplate.getForEntity(poloniexPublicUrl+poloniexPublicOrderBookCommand+pair.toString(), PriceInfo.class);
			return response.getBody();
		}
		catch(Exception ex) {
			logger.error("Error connecting to poloniex:"+ex.getMessage());
			return null;
		}
	}
	
}
