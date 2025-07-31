package com.godel.repository;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.json.JSONArray;
import com.godel.repository.model.Candle;
import com.godel.repository.model.Timeframe;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class CandleRepository implements PanacheMongoRepository<Candle> {

    @ConfigProperty(name="quarkus.mongodb.database")
    String databaseName;

    private final MongoClient mongoClient;

    public CandleRepository(MongoClient mongoClient){
        this.mongoClient = mongoClient;
    }


    
    public List<Candle> getSortedCandles(Timeframe timeframe) {
        // Switch alla collection corretta
        MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(timeframe.getCollectionName());
        
        return collection.find()
            .sort(new Document("close_time", 1))
            .map(this::documentToCandle)
            .into(new ArrayList<>());
        
    }

    public void saveCandles(JSONArray candles) {
        List<Candle> candlesList = jsonArrayToCandleList(candles);
        persist(candlesList);
    }



    private List<Candle> jsonArrayToCandleList(JSONArray jsonArray) {
        List<Candle> candleList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray c = jsonArray.getJSONArray(i);

            Candle candle = new Candle();
            candle.setOpenTime(c.getLong(0));
            candle.setOpenPrice(Double.parseDouble(c.getString(1)));
            candle.setHighPrice(Double.parseDouble(c.getString(2)));
            candle.setLowPrice(Double.parseDouble(c.getString(3)));
            candle.setClosePrice(Double.parseDouble(c.getString(4)));
            candle.setExchangedAssetQuantity(Double.parseDouble(c.getString(5)));
            candle.setCloseTime(c.getLong(6));
            candle.setExchangedCurrencyQuantity(Double.parseDouble(c.getString(7)));
            candle.setnTrades(c.getInt(8));
            candle.setAssetQuantityBoughtByTaker(Double.parseDouble(c.getString(9)));
            candle.setCurrencyQuantitySpentByTaker(Double.parseDouble(c.getString(10)));
            candle.setIgnore(c.getInt(11));

            candleList.add(candle);
        }

        return candleList;
    }

    private Candle documentToCandle(Document doc) {
        Candle candle = new Candle();
        candle.setOpenTime(doc.getLong("open_time"));
        candle.setOpenPrice(doc.getDouble("open_price"));
        candle.setHighPrice(doc.getDouble("high_price"));
        candle.setLowPrice(doc.getDouble("low_price"));
        candle.setClosePrice(doc.getDouble("close_price"));
        candle.setExchangedAssetQuantity(doc.getDouble("exchanged_asset_quantity"));
        candle.setCloseTime(doc.getLong("close_time"));
        candle.setExchangedCurrencyQuantity(doc.getDouble("exchanged_currency_quantity"));
        candle.setnTrades(doc.getInteger("n_trades"));
        candle.setAssetQuantityBoughtByTaker(doc.getDouble("asset_qty_bought_by_taker"));
        candle.setCurrencyQuantitySpentByTaker(doc.getDouble("currency_qty_spent_by_taker"));
        candle.setIgnore(doc.getInteger("ignore"));
        return candle;
    }
}
