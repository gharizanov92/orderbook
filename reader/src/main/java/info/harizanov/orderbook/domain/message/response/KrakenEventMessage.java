package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Mono;

public class KrakenEventMessage {
    private Long connectionID;
    private String event;
    private String status;
    private String version;

    public static final Gson GSON = new Gson();

    public Long getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(Long connectionID) {
        this.connectionID = connectionID;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Event{" +
                "connectionID=" + connectionID +
                ", event='" + event + '\'' +
                ", status='" + status + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public static Mono<KrakenEventMessage> parse(String json) {
        try {
            return Mono.just(GSON.fromJson(json, new TypeToken<KrakenEventMessage>() {}.getType()));
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
