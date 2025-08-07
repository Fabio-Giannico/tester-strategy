package com.godel.repository.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;


public class Candle extends PanacheMongoEntity  {

    @BsonProperty("open_time")
    private long openTime;
    @BsonProperty("open_price")
    private double openPrice;
    @BsonProperty("high_price")
    private double highPrice;
    @BsonProperty("low_price")
    private double lowPrice;
    @BsonProperty("close_price")
    private double closePrice;
    @BsonProperty("exchanged_asset_quantity")
    private double volume;
    @BsonProperty("close_time")
    private long closeTime;
    @BsonProperty("exchanged_currency_quantity")
    private double exchangedCurrencyQuantity;
    @BsonProperty("n_trades")
    private int nTrades;
    @BsonProperty("asset_qty_bought_by_taker")
    private double assetQuantityBoughtByTaker;
    @BsonProperty("currency_qty_spent_by_taker")
    private double currencyQuantitySpentByTaker;
    @BsonProperty("ignore")
    private int ignore;



    public Candle(){

    }

    public Candle(long openTime, double openPrice, double highPrice, double lowPrice, double closePrice,
            double volume, long closeTime, double exchangedCurrencyQuantity, int nTrades,
            double assetQuantityBoughtByTaker, double currencyQuantitySpentByTaker, int ignore) {
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.closeTime = closeTime;
        this.exchangedCurrencyQuantity = exchangedCurrencyQuantity;
        this.nTrades = nTrades;
        this.assetQuantityBoughtByTaker = assetQuantityBoughtByTaker;
        this.currencyQuantitySpentByTaker = currencyQuantitySpentByTaker;
        this.ignore = ignore;
    }



    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public double getExchangedCurrencyQuantity() {
        return exchangedCurrencyQuantity;
    }

    public void setExchangedCurrencyQuantity(double exchangedCurrencyQuantity) {
        this.exchangedCurrencyQuantity = exchangedCurrencyQuantity;
    }

    public int getnTrades() {
        return nTrades;
    }

    public void setnTrades(int nTrades) {
        this.nTrades = nTrades;
    }

    public double getAssetQuantityBoughtByTaker() {
        return assetQuantityBoughtByTaker;
    }

    public void setAssetQuantityBoughtByTaker(double assetQuantityBoughtByTaker) {
        this.assetQuantityBoughtByTaker = assetQuantityBoughtByTaker;
    }

    public double getCurrencyQuantitySpentByTaker() {
        return currencyQuantitySpentByTaker;
    }

    public void setCurrencyQuantitySpentByTaker(double currencyQuantitySpentByTaker) {
        this.currencyQuantitySpentByTaker = currencyQuantitySpentByTaker;
    }

    public int getIgnore() {
        return ignore;
    }

    public void setIgnore(int ignore) {
        this.ignore = ignore;
    }
}
