package info.harizanov.orderbook.domain.message.request;

import com.google.gson.annotations.SerializedName;
import org.glassfish.grizzly.utils.Pair;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * DTO to send the initial subscribe message with
 * As per https://docs.kraken.com/websockets/#message-subscribe
 *
 * Uses custom JSON Serializer
 * @see info.harizanov.orderbook.domain.message.encoder.KrakenSubscribeMessageEncoder
 */
public class KrakenSubscribeMessage extends KrakenRequestMessage {
    @SerializedName("reqid")
    private Integer requestId;
    private List<Tuple2<KrakenCurrency, KrakenCurrency>> pair;
    private final KrakenSubscription subscription;

    private KrakenSubscribeMessage(KrakenSubscription subscription) {
        super(EventType.SUBSCRIBE);
        this.subscription = subscription;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public List<Tuple2<KrakenCurrency, KrakenCurrency>> getPair() {
        return pair;
    }

    public void setPair(List<Tuple2<KrakenCurrency, KrakenCurrency>> pair) {
        this.pair = pair;
    }

    public KrakenSubscription getSubscription() {
        return subscription;
    }

    public static KrakenSubscribeMessageBuilder builder(final KrakenSubscription subscription) {
        return new KrakenSubscribeMessageBuilder(subscription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KrakenSubscribeMessage that = (KrakenSubscribeMessage) o;
        return Objects.equals(requestId, that.requestId) && Objects.equals(pair, that.pair) && Objects.equals(subscription, that.subscription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, pair, subscription);
    }

    public static class KrakenSubscribeMessageBuilder {
        private KrakenSubscribeMessage krakenSubscribeMessage;

        private KrakenSubscribeMessageBuilder(final KrakenSubscription subscription) {
            this.krakenSubscribeMessage = new KrakenSubscribeMessage(subscription);
        }

        public KrakenSubscribeMessageBuilder pairs(final Tuple2<KrakenCurrency, KrakenCurrency>... pairs) {
            return pairs(Arrays.asList(pairs));
        }

        public KrakenSubscribeMessageBuilder pairs(final List<Tuple2<KrakenCurrency, KrakenCurrency>> pairs) {
            krakenSubscribeMessage.setPair(pairs);
            return this;
        }

        public KrakenSubscribeMessageBuilder requestId(Integer requestId) {
            krakenSubscribeMessage.setRequestId(requestId);
            return this;
        }

        public KrakenSubscribeMessage build() {
            return krakenSubscribeMessage;
        }
    }
}
