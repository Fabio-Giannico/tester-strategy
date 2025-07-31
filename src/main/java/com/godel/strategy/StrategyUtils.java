package com.godel.strategy;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.godel.utils.database.RedisService;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class StrategyUtils {

    @ConfigProperty(name = "STARTING_BALANCE")
    private int startingBalance;
    @ConfigProperty(name = "AMOUNT_PER_TRADE_PERCENTAGE")
    private Integer amountPerTradePercentage;
    @ConfigProperty(name = "COMMISSION_PER_OPERATION")
    private double commissionPerOperation;
    @ConfigProperty(name = "TAKE_PROFIT_PERCENTAGE")
    private double tpPercentage;
    @ConfigProperty(name = "STOP_LOSS_PERCENTAGE")
    private double slPercentage;


    private final RedisService redisService;

    public StrategyUtils(RedisService redisService){
        this.redisService = redisService;
    }



    public void setStartingBalance(){
        redisService.setValue("Balance", String.valueOf(startingBalance));
    }
    
    public double getEntryAmount(double entryPrice){
        double balance = getBalance();
        double percentage = amountPerTradePercentage / 100.0;
        
        double entryAmount = balance * percentage;
        redisService.setValue("EntryAmount", String.valueOf(entryAmount));

        double assetQty = entryAmount / entryPrice;
        redisService.setValue("AssetQty", String.valueOf(assetQty));

        return entryAmount;
    }

    public double getExitAmount(double exitPrice) {
        double assetQty = Double.parseDouble(redisService.getValue("AssetQty"));

        double exitAmount = assetQty * exitPrice;
        redisService.setValue("ExitAmount", String.valueOf(exitAmount));
        
        return exitAmount;
    }

    public double getCommission(double amount) {
        return amount * (commissionPerOperation / 100.0);
    }

    public double calculateProfit(String tradeType) {
        double entryAmount = Double.parseDouble(redisService.getValue("EntryAmount"));
        double exitAmount = Double.parseDouble(redisService.getValue("ExitAmount"));

        double profit = 0;

        if (tradeType.equals("SHORT")) {
            profit = entryAmount - exitAmount;
        }else {
            profit = exitAmount - entryAmount;
        }

        redisService.setValue("Profit", String.valueOf(profit));
        return profit;
    }

    public double getBalance(){
        return Double.parseDouble(redisService.getValue("Balance"));
    }

    public void setBalance(double newBalance){
        redisService.setValue("Balance", String.valueOf(newBalance));
    }

    public void updateBalanceOnCloseTrade(){
        double balance = getBalance();

        double entryAmount = Double.parseDouble(redisService.getValue("EntryAmount"));
        double entryCommission = getCommission(entryAmount);
        double exitAmount = Double.parseDouble(redisService.getValue("ExitAmount"));
        double exitCommission = getCommission(exitAmount);

        double profit = Double.parseDouble(redisService.getValue("Profit"));
        double totCommission = entryCommission + exitCommission;
        double profitWithoutCommission = profit - totCommission;
        double newBalance = balance + entryAmount + profitWithoutCommission;

        setBalance(newBalance);
    }

    public String getTradeId(){
        return redisService.getValue("TradeId");
    }

    public void setTradeId(String tradeId){
        redisService.setValue("TradeId", tradeId);
    }

    public void setStrategyFinalBalance(String strategy){
        redisService.setValue(strategy, String.valueOf(getBalance()));
    }

    public void setStrategyMaxDrowdown(String strategy, double maxDrowDown){
        String key = strategy + "MDD";
        redisService.setValue(key, String.valueOf(maxDrowDown));
    }

    public void updateTradesCounter(String strategy, int totTrades, int winningTrades){
        String tradesKey = strategy + "Trades";
        String winningTradesKey = strategy + "WinningTrades";
        redisService.setValue(tradesKey, String.valueOf(totTrades));
        redisService.setValue(winningTradesKey, String.valueOf(winningTrades));
    }
}
