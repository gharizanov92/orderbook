package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookMessageTest {

    public static final Gson GSON = new Gson();

    @Test
    void testParseSnapshot_whenBookMessage_willReturnExchangeTuple() {
        // given
        final String exchange = "ETH/USD";
        final String firstAskStr = "1283.44000\",\"48.65356418\",\"1665856325.437481";
        final String firstBidStr = "1283.43000\",\"5.67410000\",\"1665856324.985065";

        final PriceLevel firstAsk = PriceLevel.fromTokens(Arrays.asList(firstAskStr.split("\",\"")));
        final PriceLevel firstBid = PriceLevel.fromTokens(Arrays.asList(firstBidStr.split("\",\"")));

        final String json = "[560,{\"as\":[[\"" + firstAskStr + "\"],[\"1283.45000\",\"0.42700784\",\"1665856291.386182\"],[\"1283.53000\",\"0.42131826\",\"1665856294.892060\"],[\"1283.64000\",\"23.65332267\",\"1665856325.182621\"],[\"1283.65000\",\"54.83362190\",\"1665856300.283142\"],[\"1283.71000\",\"12.07437816\",\"1665856261.860659\"],[\"1283.76000\",\"0.63139718\",\"1665856291.639593\"],[\"1283.79000\",\"8.28092603\",\"1665856325.537724\"],[\"1283.80000\",\"4.45000000\",\"1665856300.427455\"],[\"1283.82000\",\"0.43140585\",\"1665856291.718018\"]],\"bs\":[[\"" + firstBidStr + "\"],[\"1283.00000\",\"12.08106001\",\"1665856325.414507\"],[\"1282.93000\",\"0.01558619\",\"1665856324.128119\"],[\"1282.90000\",\"58.46114640\",\"1665856325.117058\"],[\"1282.88000\",\"1.46275402\",\"1665856277.874533\"],[\"1282.87000\",\"0.01558692\",\"1665856324.129857\"],[\"1282.86000\",\"6.22500000\",\"1665856325.142256\"],[\"1282.77000\",\"9.30157691\",\"1665856277.952790\"],[\"1282.73000\",\"12.08360294\",\"1665856007.161014\"],[\"1282.69000\",\"116.94140392\",\"1665856030.066982\"]]},\"book-10\",\"" + exchange + "\"]";

        // when
        final var message = BookMessage.parse(json);

        // then
        StepVerifier.create(message)
                .assertNext(tuple -> {
                    assertThat(tuple.getT1()).isEqualTo(exchange);
                    final var payload = tuple.getT2();
                    final PriceLevel actualFirstAsk = payload.getAsks().get(0);
                    final PriceLevel actualFirstBid = payload.getBids().get(0);

                    assertThat(actualFirstAsk.getPrice()).isEqualTo(firstAsk.getPrice());
                    assertThat(actualFirstAsk.getTimestamp()).isEqualTo(firstAsk.getTimestamp());
                    assertThat(actualFirstAsk.getVolume()).isEqualTo(firstAsk.getVolume());

                    assertThat(actualFirstBid.getPrice()).isEqualTo(firstBid.getPrice());
                    assertThat(actualFirstBid.getTimestamp()).isEqualTo(firstBid.getTimestamp());
                    assertThat(actualFirstBid.getVolume()).isEqualTo(firstBid.getVolume());
                })
                .verifyComplete();
    }

    @Test
    void testParseUpdate_whenBookMessage_willNotWorkBecauseNotSupportedYet() {
        // given
        final String json = "[\n" +
                "  1234,\n" +
                "  {\n" +
                "    \"a\": [\n" +
                "      [\n" +
                "        \"5541.30000\",\n" +
                "        \"2.50700000\",\n" +
                "        \"1534614248.456738\"\n" +
                "      ],\n" +
                "      [\n" +
                "        \"5542.50000\",\n" +
                "        \"0.40100000\",\n" +
                "        \"1534614248.456738\"\n" +
                "      ]\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"b\": [\n" +
                "      [\n" +
                "        \"5541.30000\",\n" +
                "        \"0.00000000\",\n" +
                "        \"1534614335.345903\"\n" +
                "      ]\n" +
                "    ],\n" +
                "    \"c\": \"974942666\"\n" +
                "  },\n" +
                "  \"book-10\",\n" +
                "  \"XBT/USD\"\n" +
                "]";

        // when
        final var message = BookMessage.parse(json);

        // then
        StepVerifier.create(message).expectNextCount(0).verifyComplete();
    }

    @Test
    void testParse_whenUnsupportedMessage_willReturnEmpty() {
        // given
        final String json = "[565,[\"1282.88000\",\"1282.89000\",\"1665856441.515272\",\"2.24448680\",\"34.13590393\"],\"spread\",\"ETH/USD\"]";

        // when
        final Flux<Tuple2<String, BookMessage>> message = BookMessage.parse(json);

        // then
        StepVerifier.create(message).expectNextCount(0).verifyComplete();
    }
}