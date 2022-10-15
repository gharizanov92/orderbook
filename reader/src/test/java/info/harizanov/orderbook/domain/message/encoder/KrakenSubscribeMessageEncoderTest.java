package info.harizanov.orderbook.domain.message.encoder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import info.harizanov.orderbook.domain.message.request.EventType;
import info.harizanov.orderbook.domain.message.request.KrakenSubscribeMessage;
import info.harizanov.orderbook.domain.message.request.KrakenSubscription;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import org.glassfish.grizzly.utils.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static info.harizanov.orderbook.domain.message.request.KrakenCurrency.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class KrakenSubscribeMessageEncoderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void givenConversionPairs_whenSerialized_willBeInExpectedFormat() {
        // given
        final Gson gson = new Gson();
        final KrakenSubscribeMessageEncoder encoder = new KrakenSubscribeMessageEncoder();
        final KrakenSubscribeMessage krakenSubscribeMessage = KrakenSubscribeMessage
                .builder(KrakenSubscription.builder(SubscriptionType.TICKER).build())
                .pairs(new Pair<>(USD, ETH), new Pair<>(USD, BTC))
                .build();

        // when
        final String encoded = encoder.encode(krakenSubscribeMessage);

        // then
        final Map<String, Object> subscriptionMessage = gson.fromJson(encoded,
                new TypeToken<Map<String, Object>>() {}.getType());

        assertThat(subscriptionMessage.get("event"), is(EventType.SUBSCRIBE.toString().toLowerCase()));
        assertThat(subscriptionMessage.get("pair"), notNullValue());

        final List<String> expectedPairFormat = krakenSubscribeMessage.getPair().stream()
                .map(p -> String.format("%s/%s", p.getFirst(), p.getSecond()))
                .toList();

        assertThat((List<String>) subscriptionMessage.get("pair"), hasItems(expectedPairFormat.toArray(String[]::new)));
    }
}