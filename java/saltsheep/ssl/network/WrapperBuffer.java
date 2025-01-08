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

    public WrapperBuffer(PacketBuffer buf, boolean isRemote) {
        this.buf = buf;
        this.isRemote = isRemote;
    }

    public WrapperBuffer(boolean isRemote, PacketBuffer buf) {
        this(buf, isRemote);
    }

    public int readInt() {
        return this.buf.readInt();
    }

    public WrapperBuffer writeInt(int value) {
        this.buf.writeInt(value);
        return this;
    }

    public long readLong() {
        return this.buf.readLong();
    }

    public WrapperBuffer writeLong(long value) {
        this.buf.writeLong(value);
        return this;
    }

    public short readShort() {
        return this.buf.readShort();
    }

    public WrapperBuffer writeShort(short value) {
        this.buf.writeShort(value);
        return this;
    }

    public byte readByte() {
        return this.buf.readByte();
    }

    public WrapperBuffer writeByte(byte value) {
        this.buf.writeByte(value);
        return this;
    }

    public float readFloat() {
        return this.buf.readFloat();
    }

    public WrapperBuffer writeFloat(float value) {
        this.buf.writeFloat(value);
        return this;
    }

    public double readDouble() {
        return this.buf.readDouble();
    }

    public WrapperBuffer writeDouble(double value) {
        this.buf.writeDouble(value);
        return this;
    }

    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    public WrapperBuffer writeBoolean(boolean value) {
        this.buf.writeBoolean(value);
        return this;
    }

    public char readChar() {
        return this.buf.readChar();
    }

    public WrapperBuffer writeChar(char value) {
        this.buf.writeChar(value);
        return this;
    }

    public String readString() {
        byte[] strCoding = this.buf.readByteArray();
        return new String(strCoding, StandardCharsets.UTF_8);
    }

    public WrapperBuffer writeString(String str) {
        byte[] strCoding = str.getBytes(StandardCharsets.UTF_8);
        this.buf.writeByteArray(strCoding);
        return this;
    }

    public BlockPos readBlockPos() {
        return this.buf.readBlockPos();
    }

    public void writeBlockPos(BlockPos pos) {
        this.buf.writeBlockPos(pos);
    }

    public ItemStack readItemStack() throws IOException {
        return this.buf.readItemStack();
    }

    public WrapperBuffer writeItemStack(ItemStack stack) {
        this.buf.writeItemStack(stack);
        return this;
    }

    public NBTTagCompound readCompoundTag() throws IOException {
        return this.buf.readCompoundTag();
    }

    public WrapperBuffer writeCompoundTag(NBTTagCompound compound) {
        this.buf.writeCompoundTag(compound);
        return this;
    }

    public ResourceLocation readResourceLocation() {
        return this.buf.readResourceLocation();
    }

    public WrapperBuffer writeResourceLocation(ResourceLocation rec) {
        this.buf.writeResourceLocation(rec);
        return this;
    }

    public UUID readUniqueId() {
        return this.buf.readUniqueId();
    }

    public WrapperBuffer writeUniqueId(UUID id) {
        this.buf.writeUniqueId(id);
        return this;
    }

    public ITextComponent readTextComponent() throws IOException {
        return this.buf.readTextComponent();
    }

    public WrapperBuffer writeTextComponent(ITextComponent component) {
        this.buf.writeTextComponent(component);
        return this;
    }

    public Date readTime() {
        return this.buf.readTime();
    }

    public WrapperBuffer writeTime(Date time) {
        this.buf.writeTime(time);
        return this;
    }

    @Nullable
    public IGetter<Entity> readEntity() {
        UUID entityId = this.buf.readUniqueId();
        return () -> {
            Entity entity = null;
            if (this.isRemote) {
                for (Entity each : (Minecraft.getMinecraft()).world.getLoadedEntityList()) {
                    if (each.getUniqueID().equals(entityId)) {
                        entity = each;
                        break;
                    }
                }
            } else {
                MinecraftServer server = SheepScriptLib.getMCServer();
                if (server != null)
                    entity = server.getEntityFromUuid(entityId);
            }
            return entity;
        };
    }

    public WrapperBuffer writeEntity(@Nonnull Entity entity) {
        this.buf.writeUniqueId(entity.getUniqueID());
        return this;
    }
}