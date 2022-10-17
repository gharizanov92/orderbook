package info.harizanov.orderbook.domain;

import info.harizanov.orderbook.domain.message.response.BookMessage;
import info.harizanov.orderbook.domain.message.response.PriceLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class OrderbookSummaryTest {

    @Test
    public void testOrderBookSummaryUpdate() {
        // given
        final OrderbookSummary orderbookSummary = new OrderbookSummary("BTC/USD");

        final String highestAsk = "2283.44000";
        final String highestBid = "2283.44000";
        final String latestTimestamp = "1665866326.000000";
        final List<String> testAskTokens = Arrays.asList("1283.44000", "48.65356418", "1665856324.985065");
        final List<String> testBidTokens = Arrays.asList(highestBid, "5.67410000", "1665856324.985065");

        final PriceLevel firstAsk = PriceLevel.fromTokens(testAskTokens);
        final PriceLevel firstBid = PriceLevel.fromTokens(testBidTokens);

        testAskTokens.set(0, highestAsk);
        testBidTokens.set(0, "1283.43000");

        final PriceLevel secondAsk = PriceLevel.fromTokens(testAskTokens);
        final PriceLevel secondBid = PriceLevel.fromTokens(testBidTokens);

        testAskTokens.set(2, latestTimestamp);
        testBidTokens.set(2, latestTimestamp);

        final PriceLevel lastAsk = PriceLevel.fromTokens(testAskTokens);
        final PriceLevel lastBid = PriceLevel.fromTokens(testBidTokens);

        // when
        final BookMessage bookMessage = new BookMessage();

        bookMessage.getAsks().add(firstAsk);
        bookMessage.getAsks().add(firstAsk);

        bookMessage.getBids().add(firstBid);
        bookMessage.getBids().add(firstBid);

        bookMessage.getAsks().add(secondAsk);
        bookMessage.getBids().add(secondBid);

        bookMessage.getAsks().add(lastAsk);
        bookMessage.getBids().add(lastBid);

        orderbookSummary.update(bookMessage);

        // then
        assertThat(orderbookSummary.getAsks()).hasSize(3);
        assertThat(orderbookSummary.getBids()).hasSize(3);
        assertThat(orderbookSummary.getBestAsk().getPrice()).isEqualTo(new BigDecimal(highestAsk));
        assertThat(orderbookSummary.getBestBid().getPrice()).isEqualTo(new BigDecimal(highestBid));

        assertThat(orderbookSummary.getAsks().iterator().next().getTimestamp()).isEqualTo(new BigDecimal(latestTimestamp));
        assertThat(orderbookSummary.getBids().iterator().next().getTimestamp()).isEqualTo(new BigDecimal(latestTimestamp));
    }

}