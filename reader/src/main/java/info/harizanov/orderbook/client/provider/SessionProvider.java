package info.harizanov.orderbook.client.provider;

import info.harizanov.orderbook.client.KrakenEndpoint;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

public class SessionProvider {

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

    public void connect() {
        session = Mono.create(sink -> {
            try {
                sink.success(container.connectToServer(endpoint, clientEndpointConfig, URI.create(url)));
            } catch (DeploymentException | IOException e) {
                sink.error(e);
            }
        });
    }
}
