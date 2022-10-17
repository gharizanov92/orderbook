package info.harizanov.orderbook.domain;

import info.harizanov.orderbook.domain.message.response.BookMessage;
import info.harizanov.orderbook.domain.message.response.PriceLevel;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OrderbookSummary {
    private Set<PriceLevel> asks = new TreeSet<>();
    private Set<PriceLevel> bids = new TreeSet<>();
    private PriceLevel bestAsk;
    private PriceLevel bestBid;
    private Date lastUpdated;
    private final String exchange;

    public OrderbookSummary(String exchange) {
        this.exchange = exchange;
    }

    public void update(BookMessage bookMessage) {
        asks.addAll(bookMessage.getAsks());
        bids.addAll(bookMessage.getBids());

        bestAsk = asks.stream().max(Comparator.comparing(PriceLevel::getPrice)).orElse(new PriceLevel());
        bestBid = bids.stream().max(Comparator.comparing(PriceLevel::getPrice)).orElse(new PriceLevel());

        lastUpdated = new Date(Long.max(
                        asks.stream().findFirst().get().getTimestamp().longValue() * 1000,
                        bids.stream().findFirst().get().getTimestamp().longValue() * 1000));

        System.out.println(this);
    }

//    public void update(TradeMessage tradeMessage) {
//        tradeMessage.getPrice().forEach(asks::add);
//        bookMessage.getBids().forEach(bids::add);
//
//        bestAsk =  asks.stream().map(PriceLevel::getPrice).max(BigDecimal::compareTo).orElse(new BigDecimal(0));
//        bestBid =  bids.stream().map(PriceLevel::getPrice).max(BigDecimal::compareTo).orElse(new BigDecimal(0));
//    }


    public Set<PriceLevel> getAsks() {
        return asks;
    }

    public void setAsks(Set<PriceLevel> asks) {
        this.asks = asks;
    }

    public Set<PriceLevel> getBids() {
        return bids;
    }

    public void setBids(Set<PriceLevel> bids) {
        this.bids = bids;
    }

    public PriceLevel getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(PriceLevel bestAsk) {
        this.bestAsk = bestAsk;
    }

    public PriceLevel getBestBid() {
        return bestBid;
    }

    public void setBestBid(PriceLevel bestBid) {
        this.bestBid = bestBid;
    }


    public String getExchange() {
        return exchange;
    }

    @Override
    public String toString() {
        return "<------------------------------------->\n" +
                "asks:\n" + asks.stream().map(PriceLevel::toString).collect(Collectors.joining(",\n")) +
                "\nbest ask: " + bestAsk +
                "\nbest bid: " + bestBid +
                "\nbids:\n" + bids.stream().map(PriceLevel::toString).collect(Collectors.joining(",\n")) +
                "\n" + lastUpdated +
                "\n" + exchange +
                "\n>-------------------------------------<";
    }
}
