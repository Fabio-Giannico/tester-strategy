package com.godel.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Timeframe;
import com.godel.strategy.Phoenix;
import com.godel.strategy.SAEMA;
import com.godel.strategy.SaemaVi;
import com.godel.strategy.VI;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class StrategyService {
    private final CandleService candleService;
    private final SAEMA saema;
    private final VI vi;
    private final SaemaVi saemaVi;
    private final Phoenix phoenix;

    public StrategyService(CandleService candleService, SAEMA saema, VI vi, SaemaVi saemaVi, Phoenix phoenix) {
        this.candleService = candleService;
        this.saema = saema;
        this.vi = vi;
        this.saemaVi = saemaVi;
        this.phoenix = phoenix;
    }

    public void executeStrategy(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.now();
        System.out.println("Start time: " + start.format(formatter));

        List<Candle> _60MinCandles = candleService.getSortedCandles(Timeframe.C_60);
        List<Candle> _120MinCandles = candleService.getSortedCandles(Timeframe.C_120);
        // saema.executeStrategy(candles);
        // vi.executeStrategy(candles);
        // saemaVi.executeStrategy(candles);
        phoenix.executeStrategy(_60MinCandles, _120MinCandles);

        LocalDateTime end = LocalDateTime.now();
        System.out.println("Strategy executed!\nEnd time: " + end.format(formatter));
    }
}
