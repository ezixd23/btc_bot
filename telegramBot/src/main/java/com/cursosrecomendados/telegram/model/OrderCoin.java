package com.cursosrecomendados.telegram.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "asks",
    "bids",
    "isFrozen",
    "seq"
})

public class OrderCoin {
	
    @JsonProperty("asks")
    private List<List<String>> asks = null;
    @JsonProperty("bids")
    private List<List<String>> bids = null;
    @JsonProperty("isFrozen")
    private String isFrozen;
    @JsonProperty("seq")
    private Integer seq;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonProperty("asks")
    public List<List<String>> getAsks() {
        return asks;
    }
    
    @JsonProperty("asks")
    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }
    
    @JsonProperty("bids")
    public List<List<String>> getBids() {
        return bids;
    }
   
    @JsonProperty("bids")
    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }
    
    @JsonProperty("isFrozen")
    public String getIsFrozen() {
        return isFrozen;
    }
    
    @JsonProperty("isFrozen")
    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }
    
    @JsonProperty("seq")
    public Integer getSeq() {
        return seq;
    }
    
    @JsonProperty("seq")
    public void setSeq(Integer seq) {
        this.seq = seq;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
}
