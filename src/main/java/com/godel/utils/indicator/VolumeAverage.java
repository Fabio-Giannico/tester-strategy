package com.godel.utils.indicator;

import java.util.ArrayList;
import java.util.List;
import com.godel.repository.model.Candle;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class VolumeAverage {

    private List<Double> volumeTot = new ArrayList<>();
    private double volumeAvg = 0.0;



    public void initialize(List<Candle> candles, int period) {
        if (candles.size() < period) {
            throw new IllegalArgumentException("Non ci sono abbastanza candele per calcolare il Volume Average");
        }
        
        for (int i = 0; i < period; i++){
            volumeTot.add(candles.get(i).getVolume());
        }

        volumeAvg = IndicatorUtils.calculateValuesSma(volumeTot);

        for(int i = period; i < candles.size(); i++) {
            update(candles.get(i));
        }
    }

    public void update(Candle c){
        if (volumeTot.isEmpty()) {
            throw new IllegalStateException("VolumeAverage is not initialized");
        }
        
        double currentVolume = c.getVolume();

        volumeTot.remove(0);
        volumeTot.add(currentVolume);

        volumeAvg = IndicatorUtils.calculateValuesSma(volumeTot);
    }

    public double get() {
        return volumeAvg;
    }
}
