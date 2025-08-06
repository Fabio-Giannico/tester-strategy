package com.godel.strategy;

import java.util.List;
import com.godel.repository.TradeRepository;
import com.godel.repository.model.Candle;
import com.godel.utils.indicator.ADX;
import com.godel.utils.indicator.EMA;
import com.godel.utils.indicator.VIX;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class Phoenix {

    private final int emaP1 = 5;
    private final int emaP2 = 13;
    private final int emaP3 = 34;
    private final int emaP4 = 100;
    private final int emaP5 = 200;
    private final int smallerTfInitializeIndicatorIndex = 500;
    private final int biggerTfInitializeIndicatorIndex = smallerTfInitializeIndicatorIndex / 2;
    private final int adxPeriod = 14;

    private boolean tradeIsOpen = false;
    private String tradeType = "";
    private int winningTrades = 0;
    private int totTrades = 0;
    private double maxDrowDown;

    private final EMA ema;
    private final VIX vix;
    private final ADX adx;
    private final StrategyUtils utils;
    private final TradeRepository tradeRepository;

    public Phoenix(EMA ema, VIX vix, ADX adx, StrategyUtils utils, TradeRepository tradeRepository){
        this.ema = ema;
        this.vix = vix;
        this.adx = adx;
        this.utils = utils;
        this.tradeRepository = tradeRepository;
    }



    public void executeStrategy(List<Candle> smallTfCandles, List<Candle> bigTfCandles){
        List<Candle> smallTfSubCandles = smallTfCandles.subList(0, smallerTfInitializeIndicatorIndex);
        List<Candle> bigTfSubCandles = bigTfCandles.subList(0, biggerTfInitializeIndicatorIndex);
        initializeIndicators(smallTfSubCandles, bigTfSubCandles);
    }



    private void initializeIndicators(List<Candle> smallTfCandles, List<Candle> bigTfCandles){
        ema.initialize(smallTfCandles, emaP1);
        ema.initialize(smallTfCandles, emaP2);
        ema.initialize(smallTfCandles, emaP3);
        ema.initialize(smallTfCandles, emaP4);
        ema.initialize(bigTfCandles, emaP5);
        vix.initialize(smallTfCandles);
        adx.initialize(bigTfCandles, adxPeriod);
    }
}
