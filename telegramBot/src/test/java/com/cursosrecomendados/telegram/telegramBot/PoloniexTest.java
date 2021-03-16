package com.cursosrecomendados.telegram.telegramBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cursosrecomendados.telegram.model.PoloniexPair;
import com.cursosrecomendados.telegram.model.PriceInfo;
import com.cursosrecomendados.telegram.service.PoloniexServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true",classes= Application.class)
public class PoloniexTest {
	PoloniexServiceImpl test = new PoloniexServiceImpl();
	
	@Test
	public void order() {
		Assertions.assertNotNull(test.getOrderBook(PoloniexPair.BTC_PASC));
	}
}
