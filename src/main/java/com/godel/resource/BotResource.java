package com.godel.resource;

import com.godel.service.CandleService;
import com.godel.service.StrategyService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/bot")
public class BotResource {

    private final StrategyService strategyService;
    private final CandleService candleService;

    public BotResource(StrategyService strategyService, CandleService candleService) {
        this.strategyService = strategyService;
        this.candleService = candleService;
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
}
