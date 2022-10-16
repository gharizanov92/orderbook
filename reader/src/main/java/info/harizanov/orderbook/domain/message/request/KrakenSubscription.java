package info.harizanov.orderbook.domain.message.request;

import com.google.gson.annotations.SerializedName;

public class KrakenSubscription {
    private final SubscriptionType name;
    private Integer depth;
    private Integer interval;
    @SerializedName("ratecounter")
    private Boolean rateCounter;
    private Boolean snapshot;
    private String token;

    public KrakenSubscription(SubscriptionType name) {
        this.name = name;
    }

    public SubscriptionType getName() {
        return name;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Boolean isRateCounter() {
        return rateCounter;
    }

    public void setRateCounter(Boolean rateCounter) {
        this.rateCounter = rateCounter;
    }

    public Boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Boolean snapshot) {
        this.snapshot = snapshot;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static KrakenSubscriptionBuilder builder(final SubscriptionType subscriptionType) {
        return new KrakenSubscriptionBuilder(subscriptionType);
    }

    @Override
    public String toString() {
        return "KrakenSubscription{" +
                "name=" + name +
                ", depth=" + depth +
                ", interval=" + interval +
                ", rateCounter=" + rateCounter +
                ", snapshot=" + snapshot +
                ", token='" + token + '\'' +
                '}';
    }

    public static class KrakenSubscriptionBuilder {
        KrakenSubscription subscription;

        public KrakenSubscriptionBuilder(SubscriptionType subscriptionType) {
            this.subscription = new KrakenSubscription(subscriptionType);
        }

        public KrakenSubscriptionBuilder withDepth(Integer depth) {
            subscription.depth = depth;
            return this;
        }

        public KrakenSubscriptionBuilder withInterval(Integer interval) {
            subscription.interval = interval;
            return this;
        }

        public KrakenSubscriptionBuilder withRateCounter(Boolean rateCounter) {
            subscription.rateCounter = rateCounter;
            return this;
        }

        public KrakenSubscriptionBuilder withSnapshot(Boolean snapshot) {
            subscription.snapshot = snapshot;
            return this;
        }

        public KrakenSubscriptionBuilder withToken(String token) {
            subscription.token = token;
            return this;
        }

        public KrakenSubscription build() {
            return subscription;
        }
    }
}
