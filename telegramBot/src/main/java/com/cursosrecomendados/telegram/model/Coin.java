package com.cursosrecomendados.telegram.model;

import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class Coin {
	
	@Basic
	private String name;
	@Basic
	private float value;
	@Basic
	private String currency;
	@Basic
	private float variation;
	@Basic
	private LocalDate date;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public float getVariation() {
		return variation;
	}
	public void setVariation(float variation) {
		this.variation = variation;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}
