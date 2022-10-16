package info.harizanov.orderbook.domain.message.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Periodic HeartBeat message: {"event":"heartbeat"}
 */
public class HeartBeat {
    public static final Gson GSON = new Gson();
    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public static Mono<HeartBeat> parse(String json) {
        try {
            return Mono.just(GSON.fromJson(json, new TypeToken<HeartBeat>() {}.getType()));
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "event='" + event + '\'' +
                '}';
    }
}
