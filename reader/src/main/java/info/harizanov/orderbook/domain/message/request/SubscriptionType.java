package info.harizanov.orderbook.domain.message.request;

import com.google.gson.annotations.SerializedName;

public enum SubscriptionType {
    @SerializedName("book")
    BOOK,
    @SerializedName("ohlc")
    OHLC,
    @SerializedName("openOrders")
    OPEN_ORDERS,
    @SerializedName("ownTrades")
    OWN_TRADERS,
    @SerializedName("spread")
    SPREAD,
    @SerializedName("ticker")
    TICKER,
    @SerializedName("trade")
    TRADE,
    @SerializedName("*")
    ALL
}
