package info.harizanov.orderbook.domain.message.request;

import com.google.gson.annotations.SerializedName;

public enum EventType {
    @SerializedName("subscribe")
    SUBSCRIBE,
    @SerializedName("unsubscribe")
    UNSUBSCRIBE,
    @SerializedName("subscriptionStatus")
    SUBSCRIPTION_STATUS
}
