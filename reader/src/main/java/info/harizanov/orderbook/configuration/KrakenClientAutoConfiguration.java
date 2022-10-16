package info.harizanov.orderbook.configuration;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.client.provider.SessionProvider;
import info.harizanov.orderbook.domain.message.encoder.KrakenSubscribeMessageEncoder;
import info.harizanov.orderbook.event.WSConnectEvent;
import info.harizanov.orderbook.template.KrakenTemplate;
import jakarta.websocket.*;
import org.glassfish.tyrus.core.coder.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class KrakenClientAutoConfiguration {

    @Bean("decoders")
    @ConditionalOnMissingBean(name = "decoders")
    public List<Class<? extends Decoder>> decoders() {
        return Arrays.asList(
                NoOpTextCoder.class,
                NoOpByteBufferCoder.class,
                NoOpByteArrayCoder.class,
                ReaderDecoder.class,
                InputStreamDecoder.class
        );
    }

    @Bean("encoders")
    @ConditionalOnMissingBean(name = "encoders")
    public List<Class<? extends Encoder>> encoders() {
        return Collections.singletonList(KrakenSubscribeMessageEncoder.class);
    }

    @Bean
    @ConditionalOnMissingBean(ClientEndpointConfig.class)
    public ClientEndpointConfig clientEndpointConfig(final @Qualifier("decoders") List<Class<? extends Decoder>> decoders,
                                                     final @Qualifier("encoders") List<Class<? extends Encoder>> encoders) {
        return ClientEndpointConfig.Builder.create()
                .decoders(decoders)
                .encoders(encoders)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(WebSocketContainer.class)
    public WebSocketContainer webSocketContainer() {
        return ContainerProvider.getWebSocketContainer();
    }

    @Bean
    @ConditionalOnMissingBean(MessageHandler.class)
    public MessageHandler defaultMessageHandler(@Qualifier("krakenWSSink") final Sinks.Many<String> sink) {
        return (MessageHandler.Whole<String>) sink::tryEmitNext;
    }

    @Bean("krakenWSSink")
    public Sinks.Many<String> defaultSink() {
        return Sinks.many().replay().limit(Duration.ofDays(1));
    }

//    @Bean("krakenProducer")
//    public Flux<String> defaultProducer(final Sinks.Many<String> sink) {
//        return sink.asFlux();
//    }

    @Bean
    @ConditionalOnMissingBean(KrakenEndpoint.class)
    public KrakenEndpoint krakenEndpoint(final List<MessageHandler> messageHandlers) {
        final KrakenEndpoint client = new KrakenEndpoint();
        client.setHandlers(messageHandlers);

        return client;
    }

    @Bean
    @ConditionalOnMissingBean(KrakenTemplate.class)
    public KrakenTemplate krakenTemplate(final KrakenEndpoint endpoint, final ClientEndpointConfig config,
                                         final WebSocketContainer container,
                                         final ApplicationEventPublisher applicationEventPublisher,
                                         @Qualifier("krakenWSSink") final Sinks.Many<String> sink) {
        return new KrakenTemplate(endpoint, config, container, sink, applicationEventPublisher);
    }

    @EventListener(WSConnectEvent.class)
    public void onApplicationEvent(WSConnectEvent event) {
        ((SessionProvider) event.getSource()).connect();
    }
}
