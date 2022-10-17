package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeMessageTest {

    public static final Gson GSON = new Gson();

    @Test
    void testParse_whenTradeMessage_willReturnExchangeTuple() {
        // given
        final String exchange = "ETH/USD";
        final Integer channelID = 561;
        final BigDecimal price = new BigDecimal("1282.89000");
        final BigDecimal volume = new BigDecimal("0.03839853");
        final BigDecimal time = new BigDecimal("1665856457.127177");
        final String side = "b";
        final String orderType = "l";
        final String misc = "\"\"";
        final String type = "trade";

        final String json = "[" + channelID + ",[[\"" + price.toString() + "\",\"" + volume + "\",\"" + time + "\",\"" + side + "\",\"" + orderType + "\"," + misc + "]],\"" + type + "\",\"" + exchange + "\"]";

        // when
        final Flux<Tuple2<String, TradeMessage>> message = TradeMessage.parse(json);

        // then
        StepVerifier.create(message)
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(exchange);
                    final TradeMessage payload = tuple.getT2();
                    assertThat(payload.getPrice()).isEqualTo(price);
                    assertThat(payload.getVolume()).isEqualTo(volume);
                    assertThat(payload.getTime()).isEqualTo(time);
                    assertThat(payload.getSide()).isEqualTo(side);
                    assertThat(payload.getOrderType()).isEqualTo(orderType);
                })
                .verifyComplete();
    }

    @Test
    void testParse_whenUnsupportedMessage_willReturnEmpty() {
        // given
        final String json = "[565,[\"1282.88000\",\"1282.89000\",\"1665856441.515272\",\"2.24448680\",\"34.13590393\"],\"spread\",\"ETH/USD\"]";

        // when
        final Flux<Tuple2<String, TradeMessage>> message = TradeMessage.parse(json);

        // then
        StepVerifier.create(message).expectNextCount(0).verifyComplete();
    }
}