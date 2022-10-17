package info.harizanov.orderbook;

import info.harizanov.orderbook.domain.OrderbookSummary;
import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import info.harizanov.orderbook.template.KrakenTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class OrderbookApplication implements ApplicationRunner {

    private final KrakenTemplate krakenTemplate;

    public static void main(String[] args) {
        SpringApplication.run(OrderbookApplication.class, args);
    }

    public OrderbookApplication(KrakenTemplate krakenTemplate) {
        this.krakenTemplate = krakenTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        final Map<String, OrderbookSummary> orderBook = new HashMap<>();

        krakenTemplate.subscribeForExchange(Tuples.of(KrakenCurrency.ETH, KrakenCurrency.USD), SubscriptionType.BOOK);
        krakenTemplate.subscribeForExchange(Tuples.of(KrakenCurrency.BTC, KrakenCurrency.USD), SubscriptionType.BOOK);

        krakenTemplate.getBookFeed()
                .doOnNext(update -> orderBook.computeIfAbsent(update.getT1(), OrderbookSummary::new).update(update.getT2()))
                .blockLast();
    }
}
