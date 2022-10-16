package info.harizanov.orderbook.client;

import jakarta.websocket.*;
import reactor.core.CorePublisher;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Flow;

//@ClientEndpoint(encoders = {KrakenSubscribeMessageEncoder.class})
public class KrakenEndpoint extends Endpoint {

    private List<CorePublisher<?>> publishers;

    private Flow.Subscriber<? super String> subscriber;

//    @Override
//    public void subscribe(Flow.Subscriber<? super String> subscriber) {
//        this.subscriber = subscriber;
//    }
    private Sinks.Many<String> producer = Sinks.many().replay().limit(Duration.ofDays(1));

    private List<MessageHandler> handlers;

    @SuppressWarnings("unchecked")
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        handlers.forEach(session::addMessageHandler);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        producer.tryEmitComplete();
        handlers.forEach(session::removeMessageHandler);
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

    public List<CorePublisher<?>> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<CorePublisher<?>> publishers) {
        this.publishers = publishers;
    }

    public List<MessageHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<MessageHandler> handlers) {
        this.handlers = handlers;
    }
}
