package com.cursosrecomendados.telegram.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cursosrecomendados.telegram.model.Coin;

public interface CoinRepository extends CrudRepository<Coin,Long>{
	List <Coin> findByName(String title);
}
