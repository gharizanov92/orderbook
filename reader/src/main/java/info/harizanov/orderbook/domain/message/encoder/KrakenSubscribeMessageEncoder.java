package info.harizanov.orderbook.domain.message.encoder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import jakarta.websocket.Encoder;
import org.glassfish.grizzly.utils.Pair;

import java.util.List;

public class KrakenSubscribeMessageEncoder implements Encoder.Text<KrakenSubscribeMessage> {
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static JsonSerializer<List<Pair<KrakenCurrency, KrakenCurrency>>> currencyPairSerializer =
            (pairs, type, jsonSerializationContext) -> {
                final JsonArray currencyPairs = new JsonArray();

                for (final Pair<KrakenCurrency, KrakenCurrency> pair : pairs) {
                    currencyPairs.add(String.format("%s/%s", pair.getFirst(), pair.getSecond()));
                }

                return currencyPairs;
            };

    static {
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Pair<KrakenCurrency, KrakenCurrency>>>() {}.getType(),
                currencyPairSerializer);
    }

    @Override
    public String encode(KrakenSubscribeMessage krakenSubscribeMessage) {
        return gsonBuilder.create().toJson(krakenSubscribeMessage);
    }
}
