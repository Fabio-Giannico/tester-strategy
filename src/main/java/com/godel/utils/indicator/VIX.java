package com.godel.utils.indicator;

import java.util.List;
import com.godel.repository.model.Candle;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class VIX {

    private final int timeframe = 9;
    private final int multiplier = 3;

    private double prevClose = 0.0;
    private double atr = 0.0;
    private boolean uptrend = true;
    private boolean useWicks = true;
    private double maxVal = 0.0;
    private double minVal = 0.0;
    private double vix = 0.0;


    public void initialize(List<Candle> candles) {
        if (candles.size() < (this.timeframe + 20)) {
            throw new IllegalArgumentException("Non ci sono abbastanza candele per calcolare il VIX");
        }

        // Generate the list used to calculate the first ATR (Average True Range)
        List<Candle> atrCandles = candles.subList(0, this.timeframe + 1);
        List<Candle> vixCandles = candles.subList(this.timeframe + 2, candles.size());
        
        // Set the prev_close as the close_price of the first candle of the list 
        this.prevClose = atrCandles.get(0).getClosePrice();

        // Iterate on the list to add each candle's TR to the sum to calculate the average and get the first ATR
        double trSum = 0;
        for (int i = 1; i < atrCandles.size(); i++) {
            Candle current = atrCandles.get(i);
            double candleTR = IndicatorUtils.calculateTr(current, this.prevClose);
            trSum += candleTR;
            this.prevClose = current.getClosePrice();
        }

        this.atr = trSum / this.timeframe;
        double multipliedATR = this.atr * this.multiplier;

        Candle candleForFirstVix = candles.get(this.timeframe + 1);
        calculateVix(candleForFirstVix, multipliedATR);

        for (Candle candle : vixCandles) {
            update(candle);
        }
    }

    public void update(Candle c) {
        double tr = IndicatorUtils.calculateTr(c, this.prevClose);
        this.atr = (this.atr * (this.timeframe - 1) + tr) / this.timeframe;
        double multipliedATR = this.atr * this.multiplier;

        calculateVix(c, multipliedATR);

        this.prevClose = c.getClosePrice();
    }

    public double get(){
        return this.vix;
    }



    private void calculateVix(Candle c, double multipliedATR) {
        double srcHigh = this.useWicks ? c.getHighPrice() : c.getClosePrice();
        double srcLow = this.useWicks ? c.getLowPrice() : c.getClosePrice();

        // Update maxVal/minVal
        this.maxVal = Math.max(this.maxVal, srcHigh);
        this.minVal = Math.min(this.minVal, srcLow);

        // Calculate vix
        double stop = this.uptrend ? this.maxVal - multipliedATR : this.minVal + multipliedATR;

        // Detect new trend
        boolean newUptrend = c.getClosePrice() > stop;

        if (newUptrend != this.uptrend) {
            this.uptrend = newUptrend;
            this.maxVal = srcHigh;
            this.minVal = srcLow;
            stop = this.uptrend ? this.maxVal - multipliedATR : this.minVal + multipliedATR;
        }

        this.vix = stop;
    }
}
