package info.harizanov.orderbook.template;

import info.harizanov.orderbook.client.provider.SessionSupplier;
import info.harizanov.orderbook.configuration.properties.KrakenProperties;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import jakarta.websocket.Session;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KrakenTemplateTest {

    @Test
    public void testWhenNoHeartbeatInAWhile_willReconnect() throws InterruptedException {
        // given
        final AtomicInteger reconnectionAttempts = new AtomicInteger(0);
        final KrakenProperties krakenProperties = new KrakenProperties();
        final int interval = 10;
        final int sleepTime = 1000;
        final int expectedReconnectionAttempts = 10;

        krakenProperties.setReconnectInterval(interval);
        krakenProperties.setMaxIdleTime(interval);

        final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

        final SessionSupplier sessionSupplier = mock(SessionSupplier.class);
        final Session session = mock(Session.class);

        final Sinks.Many<String> sink = Sinks.many().replay().limit(Duration.ofDays(1));

        final KrakenTemplate krakenTemplate = new KrakenTemplate(krakenProperties,
                sessionSupplier, sink, applicationEventPublisher);

        // when
        when(sessionSupplier.get()).thenReturn(Mono.just(session));
        doAnswer(e -> {
            reconnectionAttempts.incrementAndGet();
            return e;
        }).when(applicationEventPublisher).publishEvent(any());

        // emit a single heartbeat
        sink.tryEmitNext("{\"event\":\"heartbeat\"}");

        // then
        krakenTemplate.subscribeFor(Tuples.of(KrakenCurrency.ETH, KrakenCurrency.USD), SubscriptionType.BOOK);
        krakenTemplate.getHeartBeatFeed().subscribe();

        Thread.sleep(sleepTime);
        assertThat(reconnectionAttempts.get()).isGreaterThanOrEqualTo(expectedReconnectionAttempts);
    }

}