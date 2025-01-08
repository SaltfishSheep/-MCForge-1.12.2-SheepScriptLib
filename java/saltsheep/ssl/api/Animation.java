package saltsheep.ssl.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.JobPuppet;

import java.util.Map;

@Deprecated
public class Animation {
    protected PartRotation[] nextRotations = new PartRotation[6];
    protected AniTick aniTicks = AniTick.T20;

    public Animation(int speed) {
        this(AniTick.values()[(speed < 0) ? 0 : ((speed > 9) ? 9 : speed)]);
    }

    public Animation(AniTick aniTicks) {
        this.aniTicks = aniTicks;
    }

    public static NBTTagCompound toNBT(Animation ani) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("aniTicks", ani.getAniTicks().ordinal());
        NBTTagList parts = new NBTTagList();
        for (int i = 0; i < 6; i++) {
            if (ani.getNextRotations()[i] != null)
                parts.appendTag(new NBTTagIntArray(new int[]{i, (ani.getNextRotations()[i]).x, (ani.getNextRotations()[i]).y, (ani.getNextRotations()[i]).z}));
        }
        nbt.setTag("rotations", parts);
        return nbt;
    }

    public static Animation fromNBT(NBTTagCompound aninbt) {
        AniTick aniTicks = AniTick.values()[aninbt.getInteger("aniTicks")];
        Animation ani = new Animation(aniTicks);
        NBTTagList parts = aninbt.getTagList("rotations", 11);
        for (int i = 0; i < parts.tagCount(); i++) {
            int[] rotation = parts.getIntArrayAt(i);
            ani.setRotation(rotation[0], rotation[1], rotation[2], rotation[3]);
        }
        return ani;
    }

    public Animation setRotation(int part, int x, int y, int z) {
        if (part < 0 || part > 5)
            return this;
        getNextRotations()[part] = new PartRotation(x, y, z);
        return this;
    }

    public boolean updateAni(EntityNPCInterface npc) {
        JobInterface jobInterface = npc.jobInterface;
        if (!(jobInterface instanceof IJobPuppet))
            return true;
        JobPuppet puppet = (JobPuppet) npc.jobInterface;
        Map<String, Object> tempdata = CommonEntityData.getData(npc);
        int runTicks = tempdata.containsKey("aniRunTicks") ? (((Integer) tempdata.get("aniRunTicks")).intValue() + 1) : 0;
        tempdata.put("aniRunTicks", Integer.valueOf(runTicks));


        boolean flag = getAniTicks().updateAniTick(this, npc, runTicks);
        if (flag)
            tempdata.remove("aniRunTicks");
        return flag;
    }

    public int getRunTicks() {
        return (getAniTicks()).runTicks;
    }

    public PartRotation[] getNextRotations() {
        return this.nextRotations;
    }

    public AniTick getAniTicks() {
        return this.aniTicks;
    }


    public enum AniTick {
        T80(0, 80), T48(1, 48), T26(2, 26), T20(3, 20), T14(4, 14), T8(5, 8), T6(6, 6), T4(7, 4), T2(7, 2), T1(7, 1);
        private final int runTicks;
        private final int npcSpeed;

        AniTick(int npcSpeed, int runTicks) {
            this.npcSpeed = npcSpeed;
            this.runTicks = runTicks;
        }


        public boolean updateAniTick(Animation ani, EntityNPCInterface npc, int runTicks) {
            IJobPuppet puppet = (IJobPuppet) npc.jobInterface;
            Map<String, Object> tempdata = CommonEntityData.getData(npc);
            if (runTicks == 0) {
                for (int i = 0; i < 6; i++) {
                    Animation.PartRotation rotation = ani.getNextRotations()[i];
                    if (rotation != null) {


                        IJobPuppet.IJobPuppetPart end = puppet.getPart(i + 6);

                        end.setRotation(rotation.x, rotation.y, rotation.z);
                    }
                }
                puppet.setAnimationSpeed(this.npcSpeed);


            } else if (runTicks == this.runTicks) {
                for (int i = 0; i < 6; i++) {
                    IJobPuppet.IJobPuppetPart start = puppet.getPart(i);
                    IJobPuppet.IJobPuppetPart end = puppet.getPart(i + 6);

                    if (start.getRotationX() != end.getRotationX() || start.getRotationY() != end.getRotationY() || start.getRotationZ() != end.getRotationZ()) {
                        start.setRotation(end.getRotationX(), end.getRotationY(), end.getRotationZ());
                    }
                }
                return true;
            }
            return false;
        }
    }

    public static class PartRotation {
        public final int x;
        public final int y;
        public final int z;

        protected PartRotation(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Part {
        public static int HEAD = 0, LARM = 1, RARM = 2, BODY = 3, LLEG = 4, RLEG = 5;
    }
}