package com.cursosrecomendados.telegram.telegramBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import com.cursosrecomendados.telegram.model.PoloniexPair;
import com.cursosrecomendados.telegram.model.OrderBook;
import com.cursosrecomendados.telegram.service.PoloniexServiceImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class PoloniexTest {

	@Value("${poloniex.public.timeout}")
	Integer poloniexTimeout;

	@Value("${poloniex.public.url}")
	String poloniexPublicUrl;

	@Value("${poloniex.public.orderbook}")
	String poloniexPublicOrderBookCommand;

	PoloniexServiceImpl poloniexService;

	@BeforeEach
	public void setUp() throws Exception {
		Properties configProps = readProperties();
		poloniexService = new PoloniexServiceImpl(Integer.parseInt(configProps.getProperty("poloniex.public.timeout")),configProps.getProperty("poloniex.public.url"), configProps.getProperty("poloniex.public.orderbook"));
	}

	@Test
	public void testGetOrderBook() throws Exception {
		OrderBook result = poloniexService.getOrderBook(PoloniexPair.BTC_LTC);
		Assertions.assertNotNull(result);
	}


	private Properties readProperties() throws IOException {
		Properties configProps = new Properties();
		InputStream iStream = new ClassPathResource("application-test.properties").getInputStream();
		configProps.load(iStream);
		return configProps;
	}
}
