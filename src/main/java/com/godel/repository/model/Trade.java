package com.godel.repository.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;


@MongoEntity(collection = "tester_trades")
public class Trade extends PanacheMongoEntity{

    @BsonProperty("entry_time")
    private long entryTime;
    @BsonProperty("entry_price")
    private double entryPrice;
    @BsonProperty("entry_amount")
    private double entryAmount;
    @BsonProperty("exit_time")
    private long exitTime;
    @BsonProperty("exit_price")
    private double exitPrice;
    @BsonProperty("exit_amount")
    private double exitAmount;
    @BsonProperty("trade_type")
    private String tradeType;
    @BsonProperty("commission")
    private double commission;
    @BsonProperty("profit")
    private double profit;
    @BsonProperty("profit_without_commission")
    private double profitWithoutCommission;
    @BsonProperty("entry_ema100")
    private double entryEma100;
    @BsonProperty("exit_ema100")
    private double exitEma100;
    @BsonProperty("open_vix")
    private double openVix;
    @BsonProperty("close_vix")
    private double closeVix;

    
    
    public Trade(){
        
    }
    public Trade(long entryTime, double entryPrice, double entryAmount, long exitTime, double exitPrice,
            double exitAmount, String tradeType, double commission, double profit,
            double profitWithoutCommission, double entryEma100, double exitEma100, double openVix, double closeVix) {
        this.entryTime = entryTime;
        this.entryPrice = entryPrice;
        this.entryAmount = entryAmount;
        this.exitTime = exitTime;
        this.exitPrice = exitPrice;
        this.exitAmount = exitAmount;
        this.tradeType = tradeType;
        this.commission = commission;
        this.profit = profit;
        this.profitWithoutCommission = profitWithoutCommission;
        this.entryEma100 = entryEma100;
        this.exitEma100 = exitEma100;
        this.openVix = openVix;
        this.closeVix = closeVix;
    }



    public long getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }
    public double getEntryPrice() {
        return entryPrice;
    }
    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }
    public double getEntryAmount() {
        return entryAmount;
    }
    public void setEntryAmount(double entryAmount) {
        this.entryAmount = entryAmount;
    }
    public long getExitTime() {
        return exitTime;
    }
    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }
    public double getExitPrice() {
        return exitPrice;
    }
    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }
    public double getExitAmount() {
        return exitAmount;
    }
    public void setExitAmount(double exitAmount) {
        this.exitAmount = exitAmount;
    }
    public String getTradeType() {
        return tradeType;
    }
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
    public double getCommission() {
        return commission;
    }
    public void setCommission(double commission) {
        this.commission = commission;
    }
    public double getProfit() {
        return profit;
    }
    public void setProfit(double profit) {
        this.profit = profit;
    }
    public double getProfitWithoutCommission() {
        return profitWithoutCommission;
    }
    public void setProfitWithoutCommission(double profitWithoutCommission) {
        this.profitWithoutCommission = profitWithoutCommission;
    }
    public double getEntryEma100() {
        return entryEma100;
    }
    public void setEntryEma100(double entryEma100) {
        this.entryEma100 = entryEma100;
    }
    public double getExitEma100() {
        return exitEma100;
    }
    public void setExitEma100(double exitEma100) {
        this.exitEma100 = exitEma100;
    }
    public double getOpenVix() {
        return openVix;
    }
    public void setOpenVix(double openVix) {
        this.openVix = openVix;
    }
    public double getCloseVix() {
        return closeVix;
    }
    public void setCloseVix(double closeVix) {
        this.closeVix = closeVix;
    }
}
