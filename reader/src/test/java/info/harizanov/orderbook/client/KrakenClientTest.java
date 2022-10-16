package info.harizanov.orderbook.client;

import info.harizanov.orderbook.domain.message.encoder.KrakenSubscribeMessageEncoder;
import jakarta.websocket.*;
import org.glassfish.tyrus.core.coder.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

class KrakenClientTest {

    @Test
    void connected() throws DeploymentException, IOException, InterruptedException {
        final WebSocketContainer container =
                ContainerProvider.getWebSocketContainer();

        final List<Class<? extends Decoder>> decoders = new ArrayList<>();
        // decoders.addAll(PrimitiveDecoders.ALL);
        decoders.add(NoOpTextCoder.class);
        decoders.add(NoOpByteBufferCoder.class);
        decoders.add(NoOpByteArrayCoder.class);
        decoders.add(ReaderDecoder.class);
        decoders.add(InputStreamDecoder.class);

        final ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                .encoders(Collections.singletonList(KrakenSubscribeMessageEncoder.class))
                .decoders(decoders)
                .build();

        final KrakenEndpoint client = new KrakenEndpoint(mock(ApplicationEventPublisher.class));
        container.connectToServer(client, clientEndpointConfig, URI.create("wss://ws.kraken.com"));

         client.getProducer().asFlux().doOnNext(System.out::println).blockLast();
//        client.subscribe(new Flow.Subscriber<String>() {
//            @Override
//            public void onSubscribe(Flow.Subscription subscription) {
//
//            }
//
//            @Override
//            public void onNext(String item) {
//                System.out.println(item);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        Thread.sleep(10000);
    }
}