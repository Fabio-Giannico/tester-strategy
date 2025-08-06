package com.godel.utils.indicator;

import java.util.List;
import com.godel.repository.model.Candle;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ADX {

    private List<Double> trs;
    private List<Double> plusDms;
    private List<Double> minusDms;
    private List<Double> dxs;

    private double adx;

    public void initialize(List<Candle> candles, int period){
        if (candles.size() < (period * 2 + 1)) {
            throw new IllegalArgumentException("Non ci sono abbastanza candele per calcolare l'ADX");
        }

        trs = new java.util.ArrayList<>();
        plusDms = new java.util.ArrayList<>();
        minusDms = new java.util.ArrayList<>();
        dxs = new java.util.ArrayList<>();

        Candle previous = candles.get(0);

        for(int i = 1; i < candles.size(); i++){
            Candle current = candles.get(i);

            double tr = IndicatorUtils.calculateTr(current, previous.getClosePrice());
            double plusDm = IndicatorUtils.calculatePlusDm(current, previous);
            double minusDm = IndicatorUtils.calculateMinusDm(current, previous);

            trs.add(tr);
            plusDms.add(plusDm);
            minusDms.add(minusDm);

            if (trs.size() > period) {
                trs.remove(0);
                plusDms.remove(0);
                minusDms.remove(0);
            }

            if (trs.size() == period) {
                double atr = IndicatorUtils.calculateValuesSma(trs);
                double plusDiAvg = IndicatorUtils.calculateValuesSma(plusDms);
                double minusDiAvg = IndicatorUtils.calculateValuesSma(minusDms);

                double plusDi = (plusDiAvg / atr) * 100;
                double minusDi = (minusDiAvg / atr) * 100;

                double dx = Math.abs(plusDi - minusDi) / (plusDi + minusDi) * 100;
                dxs.add(dx);

                if (dxs.size() > period) {
                    dxs.remove(0);
                }

                if (dxs.size() == period) {
                    this.adx = IndicatorUtils.calculateValuesSma(dxs);
                }
            }
            previous = current;
        }
    }

    public void update(Candle newCandle, Candle previousCandle, int period){
        if (trs == null || plusDms == null || minusDms == null || dxs == null) {
            throw new IllegalStateException("ADX non inizializzato");
        }

        // Calcola i nuovi valori per la nuova candela
        double tr = IndicatorUtils.calculateTr(newCandle, previousCandle.getClosePrice());
        double plusDm = IndicatorUtils.calculatePlusDm(newCandle, previousCandle);
        double minusDm = IndicatorUtils.calculateMinusDm(newCandle, previousCandle);

        // Aggiungi i nuovi valori alle liste
        trs.add(tr);
        plusDms.add(plusDm);
        minusDms.add(minusDm);

        // Mantieni solo gli ultimi 'period' valori (lista circolare)
        if (trs.size() > period) {
            trs.remove(0);
            plusDms.remove(0);
            minusDms.remove(0);
        }

        // Se abbiamo abbastanza valori, calcola il nuovo ADX
        if (trs.size() == period) {
            double atr = IndicatorUtils.calculateValuesSma(trs);
            double plusDiAvg = IndicatorUtils.calculateValuesSma(plusDms);
            double minusDiAvg = IndicatorUtils.calculateValuesSma(minusDms);

            double plusDi = (plusDiAvg / atr) * 100;
            double minusDi = (minusDiAvg / atr) * 100;

            double dx = Math.abs(plusDi - minusDi) / (plusDi + minusDi) * 100;
            dxs.add(dx);

            // Mantieni solo gli ultimi 'period' valori DX
            if (dxs.size() > period) {
                dxs.remove(0);
            }

            // Se abbiamo abbastanza valori DX, calcola l'ADX
            if (dxs.size() == period) {
                this.adx = IndicatorUtils.calculateValuesSma(dxs);
            }
        }
    }

    public double getAdx() {
        return adx;
    }
}
