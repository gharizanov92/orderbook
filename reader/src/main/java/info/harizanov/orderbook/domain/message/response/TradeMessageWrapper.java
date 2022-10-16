package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Flux;

import java.util.List;

public class TradeMessageWrapper extends BaseChannelResponse<TradeMessage> {

    public static final Gson GSON = new Gson();

/*    public static Mono<TradeMessage> parse(String json) {

        try {
            List<Object> data = GSON.fromJson(json, new TypeToken<List<Object>>() {}.getType());
            if (("" + data.get(2)).equalsIgnoreCase("trade")) {
                List<List<String>> tokens = GSON.fromJson(GSON.toJson(data.get(1)), new TypeToken<List<List<String>>>() {}.getType());

                return GSON.fromJson(json, new TypeToken<List<Object>>() {}.getType());
            }
            // ArrayList<String> data = GSON.fromJson(json, new ArrayList<String>(){}.getType());
            return Mono.empty();
        } catch (Exception e) {
            return Mono.empty();
        }
    }
    */
    public static Flux<TradeMessage> parse(String json) {

        try {
            List<Object> data = GSON.fromJson(json, new TypeToken<List<Object>>() {}.getType());
            if (("" + data.get(2)).equalsIgnoreCase("trade")) {
                List<List<String>> tokens = GSON.fromJson(GSON.toJson(data.get(1)), new TypeToken<List<List<String>>>() {}.getType());

                return Flux.fromIterable(tokens).map(TradeMessage::fromTokens);
            }
            // ArrayList<String> data = GSON.fromJson(json, new ArrayList<String>(){}.getType());
            return Flux.empty();
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    @Override
    public String toString() {
        return "TradeMessage{" +
                "channelID=" + channelID +
                ", channelName='" + channelName + '\'' +
                ", pair='" + pair + '\'' +
                ", payload=" + payload +
                '}';
    }
}
