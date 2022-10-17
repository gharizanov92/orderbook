package info.harizanov.orderbook.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The object holder, which might be used for lazy object initialization.
 *
 * @author Alexey Stashok
 */
public abstract class Holder<E> {

    public static <T> Holder.LazyHolder<T> lazyHolder(final Supplier<T> factory) {
        return new Holder.LazyHolder<T>() {

            @Override
            protected T evaluate() {
                return factory.get();
            }
        };
    }

    public abstract E get();

    public abstract void set(E val);

    @Override
    public String toString() {
        final E obj = get();
        return obj != null ? "{" + obj + "}" : null;
    }

    public static abstract class LazyHolder<E> extends Holder<E> {
        private volatile boolean isSet;
        private volatile E value;

        @Override
        public final E get() {
            if (isSet) {
                return value;
            }

            synchronized (this) {
                if (!isSet) {
                    value = evaluate();
                    isSet = true;
                }
            }

            return value;
        }

        @Override
        public void set(E val) {
            synchronized (this) {
                value = val;
            }
        }

        synchronized void evict() {
            isSet = false;
        }

        protected abstract E evaluate();
    }
}
