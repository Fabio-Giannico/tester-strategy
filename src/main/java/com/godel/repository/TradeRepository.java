package com.godel.repository;

import org.bson.types.ObjectId;
import com.godel.repository.model.Trade;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class TradeRepository implements PanacheMongoRepository<Trade> {

    public Trade findByTradeId(String tradeId) {
        ObjectId objectId = new ObjectId(tradeId);
        return findById(objectId);
    }
}
