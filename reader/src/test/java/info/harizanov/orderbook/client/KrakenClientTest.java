package info.harizanov.orderbook.client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.WebSocketContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

class KrakenClientTest {

    @Test
    void connected() throws DeploymentException, IOException {
        WebSocketContainer container =
                ContainerProvider.getWebSocketContainer();
        container.connectToServer(KrakenClient.class, URI.create("wss://ws.kraken.com"));
    }
}