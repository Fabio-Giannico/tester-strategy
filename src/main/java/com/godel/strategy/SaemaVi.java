package com.godel.strategy;

import java.util.List;
import com.godel.repository.TradeRepository;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Trade;
import com.godel.utils.indicator.EMA;
import com.godel.utils.indicator.VIX;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class SaemaVi {

    private final int p1 = 5;
    private final int p2 = 13;
    private final int p3 = 34;
    private final int p4 = 100;
    private final int initializeIndicatorsIndex = p4 + 50;

    private boolean tradeIsOpen = false;
    private String tradeType = "";
    private int winningTrades = 0;
    private int totTrades = 0;
    private double maxDrowDown = 999999;

    private final EMA ema;
    private final VIX vix;
    private final StrategyUtils utils;
    private final TradeRepository tradeRepository;

    public SaemaVi(EMA ema, VIX vix, StrategyUtils utils, TradeRepository tradeRepository){
        this.ema = ema;
        this.vix = vix;
        this.utils = utils;
        this.tradeRepository = tradeRepository;
    }



    public void executeStrategy(List<Candle> candles) {
        List<Candle> subCandles = candles.subList(0, initializeIndicatorsIndex);
        initializeIndicators(subCandles);

        for (int i = 100; i < candles.size(); i++) {
            Candle current = candles.get(i);

            ema.updateEma(p1, current);
            ema.updateEma(p2, current);
            ema.updateEma(p3, current);
            ema.updateEma(p4, current);
            vix.update(current);

            double ema1 = ema.get(p1);
            double ema2 = ema.get(p2);
            double ema3 = ema.get(p3);
            double ema4 = ema.get(p4);
            double vIndex = vix.get();


            if (tradeIsOpen && areGoodConditionsToExit(ema1, ema2, ema3, ema4, vIndex, current.getClosePrice())) {
                closeTrade(current, ema4);
            }

            if (!tradeIsOpen && areGoodConditions(ema1, ema2, ema3, ema4, vIndex, current.getClosePrice())) {
                openTrade(current, ema4);
            }

            if (i == candles.size() - 1) {
                updateTradesCounter();
                if (tradeIsOpen) {
                    closeTrade(current, ema4);
                }
                utils.setStrategyFinalBalance("SaemaVi");
                utils.setStrategyMaxDrowdown("SaemaVi", maxDrowDown);
            }
        }
    }

    private void initializeIndicators(List<Candle> candles) {
        ema.initialize(candles, p1);
        ema.initialize(candles, p2);
        ema.initialize(candles, p3);
        ema.initialize(candles, p4);

        vix.initialize(candles);

        utils.setStartingBalance();
    }

    private boolean areGoodConditions(double ema1, double ema2, double ema3, double ema4, double vix, double currentPrice){
        if (ema4 > ema3 && ema4 > ema2 && ema4 > ema1 && vix > currentPrice) {
            tradeType = "SHORT";
            return true;
        } else if (ema4 < ema3 && ema4 < ema2 && ema4 < ema1 && vix < currentPrice) {
            tradeType = "LONG";
            return true;
        }
        return false;
    }

    private boolean areGoodConditionsToExit(double ema1, double ema2, double ema3, double ema4, double vix, double currentPrice){
        if (tradeType.equals("LONG")) {
            if (ema4 > ema3 && ema4 > ema2 && ema4 > ema1) {return true;}
            // if (vix > currentPrice) {return true;}
            return false;
        } else if (tradeType.equals("SHORT")) {
            if (ema4 < ema3 && ema4 < ema2 && ema4 < ema1) {return true;}
            // if (vix < currentPrice) {return true;}
            return false;
        }
        return false;
    }

    private void openTrade(Candle c, double ema4) {
        tradeIsOpen = true;
        
        double entryAmount = utils.getEntryAmount(c.getClosePrice());
        double entryCommission = utils.getCommission(entryAmount);

        Trade trade = new Trade();
        trade.setEntryTime(c.getCloseTime());
        trade.setEntryPrice(c.getClosePrice());
        trade.setEntryAmount(entryAmount);
        trade.setTradeType(tradeType);
        trade.setCommission(entryCommission);
        trade.setEntryEma100(ema4);
        trade.setOpenVix(vix.get());
        trade.persist();

        double balance = utils.getBalance();
        balance -= entryAmount;

        utils.setBalance(balance);
        utils.setTradeId(trade.id.toString());
    }

    private void closeTrade(Candle c, double ema4) {
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
        currentTrade.setExitEma100(ema4);
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
        utils.updateTradesCounter("SaemaVi", totTrades, winningTrades);
    }
}
