package info.harizanov.orderbook.event;

import info.harizanov.orderbook.client.provider.SessionProvider;
import org.springframework.context.ApplicationEvent;

public class WSConnectEvent extends ApplicationEvent {
    public WSConnectEvent(SessionProvider sessionProvider) {
        super(sessionProvider);
        // sessionProvider.connect();
        // super(source);
    }
}
