package saltsheep.ssl.common;

public class NodeDI<T> {
    public NodeDI<T> up;
    public NodeDI<T> down;
    T value;

    public NodeDI(T value) {
        this.value = value;
    }
}