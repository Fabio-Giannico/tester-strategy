package com.godel.utils.indicator;

import java.util.List;
import com.godel.repository.model.Candle;


public class IndicatorUtils {
    
    public static double calculateSma(List<Candle> candles){
        return candles.stream()
                      .mapToDouble(Candle::getClosePrice)
                      .average()
                      .orElseThrow(() -> new IllegalArgumentException("Lista candele vuota"));
    }
    public static double calculateValuesSma(List<Double> values){
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Lista valori vuota");
        }

        return values.stream()
                 .mapToDouble(Double::doubleValue)
                 .average()
                 .orElse(0.0);
    }

    public static double calculateEma(double previousEma, double closePrice, int period){
        double alpha = calculateAlpha(period);

        return closePrice * alpha + previousEma * (1 - alpha);
    }

    public static double calculateTr(Candle c, double prevClose) {
        double highLowDiff = c.getHighPrice() - c.getLowPrice();
        double highPrevCloseDiff = Math.abs(c.getHighPrice() - prevClose);
        double lowPrevCloseDiff = Math.abs(c.getLowPrice() - prevClose);

        return Math.max(Math.max(highLowDiff, highPrevCloseDiff), lowPrevCloseDiff);
    }

    // Positive Directional Movement
    public static double calculatePlusDm(Candle current, Candle previous) {
        double highDiff = current.getHighPrice() - previous.getHighPrice();
        double lowDiff = previous.getLowPrice() - current.getLowPrice();
        
        return (highDiff > lowDiff && highDiff > 0) ? highDiff : 0;
    }

    // Negative Directional Movement
    public static double calculateMinusDm(Candle current, Candle previous) {
        double highDiff = current.getHighPrice() - previous.getHighPrice();
        double lowDiff = previous.getLowPrice() - current.getLowPrice();
        
        return (lowDiff > highDiff && lowDiff > 0) ? lowDiff : 0;
    }




    private static double calculateAlpha(int period) {
        return 2.0 / (period + 1.0);
    }
}
