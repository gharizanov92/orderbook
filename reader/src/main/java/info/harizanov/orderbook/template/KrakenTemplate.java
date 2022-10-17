package info.harizanov.orderbook.template;

import info.harizanov.orderbook.client.KrakenEndpoint;
import info.harizanov.orderbook.client.provider.SessionSupplier;
import info.harizanov.orderbook.configuration.properties.KrakenProperties;
import info.harizanov.orderbook.domain.message.request.*;
import info.harizanov.orderbook.domain.message.response.BookMessage;
import info.harizanov.orderbook.domain.message.response.KrakenEventMessage;
import info.harizanov.orderbook.domain.message.response.SubscriptionMessage;
import info.harizanov.orderbook.domain.message.response.TradeMessage;
import info.harizanov.orderbook.event.LostConnectionEvent;
import info.harizanov.orderbook.util.Holder;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.WebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static jakarta.websocket.CloseReason.CloseCodes.NORMAL_CLOSURE;

public class KrakenTemplate {

    private final Logger logger = LoggerFactory.getLogger(KrakenTemplate.class);

    protected final SessionSupplier sessionProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Sinks.Many<String> sink;
    private final KrakenProperties krakenProperties;
    private Set<KrakenSubscriptionMessage> subscriptions = new HashSet<>();

    private Holder<Instant> lastHeartbeat = Holder.lazyHolder(Instant::now);

    public KrakenTemplate(final KrakenEndpoint endpoint, final KrakenProperties krakenProperties,
                          final ClientEndpointConfig clientEndpointConfig,
                          final WebSocketContainer container, final Sinks.Many<String> sink,
                          final ApplicationEventPublisher applicationEventPublisher) {
        this.sink = sink;
        this.krakenProperties = krakenProperties;
        this.sessionProvider = new SessionSupplier(krakenProperties.getUrl(), endpoint, clientEndpointConfig, container);
        this.applicationEventPublisher = applicationEventPublisher;
        this.sessionProvider.connect();
        ensureConnectionStaysAlive();
    }

    public void reconnect() {
        sessionProvider.get()
                .doOnNext(session -> {
                    try {
                        if (session.isOpen()) {
                            session.close(new CloseReason(NORMAL_CLOSURE, "Manual reconnect"));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenMany(Flux.fromIterable(subscriptions).zipWith(sessionProvider.connect().repeat()))
                .subscribe(tuple -> {
                    final KrakenSubscriptionMessage subscription = tuple.getT1();
                    tuple.getT2().getAsyncRemote().sendObject(subscription);
                });
    }

    private void publishReconnectEvent() {
        applicationEventPublisher.publishEvent(new LostConnectionEvent());
    }

    public void subscribeFor(Tuple2<KrakenCurrency, KrakenCurrency> exchange) {
        subscribeFor(exchange, SubscriptionType.ALL);
    }

    @SuppressWarnings("unchecked")
    public void subscribeFor(Tuple2<KrakenCurrency, KrakenCurrency> exchange, SubscriptionType subscriptionType) {
        subscribeFor(KrakenSubscriptionMessage
                .builder(KrakenSubscription.builder(subscriptionType).build())
                .pairs(exchange)
                .build());
    }

    public void unsubscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange) {
        unsubscribeForExchange(exchange, SubscriptionType.ALL);
    }

    @SuppressWarnings("unchecked")
    public void unsubscribeForExchange(Tuple2<KrakenCurrency, KrakenCurrency> exchange, SubscriptionType subscriptionType) {
        subscribeFor(KrakenSubscriptionMessage
                .builder(KrakenSubscription.builder(subscriptionType).build())
                .eveht(EventType.UNSUBSCRIBE)
                .pairs(exchange)
                .build());
    }

    @SuppressWarnings("unchecked")
    public void subscribeFor(final KrakenSubscriptionMessage subscribeMessage) {
        subscriptions.add(subscribeMessage);

        sessionProvider.get()
                .doOnError((any) -> {
                    logger.error("Lost connection to server, attempting to reconnect..");
                    publishReconnectEvent();
                })
                .subscribe(session -> session.getAsyncRemote().sendObject(subscribeMessage));
        this.sink.asFlux().subscribe(logger::debug);
    }

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

    private void ensureConnectionStaysAlive() {
        getHeartBeatFeed().subscribe(h -> lastHeartbeat.set(Instant.now()));

        Flux.generate(sink -> sink.next("")).delayElements(Duration.of(krakenProperties.getReconnectInterval(), ChronoUnit.MILLIS))
                .subscribe(e -> {
                    if (!subscriptions.isEmpty() && lastHeartbeat.get() != null && (Instant.now()).minus(5, ChronoUnit.SECONDS).isAfter(lastHeartbeat.get())) {
                        lastHeartbeat.set(Instant.now());
                        publishReconnectEvent();
                    }
                });

        if (subscriptions.isEmpty()) {
            logger.warn("There are no active subscriptions");
        }
    }

    public SessionSupplier getSessionProvider() {
        return sessionProvider;
    }
}
