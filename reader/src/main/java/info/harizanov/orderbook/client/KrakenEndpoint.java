package info.harizanov.orderbook.client;

import info.harizanov.orderbook.event.LostConnectionEvent;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static jakarta.websocket.CloseReason.CloseCodes.NORMAL_CLOSURE;

public class KrakenEndpoint extends Endpoint {

    private final Logger logger = LoggerFactory.getLogger(KrakenEndpoint.class);

    private Sinks.Many<String> producer = Sinks.many().replay().limit(Duration.ofDays(1));

    private List<MessageHandler> handlers = new ArrayList<>();

    private final ApplicationEventPublisher applicationEventPublisher;

    public KrakenEndpoint(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        handlers.forEach(session::addMessageHandler);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.error("Connection closed {}", closeReason.toString());
    }

    @Override
    public void onError(Session session, Throwable err) {
        producer.tryEmitError(err);
        restartSession(session, err);
    }

    private void restartSession(Session session, Throwable err) {
        try {
            handlers.forEach(session::removeMessageHandler);
            session.close(new CloseReason(NORMAL_CLOSURE, err.getMessage()));
        } catch (IOException e) {
            logger.error("Unable to close session", e);
            throw new RuntimeException(e);
        }
        applicationEventPublisher.publishEvent(new LostConnectionEvent());
    }

    public Sinks.Many<String> getProducer() {
        return producer;
    }

    public void setProducer(Sinks.Many<String> producer) {
        this.producer = producer;
    }

    public List<MessageHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<MessageHandler> handlers) {
        this.handlers = handlers;
    }
}
