package info.harizanov.orderbook.template;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.client.provider.SessionProvider;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import info.harizanov.orderbook.domain.message.response.*;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.WebSocketContainer;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.function.Predicate;

public class KrakenTemplate {

    protected final SessionProvider sessionProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Sinks.Many<String> sink;

    public KrakenTemplate(final KrakenEndpoint endpoint, final ClientEndpointConfig clientEndpointConfig,
                          final WebSocketContainer container, final Sinks.Many<String> sink,
                          final ApplicationEventPublisher applicationEventPublisher) {
        this.sink = sink;
        this.sessionProvider = new SessionProvider("wss://ws.kraken.com", endpoint, clientEndpointConfig, container);
        this.applicationEventPublisher = applicationEventPublisher;
        this.sessionProvider.connect();
    }

    public void subscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange) {
        subscribeForExchange(exchange, SubscriptionType.ALL);
    }

    public void subscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange, SubscriptionType subscriptionType) {
        sessionProvider.getSession()
                .subscribe(session ->
                        session.getAsyncRemote().sendObject(KrakenSubscribeMessage
                                .builder(KrakenSubscription.builder(subscriptionType).build())
                                .pairs(exchange)
                                .build())
                );
    }

    public void unsubscribeForExchange(List<Tuple2<KrakenCurrency, KrakenCurrency>> currencies) {

    }

    public Flux<KrakenEventMessage> getHeartBeatFeed() {
        return getEventFeed().filter(e -> "heartbeat".equalsIgnoreCase(e.getEvent()));
    }

    public Flux<SubscriptionMessage> getSubscriptionMessageFeed() {
        return sink.asFlux().flatMap(SubscriptionMessage::parse);
    }

    public Flux<KrakenEventMessage> getEventFeed() {
        return sink.asFlux().flatMap(KrakenEventMessage::parse);
    }

    public Flux<KrakenEventMessage> getEventFeed(Predicate<KrakenEventMessage> filter) {
        return getEventFeed().filter(filter);
    }

    public Flux<Tuple2<String, TradeMessage>> getTradeFeed() {
        return sink.asFlux().flatMap(TradeMessage::parse);
    }
}
