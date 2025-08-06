package com.godel.resource;

import com.godel.service.CandleService;
import com.godel.service.StrategyService;
import com.godel.utils.database.RedisService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/bot")
public class BotResource {

    private final StrategyService strategyService;
    private final CandleService candleService;
    private final RedisService redisService;

    public BotResource(StrategyService strategyService, CandleService candleService, RedisService redisService) {
        this.strategyService = strategyService;
        this.candleService = candleService;
        this.redisService = redisService;
    }

    

    @GET
    @Path("/execute-strategy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeStrategy() {
        strategyService.executeStrategy();
        return Response.ok().build();
    }

    @GET
    @Path("/fill-db")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getData() throws Exception {
        candleService.fillDb();
        return Response.ok().build();
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRedisData() throws Exception {
        redisService.getValue("Balance");
        System.out.println("Redis value: " + redisService.getValue("Balance"));

        return Response.ok().build();
    }
}
