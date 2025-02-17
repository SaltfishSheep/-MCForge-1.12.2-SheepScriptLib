package saltsheep.ssl.core;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Trans implements IClassTransformer {

    private static final Map<String, List<Function<byte[], byte[]>>> transformers = new HashMap();

    public static void register(String className, Function<byte[], byte[]> editor) {
        List<Function<byte[], byte[]>> list = transformers.get(className);
        if (list == null) {
            list = new LinkedList<>();
            transformers.put(className, list);
        }
        list.add(editor);
    }
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        try {
            List<Function<byte[], byte[]>> list = transformers.get(transformedName);
            if (list != null)
                for (Function<byte[], byte[]> editor : list)
                    basicClass = editor.apply(basicClass);
        } catch (Throwable error) {
            error.printStackTrace();
        }
        return basicClass;
    }
}
