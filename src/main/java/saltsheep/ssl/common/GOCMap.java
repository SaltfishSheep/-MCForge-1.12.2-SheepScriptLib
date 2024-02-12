package saltsheep.ssl.common;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class GOCMap<K,V> extends HashMap<K,V> {

    private final Callable<V> creator;

    public GOCMap(Callable<V> creator){
        this.creator = creator;
    }

    public V getOrCreate(K key){
        V value = this.get(key);
        if(value==null){
            try {
                value = creator.call();
                this.put(key,value);
            } catch (Exception e) {
                return null;
            }
        }
        return value;
    }

}
