package saltsheep.ssl.common;

@FunctionalInterface
public interface IGetter<T> {
    T get();
}