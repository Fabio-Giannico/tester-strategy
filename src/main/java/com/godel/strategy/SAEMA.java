package com.godel.strategy;

import java.util.List;
import com.godel.repository.TradeRepository;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Trade;
import com.godel.utils.indicator.EMA;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class SAEMA {

    private final int p1 = 5;
    private final int p2 = 13;
    private final int p3 = 34;
    private final int p4 = 100;
    private final int initializeIndicatorsIndex = p4 + 50;

    private String tradeType = "";
    private boolean tradeIsOpen = false;
    private int winningTrades = 0;
    private int totTrades = 0;
    private double maxDrowDown = 999999;

    private final EMA ema;
    private final TradeRepository tradeRepository;
    private final StrategyUtils utils;

    public SAEMA(EMA ema, TradeRepository tradeRepository, StrategyUtils utils) {
        this.ema = ema;
        this.tradeRepository = tradeRepository;
        this.utils = utils;
    }

    public void executeStrategy(List<Candle> candles) {
        List<Candle> subCandles = candles.subList(0, initializeIndicatorsIndex);
        initializeIndicators(subCandles);

        for (int i = initializeIndicatorsIndex; i < candles.size(); i++) {
            Candle current = candles.get(i);

            ema.updateEma(p1, current);
            ema.updateEma(p2, current);
            ema.updateEma(p3, current);
            ema.updateEma(p4, current);

            double ema1 = ema.get(p1);
            double ema2 = ema.get(p2);
            double ema3 = ema.get(p3);
            double ema4 = ema.get(p4);

            if (tradeIsOpen && shouldExit(ema1, ema2, ema3, ema4, current.getClosePrice())) {
                closeTrade(current, ema4);
            }

            if (!tradeIsOpen && shouldEnter(ema1, ema2, ema3, ema4)) {
                openTrade(current, ema4);
            }

            if (i == candles.size() - 1) {
                updateTradesCounter();
                if (tradeIsOpen) {
                    closeTrade(current, ema4);
                }
                utils.setStrategyFinalBalance("Saema");
                utils.setStrategyMaxDrowdown("Saema", maxDrowDown);
            }
        }
    }

    private void initializeIndicators(List<Candle> candles) {
        ema.initialize(candles, p1);
        ema.initialize(candles, p2);
        ema.initialize(candles, p3);
        ema.initialize(candles, p4);
        
        utils.setStartingBalance();
    }

    private boolean shouldEnter(double ema1, double ema2, double ema3, double ema4) {
        boolean alignedUp = ema4 < ema3 && ema4 < ema2 && ema4 < ema1;
        boolean alignedDown = ema4 > ema3 && ema4 > ema2 && ema4 > ema1;

        if (alignedUp) {
            tradeType = "LONG";
            return true;
        } else if (alignedDown) {
            tradeType = "SHORT";
            return true;
        }
        return false;
    }

    private boolean shouldExit(double ema1, double ema2, double ema3, double ema4, double price) {
        if (tradeType.equals("LONG")) {
            if (ema4 > ema3 && ema4 > ema2 && ema4 > ema1) {return true; }
        } else if (tradeType.equals("SHORT")) {
            if (ema4 < ema3 && ema4 < ema2 && ema4 < ema1) {return true; }
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
        double commission = utils.getCommission(exitAmount);
        double totalCommission = currentTrade.getCommission() + commission;
        double profit = utils.calculateProfit(tradeType);
        double profitNet = profit - totalCommission;

        currentTrade.setExitTime(c.getCloseTime());
        currentTrade.setExitPrice(c.getClosePrice());
        currentTrade.setExitAmount(exitAmount);
        currentTrade.setCommission(totalCommission);
        currentTrade.setProfit(profit);
        currentTrade.setProfitWithoutCommission(profitNet);
        currentTrade.setExitEma100(ema4);
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
        if (profitNet > 0) winningTrades++;
    }

    private void updateTradesCounter() {
        utils.updateTradesCounter("Saema", totTrades, winningTrades);
    }
}
