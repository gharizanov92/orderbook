package info.harizanov.orderbook.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kraken")
public class KrakenProperties {
    private String url;
    private Integer reconnectInterval;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(Integer reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }
}
