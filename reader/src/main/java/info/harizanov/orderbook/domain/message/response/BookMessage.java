package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.List;

/**
 * As per https://docs.kraken.com/websockets/#message-book
 */
public class BookMessage {

    public static final Gson GSON = new Gson();

    @SerializedName("as")
    private List<PriceLevel> asks;

    @SerializedName("bs")
    private List<PriceLevel> bids;

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
        final BookMessage bookMessage = new BookMessage();
        try {
            final List<Object> data = GSON.fromJson(json, new TypeToken<List<Object>>() {}.getType());

            if (("" + data.get(data.size() - 2)).matches("book-\\d+")) {
                final LinkedTreeMap<String, List<List<String>>> tokens = (LinkedTreeMap<String, List<List<String>>>) data.get(1);

                bookMessage.setAsks(
                        tokens.get("as")
                                .stream()
                                .map(PriceLevel::fromTokens)
                                .toList()
                );

                bookMessage.setBids(
                        tokens.get("bs")
                                .stream()
                                .map(PriceLevel::fromTokens)
                                .toList()
                );

                return Flux.just("" + data.get(3)).zipWith(Flux.just(bookMessage));
            }
            // ArrayList<String> data = GSON.fromJson(json, new ArrayList<String>(){}.getType());
            return Flux.empty();
        } catch (Exception e) {
            return Flux.empty();
        }
    }
}
