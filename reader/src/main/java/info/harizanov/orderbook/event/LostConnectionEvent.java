package info.harizanov.orderbook.event;

import org.springframework.context.ApplicationEvent;

public class LostConnectionEvent extends ApplicationEvent {

    public LostConnectionEvent() {
        super("");
    }
}
