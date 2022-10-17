package info.harizanov.orderbook.template;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.client.provider.SessionProvider;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import info.harizanov.orderbook.domain.message.response.*;
import info.harizanov.orderbook.event.LostConnectionEvent;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.WebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class KrakenTemplate {

    private final Logger logger = LoggerFactory.getLogger(KrakenTemplate.class);

    protected final SessionProvider sessionProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Sinks.Many<String> sink;
    private Set<KrakenSubscribeMessage> subscriptions = new HashSet<>();

    public KrakenTemplate(final KrakenEndpoint endpoint, final ClientEndpointConfig clientEndpointConfig,
                          final WebSocketContainer container, final Sinks.Many<String> sink,
                          final ApplicationEventPublisher applicationEventPublisher) {
        this.sink = sink;
        // TODO: config
        this.sessionProvider = new SessionProvider("wss://ws.kraken.com", endpoint, clientEndpointConfig, container);
        this.applicationEventPublisher = applicationEventPublisher;
        this.sessionProvider.connect();
    }

    public void reconnect() {
        Flux.fromIterable(subscriptions)
                .doOnError(e -> {
                    logger.error("Unable to reconnect", e);
                })
                .subscribe(detachedSubscriptions ->
                        Mono.delay(Duration.of(3, ChronoUnit.SECONDS)).then(sessionProvider.connect())
                                .doOnTerminate(() -> {
                                    logger.error("Unable to establish connection, attempting to reconnect..");
                                    applicationEventPublisher.publishEvent(new LostConnectionEvent());
                                })
                                .subscribe(session -> session.getAsyncRemote().sendObject(detachedSubscriptions))
                );
    }

    public void subscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange) {
        subscribeForExchange(exchange, SubscriptionType.ALL);
    }

    @SuppressWarnings("unchecked")
    public void subscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange, SubscriptionType subscriptionType) {
        subscribeForExchange(exchange, KrakenSubscribeMessage
                .builder(KrakenSubscription.builder(subscriptionType).build())
                .pairs(exchange)
                .build());
    }

    @SuppressWarnings("unchecked")
    public void subscribeForExchange(final Tuple2<KrakenCurrency, KrakenCurrency> exchange,
                                     final KrakenSubscribeMessage subscribeMessage) {
        subscriptions.add(subscribeMessage);

        sessionProvider.getSession()
                .doOnError((any) -> {
                    logger.error("Lost connection to server, attempting to reconnect..");
                    applicationEventPublisher.publishEvent(new LostConnectionEvent());
                })
                .subscribe(session -> session.getAsyncRemote().sendObject(subscribeMessage));
        this.sink.asFlux().subscribe(logger::debug);
    }

    public void unsubscribeForExchange(List<Tuple2<KrakenCurrency, KrakenCurrency>> currencies) {
        // TODO
    }

    // TODO: reconnect if haven't received in a while
    public Flux<KrakenEventMessage> getHeartBeatFeed() {
        ensureAtLeastOneSubscriptionIsPresent();
        return getEventFeed().filter(e -> "heartbeat".equalsIgnoreCase(e.getEvent()));
    }

    public Flux<SubscriptionMessage> getSubscriptionMessageFeed() {
        ensureAtLeastOneSubscriptionIsPresent();
        return sink.asFlux().flatMap(SubscriptionMessage::parse);
    }

    public Flux<KrakenEventMessage> getEventFeed() {
        ensureAtLeastOneSubscriptionIsPresent();
        return sink.asFlux().flatMap(KrakenEventMessage::parse);
    }

    public Flux<KrakenEventMessage> getEventFeed(Predicate<KrakenEventMessage> filter) {
        ensureAtLeastOneSubscriptionIsPresent();
        return getEventFeed().filter(filter);
    }

    public Flux<Tuple2<String, BookMessage>> getBookFeed() {
        ensureAtLeastOneSubscriptionIsPresent();
        return sink.asFlux().flatMap(BookMessage::parse);
    }

    public Flux<Tuple2<String, TradeMessage>> getTradeFeed() {
        ensureAtLeastOneSubscriptionIsPresent();
        return sink.asFlux().flatMap(TradeMessage::parse);
    }

    private void ensureAtLeastOneSubscriptionIsPresent() {
        if (subscriptions.isEmpty()) {
            logger.warn("There are no active subscriptions");
        }
    }

    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }
}
