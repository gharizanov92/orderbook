package info.harizanov.orderbook.domain.message.encoder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import jakarta.websocket.Encoder;
import reactor.util.function.Tuple2;

import java.util.List;

public class KrakenSubscribeMessageEncoder implements Encoder.Text<KrakenSubscribeMessage> {
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static JsonSerializer<List<Tuple2<KrakenCurrency, KrakenCurrency>>> currencyPairSerializer =
            (pairs, type, jsonSerializationContext) -> {
                final JsonArray currencyPairs = new JsonArray();

                for (final Tuple2<KrakenCurrency, KrakenCurrency> pair : pairs) {
                    currencyPairs.add(String.format("%s/%s", pair.getT1(), pair.getT2()));
                }

                return currencyPairs;
            };

    static {
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Tuple2<KrakenCurrency, KrakenCurrency>>>() {}.getType(),
                currencyPairSerializer);
    }

    @Override
    public String encode(KrakenSubscribeMessage krakenSubscribeMessage) {
        return gsonBuilder.create().toJson(krakenSubscribeMessage);
    }
}
