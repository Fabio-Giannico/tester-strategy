package com.godel.utils.indicator;

import java.util.HashMap;
import java.util.List;
import com.godel.repository.model.Candle;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class EMA {
    
    private final HashMap<Integer, Double> emaMap = new HashMap<>();

    
    public void initialize(List<Candle> candles, int period) {
        if (candles.size() < (period + 50)) {
            throw new IllegalArgumentException("Non ci sono abbastanza candele per calcolare l'EMA");
        }

        List<Candle> candlesForSma = candles.subList(0, period);
        double sma = IndicatorUtils.calculateSma(candlesForSma);

        double currentEma = sma;
        for (int i = period; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            currentEma = IndicatorUtils.calculateEma(currentEma, candle.getClosePrice(), period);
        }

        emaMap.put(period, currentEma);
    }

    public void updateEma(int period, Candle newCandle) {
        Double currentEma = emaMap.get(period);
        if (currentEma == null) {
            throw new IllegalStateException("EMA non inizializzata per il periodo " + period);
        }

        double newEma = IndicatorUtils.calculateEma(currentEma, newCandle.getClosePrice(), period);
        emaMap.put(period, newEma);
    }

    public double get(int period) {
        Double ema = emaMap.get(period);
        if (ema == null) {
            throw new IllegalStateException("EMA non inizializzata per il periodo " + period);
        }
        return ema;
    }
}
