package info.harizanov.orderbook.domain.message.response;

import java.util.Optional;

public abstract class BaseResponseMessage<T> {
    public abstract Optional<T> tryParse();
}
