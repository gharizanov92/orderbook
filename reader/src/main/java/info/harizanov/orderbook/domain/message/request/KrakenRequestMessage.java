package info.harizanov.orderbook.domain.message.request;

public abstract class KrakenRequestMessage {
    protected EventType event;

    public KrakenRequestMessage(EventType event) {
        this.event = event;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }
}
