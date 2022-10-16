package info.harizanov.orderbook;

import info.harizanov.orderbook.domain.message.request.KrakenCurrency;
import info.harizanov.orderbook.domain.message.request.SubscriptionType;
import info.harizanov.orderbook.template.KrakenTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.util.function.Tuples;

@SpringBootApplication
public class OrderbookDemoApplication implements ApplicationRunner {

    private final KrakenTemplate krakenTemplate;

    public OrderbookDemoApplication(KrakenTemplate krakenTemplate) {
        this.krakenTemplate = krakenTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        krakenTemplate.subscribeForExchange(Tuples.of(KrakenCurrency.ETH, KrakenCurrency.USD), SubscriptionType.ALL);
        krakenTemplate.getHeartBeatFeed().subscribe(System.out::println);
        krakenTemplate.getTradeFeed().doOnNext(System.out::println).blockLast();
    }
}
