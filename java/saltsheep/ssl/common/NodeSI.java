package saltsheep.ssl.common;

public class NodeSI<T> {
    public NodeSI<T> next;
    public T value;

    public NodeSI(T value) {
        this.value = value;
    }
}