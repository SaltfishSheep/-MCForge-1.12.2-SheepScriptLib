package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;

public class TaskPlay extends TaskBaseTimer {

    private float xEnd;
    private float yEnd;
    private float zEnd;
    public PlayMode mode = PlayMode.NORMAL;

    private double lastTime;
    private float lastPersent;

    public TaskPlay(long durationTime, float xEnd, float yEnd, float zEnd){
        super(durationTime);
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.zEnd = zEnd;
    }

    @Override
    public void onAttended(JobPuppetSSLData.RotationController controller) {
    }

    @Override
    public void onEnded(JobPuppetSSLData.RotationController controller) {
        super.onEnded(controller);
        controller.xStart = xEnd;
        controller.yStart = yEnd;
        controller.zStart = zEnd;
    }

    public float getCurrentPercent(){
        double nowTime = getTime();
        if(lastTime!=nowTime) {
            double spendTime = nowTime - startTime;
            lastPersent = (float) (spendTime / (durationTime));
            lastTime = nowTime;
        }
        return lastPersent;
    }

    @Override
    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return mode.getRotation(controller.xStart,xEnd, getCurrentPercent());
    }

    @Override
    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return mode.getRotation(controller.yStart,yEnd, getCurrentPercent());
    }

    @Override
    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return mode.getRotation(controller.zStart,zEnd, getCurrentPercent());
    }

    @Override
    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }

    private static float quarterT = (float) (Math.PI*0.5);

    public static enum PlayMode {

        NORMAL(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return (float) (start+(change*persent));
            }
        },
        //*slow to fast - small (max speed:1.57)
        STF_SMALL(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return start + (change*(1 - MathHelper.cos(quarterT*persent)));
            }
        },
        //*fast to slow - small (max speed:1.57)
        FTS_SMALL(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return start + (change*MathHelper.sin(quarterT*persent));
            }
        },
        //*slow to fast - normal (max speed:2)
        STF_NORMAL(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return start + (change*persent*persent);
            }
        },
        //*fast to slow - normal (max speed:2)
        FTS_NORMAL(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return start + (change*(2*persent-persent*persent));
            }
        },
        //*slow to fast - large (max speed:3)
        STF_LARGE(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                return start + (change*persent*persent*persent);
            }
        },
        //*fast to slow - large (max speed:3)
        FTS_LARGE(){
            @Override
            public float getRotation(float start, float end, float persent){
                float change = end-start;
                float x = persent-1;
                return start + (change*(1+x*x*x));
            }
        };

        public abstract float getRotation(float start, float end, float persent);

    }

    public static class Loader implements JobPuppetSSLData.IAnimationTaskLoader {

        public final static JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();

        private Loader(){}

        @Override
        public int id() {
            return 2;
        }

        @Override
        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            NBTTagCompound data = (NBTTagCompound) taskData;
            long durationTime = data.getLong("duration");
            float endX = data.getFloat("x");
            float endY = data.getFloat("y");
            float endZ = data.getFloat("z");
            PlayMode mode = PlayMode.valueOf(data.getString("mode"));
            TaskPlay task = new TaskPlay(durationTime,endX,endY,endZ);
            task.mode = mode;
            return task;
        }

        @Override
        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            NBTTagCompound data = new NBTTagCompound();
            TaskPlay normalizeTask = (TaskPlay) task;
            data.setLong("duration",normalizeTask.durationTime);
            data.setFloat("x",normalizeTask.xEnd);
            data.setFloat("y",normalizeTask.yEnd);
            data.setFloat("z",normalizeTask.zEnd);
            data.setString("mode",normalizeTask.mode.name());
            return data;
        }

    }

}
