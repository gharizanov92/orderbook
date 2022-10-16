package info.harizanov.orderbook.domain.message.response;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class PriceLevel {
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal timestamp;

    private String updateType;

    public static PriceLevel fromTokens(final List<String> args) {
        final var payload = new PriceLevel();
        final Iterator<String> it = args.iterator();
        payload.price = new BigDecimal(it.next());
        payload.volume = new BigDecimal(it.next());
        payload.timestamp = new BigDecimal(it.next());

        if (it.hasNext()) {
            payload.updateType = it.next();
        }

        return payload;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BigDecimal timestamp) {
        this.timestamp = timestamp;
    }
}