package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import java.util.HashMap;
import java.util.Map;

public class PuppetTaskLoaders {

    private static final Map<Integer,JobPuppetSSLData.IAnimationTaskLoader> loaders = new HashMap<>();

    public static final void register(JobPuppetSSLData.IAnimationTaskLoader loader){
        if(loaders.containsKey(loader.id()))
            throw new IllegalStateException("There's already a loader has id:"+loader.id());
        loaders.put(loader.id(),loader);
    }

    public static final JobPuppetSSLData.IAnimationTask readTask(NBTTagCompound main){
        int id = main.getInteger("id");
        JobPuppetSSLData.IAnimationTaskLoader loader = loaders.get(id);
        if(loader==null)
            throw new IllegalStateException("There's no such loader has id:"+id);
        return loader.read(main.getTag("data"));
    }

    public static final NBTTagCompound writeTask(JobPuppetSSLData.IAnimationTask task){
        NBTTagCompound main = new NBTTagCompound();
        JobPuppetSSLData.IAnimationTaskLoader loader = task.getLoader();
        main.setInteger("id",loader.id());
        NBTBase data = loader.write(task);
        if(data!=null)
            main.setTag("data",data);
        return main;
    }

    static{
        register(TaskClear.Loader.INSTANCE);
        register(TaskDelay.Loader.INSTANCE);
        register(TaskPlay.Loader.INSTANCE);
        register(TaskReset.Loader.INSTANCE);
    }

}
