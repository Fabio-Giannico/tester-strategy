package com.godel.utils.binance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.json.JSONArray;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class BinanceApi {
    @ConfigProperty(name = "BASE_URL", defaultValue = "https://api.binance.com/api/v3")
    private String BASE_URL;

    private final HttpClient client = HttpClient.newHttpClient();



    public JSONArray getCandles(HashMap<String, String> params) throws Exception {
        // Formatta la stringa dei parametri concatenandoli con '&' e assegnandoli con '='
        String queryParams = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");

        // Crea l'URL della richiesta
        String url = BASE_URL + "/klines?" + queryParams;

        int attemptCounter = 0;
        int maxAttempts = 5;
        JSONArray candles = null;

        while (attemptCounter < maxAttempts) {
            try {
                // Invia la richiesta HTTP e ottiene la risposta
                HttpResponse<String> res = sendHttpRequest(url);

                // Verifica se la risposta è 200 OK (se contiene candele le ritorna)
                if (res.statusCode() == 200) {
                    candles = new JSONArray(res.body());

                    if (candles.length() > 0) {
                        return candles;
                    }
                } else {
                    System.err.println("Tentativo " + attemptCounter + " fallito. Status code: " + res.statusCode());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            attemptCounter++;
        };
        throw new Exception("Impossibile ottenere candele dopo " + maxAttempts + " tentativi.");
    }



    private HttpResponse<String> sendHttpRequest(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            return client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Errore nella richiesta: " + e.getMessage(), e);
        }
    }
}
