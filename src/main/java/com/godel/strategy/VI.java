package com.godel.strategy;

import java.util.List;
import com.godel.repository.TradeRepository;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Trade;
import com.godel.utils.indicator.VIX;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class VI {

    private final int initializeIndicatorsIndex = 35;

    private boolean tradeIsOpen = false;
    private String tradeType = "";
    private int winningTrades = 0;
    private int totTrades = 0;
    private double maxDrowDown = 999999;

    private final VIX vix;
    private final StrategyUtils utils;
    private final TradeRepository tradeRepository;

    public VI(VIX vix, StrategyUtils utils, TradeRepository tradeRepository){
        this.vix = vix;
        this.utils = utils;
        this.tradeRepository = tradeRepository;
    }



    public void executeStrategy(List<Candle> candles){
        List<Candle> subCandles = candles.subList(0, initializeIndicatorsIndex);
        initializeIndicators(subCandles);

        for(int i = initializeIndicatorsIndex; i < candles.size(); i++){
            Candle current = candles.get(i);
            vix.update(current);

            if (tradeIsOpen && areGoodConditionsToExit(current)) {
                closeTrade(current);
            }

            if (!tradeIsOpen) {
                setTradeType(current);
                if (!tradeType.equals("")) {
                    openTrade(current);
                }
            }            

            if (i == candles.size() - 1) {
                updateTradesCounter();
                if (tradeIsOpen) {
                    closeTrade(current);
                }
                utils.setStrategyFinalBalance("Vi");
                utils.setStrategyMaxDrowdown("Vi", maxDrowDown);
            }
        }
    }

    

    private void initializeIndicators(List<Candle> candles){
        vix.initialize(candles);

        utils.setStartingBalance();
    }

    private void setTradeType(Candle c){
        double vIndex = vix.get();

        if (vIndex > c.getClosePrice()) {
            tradeType = "SHORT";
        } else if (vIndex < c.getClosePrice()) {
            tradeType = "LONG";
        } else {
            tradeType = "";
        }
    }

    private boolean areGoodConditionsToExit(Candle c){
        double vIndex = vix.get();

        if (tradeType.equals("SHORT") && vIndex < c.getClosePrice()) {
            return true;
        } else if(tradeType.equals("LONG") && vIndex > c.getClosePrice()) {
            return true;
        } else {
            return false;
        }
    }

    private void openTrade(Candle c){
        tradeIsOpen = true;

        double entryAmount = utils.getEntryAmount(c.getClosePrice());
        double entryCommission = utils.getCommission(entryAmount);

        Trade trade = new Trade();
        trade.setEntryTime(c.getCloseTime());
        trade.setEntryPrice(c.getClosePrice());
        trade.setEntryAmount(entryAmount);
        trade.setTradeType(tradeType);
        trade.setCommission(entryCommission);
        trade.setOpenVix(vix.get());
        trade.persist();

        double balance = utils.getBalance();
        balance -= entryAmount;

        utils.setBalance(balance);
        utils.setTradeId(trade.id.toString());
    }

    private void closeTrade(Candle c){
        tradeIsOpen = false;

        String tradeId = utils.getTradeId();
        Trade currentTrade = tradeRepository.findByTradeId(tradeId);

        double exitAmount = utils.getExitAmount(c.getClosePrice());
        double exitCommission = utils.getCommission(exitAmount);
        double totCommission = exitCommission + currentTrade.getCommission();
        double profit = utils.calculateProfit(tradeType);
        double profitWithoutCommission = profit - totCommission;

        currentTrade.setExitTime(c.getCloseTime());
        currentTrade.setExitPrice(c.getClosePrice());
        currentTrade.setExitAmount(exitAmount);
        currentTrade.setCommission(totCommission);
        currentTrade.setProfit(profit);
        currentTrade.setProfitWithoutCommission(profitWithoutCommission);
        currentTrade.setCloseVix(vix.get());
        currentTrade.update();

        utils.updateBalanceOnCloseTrade();
        
        double balance = utils.getBalance();
        if (balance < maxDrowDown) {
            maxDrowDown = balance;
        }
        System.out.println("TRADE CHIUSO | Balance: " + balance);
        utils.setTradeId("");

        tradeType = "";
        totTrades++;
        if (profitWithoutCommission > 0) winningTrades++;
    }

    private void updateTradesCounter(){
        utils.updateTradesCounter("Vi", totTrades, winningTrades);
    }
}
