package com.godel.repository.model;

public enum Timeframe {
    C_30("30_min_candles"),
    C_60("60_min_candles"),
    C_120("120_min_candles");

    private final String collectionName;

    Timeframe(String collectionName){
        this.collectionName = collectionName;
    }
    
    public String getCollectionName(){
        return collectionName;
    }
}
