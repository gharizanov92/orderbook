package info.harizanov.orderbook.domain.message.response;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PriceLevel implements Comparable<PriceLevel> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceLevel that = (PriceLevel) o;
        return Objects.equals(price, that.price) && Objects.equals(volume, that.volume) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, volume, timestamp);
    }

    @Override
    public int compareTo(PriceLevel o) {
        int timeDiff = o.timestamp.compareTo(this.timestamp);
        if (timeDiff == 0) {
            int priceDiff = o.getPrice().compareTo(this.price);
            if (priceDiff == 0) {
                return o.getVolume().compareTo(this.volume);
            }
            return priceDiff;
        }
        return timeDiff;
    }

    @Override
    public String toString() {
        return "[ " + price +
                ", " + volume + " ]";
    }
}
