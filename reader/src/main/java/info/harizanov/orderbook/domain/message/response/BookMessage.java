package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * As per https://docs.kraken.com/websockets/#message-book
 */
public class BookMessage {

    public static final Gson GSON = new Gson();

    @SerializedName("as")
    private List<PriceLevel> asks = new ArrayList<>();

    @SerializedName("bs")
    private List<PriceLevel> bids = new ArrayList<>();

    public List<PriceLevel> getAsks() {
        return asks;
    }

    public void setAsks(List<PriceLevel> asks) {
        this.asks = asks;
    }

    public List<PriceLevel> getBids() {
        return bids;
    }

    public void setBids(List<PriceLevel> bids) {
        this.bids = bids;
    }

    public static Flux<Tuple2<String, BookMessage>> parse(String json) {

        try {
            final List<Object> data = GSON.fromJson(json, new TypeToken<List<Object>>() {
            }.getType());

            final String name = "" + data.get(data.size() - 2);
            if (name.matches("book-\\d+")) {
                return Flux.just("" + data.get(data.size() - 1)).zipWith(
                        Flux.range(1, data.size() - 3)
                                .map(idx -> (LinkedTreeMap<String, List<List<String>>>) data.get(idx))
                                .map(tokens -> {
                                    final BookMessage bookMessage = new BookMessage();

                                    bookMessage.getAsks().addAll(
                                            tokens.getOrDefault("as", Collections.emptyList())
                                                    .stream()
                                                    .map(PriceLevel::fromTokens)
                                                    .toList()
                                    );

                                    bookMessage.getAsks().addAll(
                                            tokens.getOrDefault("a", Collections.emptyList())
                                                    .stream()
                                                    .map(PriceLevel::fromTokens)
                                                    .toList()
                                    );

                                    bookMessage.getBids().addAll(
                                            tokens.getOrDefault("bs", Collections.emptyList())
                                                    .stream()
                                                    .map(PriceLevel::fromTokens)
                                                    .toList()
                                    );

                                    bookMessage.getBids().addAll(
                                            tokens.getOrDefault("b", Collections.emptyList())
                                                    .stream()
                                                    .map(PriceLevel::fromTokens)
                                                    .toList()
                                    );

                                    return bookMessage;
                                })
                                .reduce(new BookMessage(), (current, next) -> {
                                    current.getAsks().addAll(next.getAsks());
                                    current.getBids().addAll(next.getBids());
                                    return current;
                                })
                );
            }
            return Flux.empty();
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    @Override
    public String toString() {
        return "BookMessage{" +
                "asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
