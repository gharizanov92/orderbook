package info.harizanov.orderbook.client.provider;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.util.Holder;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

public class SessionSupplier implements Supplier<Mono<Session>> {
    private final Logger logger = LoggerFactory.getLogger(SessionSupplier.class);
    protected final KrakenEndpoint endpoint;
    protected final ClientEndpointConfig clientEndpointConfig;
    protected final WebSocketContainer container;
    private Mono<Session> session;
    private final String url;

    public SessionSupplier(String url, KrakenEndpoint endpoint, ClientEndpointConfig clientEndpointConfig, WebSocketContainer container) {
        this.url = url;
        this.endpoint = endpoint;
        this.clientEndpointConfig = clientEndpointConfig;
        this.container = container;

        connect();
    }

    public Mono<Session> connect() {
        final Mono<Session> sessionMono = Mono.create(sink -> {
            try {
                logger.info("Attempting to obtain connection to exchange {}", url);
                sink.success(container.connectToServer(endpoint, clientEndpointConfig, URI.create(url)));
                logger.info("Established connection to {}", url);
            } catch (Exception e) {
                sink.error(e);
            }
        });
        session = sessionMono.cache();
        return session;
    }

    @Override
    public Mono<Session> get() {
        return session;
    }
}
