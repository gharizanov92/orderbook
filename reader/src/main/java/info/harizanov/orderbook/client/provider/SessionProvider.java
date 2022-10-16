package info.harizanov.orderbook.client.provider;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.template.KrakenTemplate;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class SessionProvider {

    private final Logger logger = LoggerFactory.getLogger(SessionProvider.class);

    protected final KrakenEndpoint endpoint;
    protected final ClientEndpointConfig clientEndpointConfig;
    protected final WebSocketContainer container;
    private volatile Mono<Session> session;
    private final String url;

    public SessionProvider(String url, KrakenEndpoint endpoint, ClientEndpointConfig clientEndpointConfig, WebSocketContainer container) {
        this.url = url;
        this.endpoint = endpoint;
        this.clientEndpointConfig = clientEndpointConfig;
        this.container = container;
    }

    public Mono<Session> getSession() {
        return session;
    }

    public Mono<Session> connect() {
        session = Mono.create(sink -> {
            try {
                logger.info("Attempting to obtain connection to exchange {}", url);
                sink.success(container.connectToServer(endpoint, clientEndpointConfig, URI.create(url)));
                logger.info("Established connection to {}", url);
            } catch (Exception e) {
                sink.error(e);
            }
        });

        return session;
    }
}
