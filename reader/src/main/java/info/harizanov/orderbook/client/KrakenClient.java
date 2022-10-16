package info.harizanov.orderbook.client;

import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import jakarta.websocket.*;
import org.glassfish.grizzly.utils.Pair;
//import org.reactivestreams.Publisher;
//import org.reactivestreams.Subscriber;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Flow;

//@ClientEndpoint(encoders = {KrakenSubscribeMessageEncoder.class})
public class KrakenClient extends Endpoint /*implements Flow.Publisher<String>*/ {

    private Flow.Subscriber<? super String> subscriber;

/*    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
        // subscriber.onSubscribe();
        // return producer.asFlux();
    }*/

    private ClientEndpointConfig config;
    private Session session;

    private Sinks.Many<String> producer = Sinks.many().replay().limit(Duration.ofDays(1));

    @SuppressWarnings("unchecked")
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        session.addMessageHandler ((MessageHandler.Whole<String>) msg -> {
            producer.tryEmitNext(msg);
            // subscriber.onNext(msg);
        });

        final KrakenSubscribeMessage subscribeMessage = KrakenSubscribeMessage
                .builder(KrakenSubscription.builder(SubscriptionType.ALL).build())
                .pairs(new Pair<>(KrakenCurrency.ETH, KrakenCurrency.USD))
                .build();

        try {
            session.getBasicRemote().sendObject(subscribeMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EncodeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        producer.tryEmitComplete();
        System.out.println(closeReason);
    }

    @Override
    public void onError(Session session, Throwable err) {
        producer.tryEmitError(err);
    }

    public Sinks.Many<String> getProducer() {
        return producer;
    }

    public void setProducer(Sinks.Many<String> producer) {
        this.producer = producer;
    }
}
