package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * As per https://docs.kraken.com/websockets/#message-trade
 */
public class TradeMessage {

    public static final Gson GSON = new Gson();

    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal time;
    private String side;
    private String orderType;
    private String misc;

    public static TradeMessage fromTokens(final List<String> args) {
        final var payload = new TradeMessage();
        final Iterator<String> it = args.iterator();
        payload.price = new BigDecimal(it.next());
        payload.volume = new BigDecimal(it.next());
        payload.time = new BigDecimal(it.next());
        payload.side = it.next();
        payload.orderType = it.next();
        payload.misc = it.next();

        return payload;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    @Override
    public String toString() {
        return "{" +
                "price=" + price +
                ", volume=" + volume +
                ", time=" + time +
                ", buy/sell='" + side + '\'' +
                ", market/limit='" + orderType + '\'' +
                ", misc='" + misc + '\'' +
                '}';
    }

    public static Flux<Tuple2<String, TradeMessage>> parse(String json) {

        try {
            final List<Object> data = GSON.fromJson(json, new TypeToken<List<Object>>() {}.getType());
            if (("" + data.get(2)).equalsIgnoreCase("trade")) {
                List<List<String>> tokens = GSON.fromJson(GSON.toJson(data.get(1)), new TypeToken<List<List<String>>>() {}.getType());

                return Flux.just("" + data.get(3)).zipWith(Flux.fromIterable(tokens).map(TradeMessage::fromTokens));
            }
            // ArrayList<String> data = GSON.fromJson(json, new ArrayList<String>(){}.getType());
            return Flux.empty();
        } catch (Exception e) {
            return Flux.empty();
        }
    }
}
