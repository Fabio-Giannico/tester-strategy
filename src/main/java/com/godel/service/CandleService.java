package com.godel.service;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import com.godel.repository.CandleRepository;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Timeframe;
import com.godel.utils.binance.BinanceApi;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class CandleService {
    private int[] candlesTimespanInMinutes = {30, 60, 120};
    private int counter = 0;
    private int cicles = 1;
    private int candlesLimit = 1000;

    private final BinanceApi binanceApi;
    private final CandleRepository repository;

    public CandleService(BinanceApi binanceApi, CandleRepository candleRepository){
        this.binanceApi = binanceApi;
        this.repository = candleRepository;
    }


    public void fillDb() throws Exception {
        for(int i = 0; i < candlesTimespanInMinutes.length; i++){
            int timespan = candlesTimespanInMinutes[i];

            if (candlesLimit % (i + 1) != 0) {
                throw new IllegalArgumentException("Impossibile fare la richiesta poichè il limit ha raggiunto un valore con la virgola");
            }

            String symbol = "BTCUSDC";
            int intervalMinutes = (timespan < 59) ? timespan : timespan / 60;
            char intervalLetter = (timespan < 59) ? 'm' : 'h';
            String interval = String.valueOf(intervalMinutes) + intervalLetter;
            int limitValue = candlesLimit / (i + 1);
            String limit = String.valueOf(limitValue);

            HashMap<String, String> params = new HashMap<>();
            params.put("symbol", symbol);
            params.put("interval", interval);
            params.put("limit", limit);

            Long endTime = null;

            while (counter < cicles) {
                if (endTime != null) {
                    params.put("endTime", String.valueOf(endTime));
                }

                JSONArray candles = binanceApi.getCandles(params);

                if (candles == null || candles.length() == 0) {
                    System.out.println("Nessuna candela trovata.");
                    break;
                }
                
                repository.saveCandles(candles);
                System.out.println("Cicle number " + counter);

                // Prendi openTime dell’ultima candela
                long oldestOpenTime = candles.getJSONArray(0).getLong(0);
                endTime = oldestOpenTime - 1;

                counter++;
                Thread.sleep(200);
            }
        }
        System.out.println("DB filled");
    }

    public List<Candle> getSortedCandles(Timeframe tf){
        return repository.getSortedCandles(tf);
    }
}
