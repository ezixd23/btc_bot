package com.cursosrecomendados.telegram.mappers;

import com.cursosrecomendados.telegram.model.OrderBook;
import com.cursosrecomendados.telegram.model.PoloniexPair;
import com.cursosrecomendados.telegram.model.PriceInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PriceInfoConverter {

    public PriceInfo convert(PoloniexPair pair, OrderBook orderBook) {
        //TODO: Fer test
        PriceInfo res=new PriceInfo();
        res.setPair(pair.toString());
        res.setPriceAsk(getAskPrice(orderBook.getAsks().get(0)));
        res.setPriceBid(getBidPrice(orderBook.getBids().get(0)));
        return res;
    }

    private BigDecimal getAskPrice(List<String> poloniexData) {
        return BigDecimal.valueOf(Double.parseDouble(poloniexData.get(0)));
    }

    private BigDecimal getBidPrice(List<String> poloniexData) {
        return BigDecimal.valueOf(Double.parseDouble(poloniexData.get(0)));
    }

}
