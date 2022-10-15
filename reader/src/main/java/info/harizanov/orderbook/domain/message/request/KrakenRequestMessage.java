package info.harizanov.orderbook.domain.message.request;

public abstract class KrakenRequestMessage {
    protected final EventType event;

    public KrakenRequestMessage(EventType event) {
        this.event = event;
    }
}
