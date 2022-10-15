package info.harizanov.orderbook.client;

import info.harizanov.orderbook.domain.message.encoder.KrakenSubscribeMessageEncoder;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import jakarta.websocket.*;
import org.glassfish.grizzly.utils.Pair;

import java.io.IOException;

@ClientEndpoint(encoders = {KrakenSubscribeMessageEncoder.class})
public class KrakenClient {

    private ClientEndpointConfig config;
    private Session session;

    @OnOpen
    @SuppressWarnings("unchecked")
    public void connected(Session session, EndpointConfig config) throws EncodeException, IOException {
        this.session = session;
        System.out.println();
        final KrakenSubscribeMessage subscribeMessage = KrakenSubscribeMessage
                .builder(KrakenSubscription.builder(SubscriptionType.TICKER).build())
                .pairs(new Pair<>(KrakenCurrency.ETH, KrakenCurrency.USD))
                .build();
        session.getBasicRemote().sendObject(subscribeMessage);
    }

    @OnMessage
    public void onMessage(String msg) {
        System.out.println(msg);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(closeReason);
    }

    @OnError
    public void onError(Session session, Throwable err) {
        err.printStackTrace();
    }
}
