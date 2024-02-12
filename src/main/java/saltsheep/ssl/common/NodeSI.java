package saltsheep.ssl.common;

//*Single interface
public class NodeSI<T> {
    public NodeSI<T> next;
    public T value;
    public NodeSI(T value){
        this.value = value;
    }
}
