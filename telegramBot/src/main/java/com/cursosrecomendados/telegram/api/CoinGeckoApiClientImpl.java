package com.cursosrecomendados.telegram.api;

import java.util.List;
import java.util.Map;

import com.cursosrecomendados.telegram.domain.Ping;
import com.cursosrecomendados.telegram.domain.Coins.*;
import com.cursosrecomendados.telegram.domain.Events.*;
import com.cursosrecomendados.telegram.domain.Exchanges.*;
import com.cursosrecomendados.telegram.domain.Global.Global;
import com.cursosrecomendados.telegram.domain.Status.StatusUpdates;
import com.cursosrecomendados.telegram.service.CoinGeckoApiService;


public class CoinGeckoApiClientImpl implements CoinGeckoApiClient {
    private CoinGeckoApiService coinGeckoApiService;
    private CoinGeckoApi coinGeckoApi;

    public CoinGeckoApiClientImpl(){
        this.coinGeckoApi = new CoinGeckoApi();
        this.coinGeckoApiService = coinGeckoApi.createService(CoinGeckoApiService.class);

    }

    public Ping ping(){
        return coinGeckoApi.executeSync(coinGeckoApiService.ping());
    }

    public Map<String, Map<String, Double>> getPrice(String ids, String vsCurrencies){
        return getPrice(ids, vsCurrencies, false, false, false,  false);
    }

    public Map<String, Map<String, Double>> getPrice(String ids, String vsCurrencies, boolean includeMarketCap, boolean include24hrVol, boolean include24hrChange, boolean includeLastUpdatedAt) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getPrice(ids, vsCurrencies,includeMarketCap, include24hrVol, include24hrChange, includeLastUpdatedAt));
    }

    
    public Map<String, Map<String, Double>> getTokenPrice(String id, String contractAddress, String vsCurrencies) {
        return getTokenPrice(id,contractAddress,vsCurrencies,false,false,false,false);
    }

    
    public Map<String, Map<String, Double>> getTokenPrice(String id, String contractAddress, String vsCurrencies, boolean includeMarketCap, boolean include24hrVol, boolean include24hrChange, boolean includeLastUpdatedAt) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getTokenPrice(id,contractAddress,vsCurrencies,includeMarketCap,include24hrVol,include24hrChange,includeLastUpdatedAt));
    }

    
    public List<String> getSupportedVsCurrencies(){
        return coinGeckoApi.executeSync(coinGeckoApiService.getSupportedVsCurrencies());
    }

    
    public List<CoinList> getCoinList() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinList());
    }

    
    public List<CoinMarkets> getCoinMarkets(String vsCurrency) {
        return getCoinMarkets(vsCurrency,null,null,null,null,false,null);
    }

    
    public List<CoinMarkets> getCoinMarkets(String vsCurrency, String ids, String order, Integer perPage, Integer page, boolean sparkline, String priceChangePercentage) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinMarkets(vsCurrency,ids,order,perPage,page,sparkline,priceChangePercentage));
    }

    
    public CoinFullData getCoinById(String id) {
        return getCoinById(id,true,true,true,true,true,false);
    }

    
    public CoinFullData getCoinById(String id, boolean localization, boolean tickers, boolean marketData, boolean communityData, boolean developerData, boolean sparkline) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinById(id,localization,tickers,marketData,communityData,developerData,sparkline));
    }

    
    public CoinTickerById getCoinTickerById(String id) {
        return getCoinTickerById(id,null,null,null);
    }

    
    public CoinTickerById getCoinTickerById(String id, String exchangeIds, Integer page, String order) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinTickerById(id,exchangeIds,page,order));
    }

    public CoinHistoryById getCoinHistoryById(String id, String date) {
        return getCoinHistoryById(id,date,true);
    }

    
    public CoinHistoryById getCoinHistoryById(String id, String data, boolean localization) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinHistoryById(id,data,localization));
    }

    
    public MarketChart getCoinMarketChartById(String id, String vsCurrency, Integer days) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinMarketChartById(id,vsCurrency,days));
    }

    
    public MarketChart getCoinMarketChartRangeById(String id, String vsCurrency, String from, String to) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinMarketChartRangeById(id,vsCurrency,from,to));
    }

    
    public StatusUpdates getCoinStatusUpdateById(String id) {
        return getCoinStatusUpdateById(id,null,null);
    }

    
    public StatusUpdates getCoinStatusUpdateById(String id, Integer perPage, Integer page) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinStatusUpdateById(id,perPage,page));
    }

    
    public CoinFullData getCoinInfoByContractAddress(String id, String contractAddress) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getCoinInfoByContractAddress(id,contractAddress));
    }

    
    public List<Exchanges> getExchanges() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchanges());
    }

    
    public List<ExchangesList> getExchangesList() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangesList());
    }

    
    public ExchangeById getExchangesById(String id) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangesById(id));
    }

    
    public ExchangesTickersById getExchangesTickersById(String id) {
        return getExchangesTickersById(id,null,null,null);
    }

    
    public ExchangesTickersById getExchangesTickersById(String id, String coinIds, Integer page, String order) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangesTickersById(id,coinIds,page,order));
    }

    
    public StatusUpdates getExchangesStatusUpdatesById(String id) {
        return getExchangesStatusUpdatesById(id,null,null);
    }

    
    public StatusUpdates getExchangesStatusUpdatesById(String id, Integer perPage, Integer page) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangesStatusUpdatesById(id,perPage,page));
    }

    
    public List<List<String>> getExchangesVolumeChart(String id, Integer days) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangesVolumeChart(id,days));
    }

    
    public StatusUpdates getStatusUpdates() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getStatusUpdates());
    }

    
    public StatusUpdates getStatusUpdates(String category, String projectType, Integer perPage, Integer page) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getStatusUpdates(category, projectType,perPage,page));
    }

    
    public Events getEvents() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getEvents());
    }

    
    public Events getEvents(String countryCode, String type, Integer page, boolean upcomingEventsOnly, String fromDate, String toDate) {
        return coinGeckoApi.executeSync(coinGeckoApiService.getEvents(countryCode,type,page,upcomingEventsOnly,fromDate,toDate));
    }

    
    public EventCountries getEventsCountries() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getEventsCountries());
    }

    
    public EventTypes getEventsTypes() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getEventsTypes());
    }

    
    public ExchangeRates getExchangeRates() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getExchangeRates());
    }

    
    public Global getGlobal() {
        return coinGeckoApi.executeSync(coinGeckoApiService.getGlobal());
    }
}
