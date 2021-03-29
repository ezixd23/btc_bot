package com.cursosrecomendados.telegram.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.cursosrecomendados.telegram.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "price_info", schema = "public" )
public class PriceInfo extends BaseEntity {
	
	@JsonProperty("pair")
	@Column(name = "pair")
	String pair;
	
	@JsonProperty("price_ask")
	@Column(name = "price_ask", precision=12, scale=8)
	BigDecimal priceAsk;
	
	@JsonProperty("price_bid")
	@Column(name = "price_bid", precision=12, scale=8)
	BigDecimal priceBid;
	
	public PriceInfo() {
		super.setCreatedAt(new Date());
	}
	
	@JsonIgnore
	public int getId() {
        return super.getId();
    }
	
	@JsonIgnore
	public Date getCreatedAt()
	{
		return super.getCreatedAt();
	}
	
	@JsonProperty("moment")
	public Long getMoment() {
		return super.getCreatedAt().getTime();
	}

	public void setMoment(Long moment) {
		super.setCreatedAt(new Date(moment));
	}

	@JsonProperty("pair")
	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	@JsonProperty("price_ask")
	public BigDecimal getPriceAsk() {
		return priceAsk;
	}
	
	@JsonProperty("price_ask")
	public void setPriceAsk(BigDecimal priceAsk) {
		this.priceAsk = priceAsk;
	}
	
	@JsonProperty("price_bid")
	public BigDecimal getPriceBid() {
		return priceBid;
	}
	
	@JsonProperty("price_bid")
	public void setPriceBid(BigDecimal priceBid) {
		this.priceBid = priceBid;
	}

	@Override
	public String toString() {
		return "PriceInfo [Price Ask=" + getPriceAsk() + ", Price Bid=" + getPriceBid() + "]";
	}
	
	
}





