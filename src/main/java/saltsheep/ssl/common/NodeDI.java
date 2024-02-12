package saltsheep.ssl.common;

//*Double interface
public class NodeDI<T> {
    public NodeDI<T> up;
    public NodeDI<T> down;
    T value;
    public NodeDI(T value){
        this.value = value;
    }
}
