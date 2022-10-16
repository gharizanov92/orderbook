package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import reactor.core.publisher.Mono;

/**
 * Message that the client gets after subscription request
 * Example:
 * {
 *   "channelID": 560,
 *   "channelName": "book-10",
 *   "event": "subscriptionStatus",
 *   "pair": "ETH/USD",
 *   "status": "subscribed",
 *   "subscription": {
 *     "depth": 10,
 *     "name": "book"
 *   }
 * }
 */
public class SubscriptionMessage {
    public static final Gson GSON = new Gson();
    private Integer channelID;
    private String channelName;
    private String event;
    private String pair;
    private String status;
    private KrakenSubscription subscription;

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public KrakenSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(KrakenSubscription subscription) {
        this.subscription = subscription;
    }

    public static Mono<SubscriptionMessage> parse(String json) {
        try {
            return Mono.just(GSON.fromJson(json, new TypeToken<SubscriptionMessage>() {}.getType()));
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public String toString() {
        return "SubscriptionMessage{" +
                "channelID=" + channelID +
                ", channelName='" + channelName + '\'' +
                ", event='" + event + '\'' +
                ", pair='" + pair + '\'' +
                ", status='" + status + '\'' +
                ", subscription=" + subscription +
                '}';
    }
}
