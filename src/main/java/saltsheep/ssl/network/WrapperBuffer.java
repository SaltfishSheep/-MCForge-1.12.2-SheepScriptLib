package saltsheep.ssl.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import saltsheep.ssl.SheepScriptLib;
import saltsheep.ssl.common.IGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class WrapperBuffer {

    private final boolean isRemote;
    private final PacketBuffer buf;

    public WrapperBuffer(PacketBuffer buf, boolean isRemote){
        this.buf = buf;
        this.isRemote = isRemote;
    }

    public WrapperBuffer(boolean isRemote, PacketBuffer buf){
        this(buf,isRemote);
    }

    public int readInt(){
        return buf.readInt();
    }

    public WrapperBuffer writeInt(int value){
        buf.writeInt(value);
        return this;
    }

    public long readLong(){
        return buf.readLong();
    }

    public WrapperBuffer writeLong(long value){
        buf.writeLong(value);
        return this;
    }

    public short readShort(){
        return buf.readShort();
    }

    public WrapperBuffer writeShort(short value){
        buf.writeShort(value);
        return this;
    }

    public byte readByte(){
        return buf.readByte();
    }

    public WrapperBuffer writeByte(byte value){
        buf.writeByte(value);
        return this;
    }

    public float readFloat(){
        return buf.readFloat();
    }

    public WrapperBuffer writeFloat(float value){
        buf.writeFloat(value);
        return this;
    }

    public double readDouble(){
        return buf.readDouble();
    }

    public WrapperBuffer writerDouble(double value){
        buf.writeDouble(value);
        return this;
    }

    public boolean readBoolean(){
        return buf.readBoolean();
    }

    public WrapperBuffer writeBoolean(boolean value){
        buf.writeBoolean(value);
        return this;
    }

    public char readChar(){
        return buf.readChar();
    }

    public WrapperBuffer writeChar(char value){
        buf.writeChar(value);
        return this;
    }

    public String readString(){
        byte[] strCoding = buf.readByteArray();
        return new String(strCoding, StandardCharsets.UTF_8);
    }

    public WrapperBuffer writeString(String str){
        byte[] strCoding = str.getBytes(StandardCharsets.UTF_8);
        buf.writeByteArray(strCoding);
        return this;
    }

    public BlockPos readBlockPos(){
        return buf.readBlockPos();
    }

    public void writeBlockPos(BlockPos pos){
        buf.writeBlockPos(pos);
    }

    public ItemStack readItemStack() throws IOException {
        return buf.readItemStack();
    }

    public WrapperBuffer writeItemStack(ItemStack stack){
        buf.writeItemStack(stack);
        return this;
    }

    public NBTTagCompound readCompoundTag() throws IOException {
        return buf.readCompoundTag();
    }

    public WrapperBuffer writeCompoundTag(NBTTagCompound compound){
        buf.writeCompoundTag(compound);
        return this;
    }

    public ResourceLocation readResourceLocation(){
        return buf.readResourceLocation();
    }

    public WrapperBuffer writeResourceLocation(ResourceLocation rec){
        buf.writeResourceLocation(rec);
        return this;
    }

    public UUID readUniqueId(){
        return buf.readUniqueId();
    }

    public WrapperBuffer writeUniqueId(UUID id){
        buf.writeUniqueId(id);
        return this;
    }

    public ITextComponent readTextComponent() throws IOException{
        return buf.readTextComponent();
    }

    public WrapperBuffer writeTextComponent(ITextComponent component){
        buf.writeTextComponent(component);
        return this;
    }

    public Date readTime(){
        return buf.readTime();
    }

    public WrapperBuffer writeTime(Date time){
        buf.writeTime(time);
        return this;
    }

    @Nullable
    public IGetter<Entity> readEntity(){
        UUID entityId = buf.readUniqueId();
        return ()->{
            Entity entity = null;
            if(isRemote){
                entities:for(Entity each:Minecraft.getMinecraft().world.getLoadedEntityList()) {
                    if (each.getUniqueID().equals(entityId)) {
                        entity = each;
                        break entities;
                    }
                }
            } else {
                MinecraftServer server = SheepScriptLib.getMCServer();
                if(server!=null)
                    entity = server.getEntityFromUuid(entityId);
            }
            return entity;
        };
    }

    public WrapperBuffer writeEntity(@Nonnull Entity entity){
        buf.writeUniqueId(entity.getUniqueID());;
        return this;
    }

}
