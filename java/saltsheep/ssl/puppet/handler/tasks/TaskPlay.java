package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;

public class TaskPlay
        extends TaskBaseTimer {
    private static final float quarterT = 1.5707964F;
    public PlayMode mode = PlayMode.NORMAL;
    private final float xEnd;
    private final float yEnd;
    private final float zEnd;
    private double lastTime;
    private float lastPersent;


    public TaskPlay(long durationTime, float xEnd, float yEnd, float zEnd) {
        super(durationTime);
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.zEnd = zEnd;
    }

    public void onAttended(JobPuppetSSLData.RotationController controller) {
    }

    public void onEnded(JobPuppetSSLData.RotationController controller) {
        super.onEnded(controller);
        controller.xStart = this.xEnd;
        controller.yStart = this.yEnd;
        controller.zStart = this.zEnd;
    }

    public float getCurrentPercent() {
        double nowTime = getTime();
        if (this.lastTime != nowTime) {
            double spendTime = nowTime - this.startTime;
            this.lastPersent = (float) (spendTime / this.durationTime);
            this.lastTime = nowTime;
        }
        return this.lastPersent;
    }

    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return this.mode.getRotation(controller.xStart, this.xEnd, getCurrentPercent());
    }

    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return this.mode.getRotation(controller.yStart, this.yEnd, getCurrentPercent());
    }

    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return this.mode.getRotation(controller.zStart, this.zEnd, getCurrentPercent());
    }

    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }

    public enum PlayMode {
        NORMAL {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * persent;
            }
        },

        STF_SMALL {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * (1.0F - MathHelper.cos(TaskPlay.quarterT * persent));
            }
        },

        FTS_SMALL {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * MathHelper.sin(TaskPlay.quarterT * persent);
            }
        },

        STF_NORMAL {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * persent * persent;
            }
        },

        FTS_NORMAL {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * (2.0F * persent - persent * persent);
            }
        },

        STF_LARGE {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                return start + change * persent * persent * persent;
            }
        },

        FTS_LARGE {
            public float getRotation(float start, float end, float persent) {
                float change = end - start;
                float x = persent - 1.0F;
                return start + change * (1.0F + x * x * x);
            }
        };

        public abstract float getRotation(float param1Float1, float param1Float2, float param1Float3);
    }

    public static class Loader
            implements JobPuppetSSLData.IAnimationTaskLoader {
        public static final JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();


        public int id() {
            return 2;
        }


        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            NBTTagCompound data = (NBTTagCompound) taskData;
            long durationTime = data.getLong("duration");
            float endX = data.getFloat("x");
            float endY = data.getFloat("y");
            float endZ = data.getFloat("z");
            TaskPlay.PlayMode mode = TaskPlay.PlayMode.valueOf(data.getString("mode"));
            TaskPlay task = new TaskPlay(durationTime, endX, endY, endZ);
            task.mode = mode;
            return task;
        }


        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            NBTTagCompound data = new NBTTagCompound();
            TaskPlay normalizeTask = (TaskPlay) task;
            data.setLong("duration", normalizeTask.durationTime);
            data.setFloat("x", normalizeTask.xEnd);
            data.setFloat("y", normalizeTask.yEnd);
            data.setFloat("z", normalizeTask.zEnd);
            data.setString("mode", normalizeTask.mode.name());
            return data;
        }
    }
}