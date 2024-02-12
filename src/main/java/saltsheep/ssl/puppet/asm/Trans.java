package saltsheep.ssl.puppet.asm;

import org.objectweb.asm.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.*;

public class Trans implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        try {
            if(transformedName.equals("noppes.npcs.roles.JobPuppet")){
                ClassReader reader = new ClassReader(basicClass);
                ClassNode cnode = new ClassNode();
                reader.accept(cnode, 0);
                //*添加属性以及获取渠道
                String dataD = DescriptorUtils.getFieldDescriptor("saltsheep.ssl.puppet.handler.JobPuppetSSLData");
                FieldNode dataF = new FieldNode(Opcodes.ACC_PRIVATE,"sslData",dataD,null,null);
                cnode.fields.add(dataF);
                MethodNode getDataM = new MethodNode(Opcodes.ACC_PUBLIC,"getSSLData", DescriptorUtils.getMethodDescriptor("saltsheep.ssl.puppet.handler.JobPuppetSSLData"),null,null);
                getDataM.visitVarInsn(Opcodes.ALOAD, 0);//*读取this
                getDataM.visitFieldInsn(Opcodes.GETFIELD, ct("noppes.npcs.roles.JobPuppet"), "sslData", dataD);//*读取this.sslData
                getDataM.visitInsn(Opcodes.ARETURN);//* return this.sslData
                cnode.methods.add(getDataM);
                MethodNode setDataM = new MethodNode(Opcodes.ACC_PUBLIC, "setSSLData", DescriptorUtils.getMethodDescriptor("void","saltsheep.ssl.puppet.handler.JobPuppetSSLData"),null,null);
                setDataM.visitVarInsn(Opcodes.ALOAD,0);
                setDataM.visitVarInsn(Opcodes.ALOAD,1);
                setDataM.visitFieldInsn(Opcodes.PUTFIELD, ct("noppes.npcs.roles.JobPuppet"), "sslData", dataD);
                setDataM.visitInsn(Opcodes.RETURN);
                cnode.methods.add(setDataM);
                //*编辑getRotationX(Y/Z)方法
                MethodNode rawX = null;
                MethodNode rawY = null;
                MethodNode rawZ = null;
                String methodDesc = DescriptorUtils.getMethodDescriptor("float","noppes.npcs.roles.JobPuppet$PartConfig", "noppes.npcs.roles.JobPuppet$PartConfig", "float");
                for(MethodNode node:cnode.methods){
                    if(node.name.startsWith("getRotation")&&node.desc.equals(methodDesc)){
                        switch (node.name){
                            case "getRotationX":
                                rawX = node;
                                break;
                            case "getRotationY":
                                rawY = node;
                                break;
                            case "getRotationZ":
                                rawZ = node;
                                break;
                        }
                    }
                }
                cnode.methods.remove(rawX);
                cnode.methods.remove(rawY);
                cnode.methods.remove(rawZ);
                String newMCanUseDesc = DescriptorUtils.getMethodDescriptor("boolean", "noppes.npcs.roles.JobPuppet", "noppes.npcs.roles.JobPuppet$PartConfig");
                String newMDesc = DescriptorUtils.getMethodDescriptor("float", "noppes.npcs.roles.JobPuppet", "noppes.npcs.roles.JobPuppet$PartConfig");
                MethodNode newX = new MethodNode(Opcodes.ACC_PUBLIC, "getRotationX", methodDesc, null, null);
                Label elseLable = new Label();
                newX.visitVarInsn(Opcodes.ALOAD, 0);
                newX.visitVarInsn(Opcodes.ALOAD, 1);
                newX.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newX.visitJumpInsn(Opcodes.IFEQ,elseLable);//*==false
                newX.visitVarInsn(Opcodes.ALOAD, 0);
                newX.visitVarInsn(Opcodes.ALOAD, 1);
                newX.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationXAgency", newMDesc, false);
                newX.visitInsn(Opcodes.FRETURN);
                newX.visitLabel(elseLable);//*定义跳转标签
                rawX.accept(newX);//*继承旧方法
                MethodNode newY = new MethodNode(Opcodes.ACC_PUBLIC, "getRotationY", methodDesc, null, null);
                elseLable = new Label();
                newY.visitVarInsn(Opcodes.ALOAD, 0);
                newY.visitVarInsn(Opcodes.ALOAD, 1);
                newY.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newY.visitJumpInsn(Opcodes.IFEQ,elseLable);//*==false
                newY.visitVarInsn(Opcodes.ALOAD, 0);
                newY.visitVarInsn(Opcodes.ALOAD, 1);
                newY.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationYAgency", newMDesc, false);
                newY.visitInsn(Opcodes.FRETURN);
                newY.visitLabel(elseLable);//*定义跳转标签
                rawY.accept(newY);//*继承旧方法
                MethodNode newZ = new MethodNode(Opcodes.ACC_PUBLIC, "getRotationZ", methodDesc, null, null);
                elseLable = new Label();
                newZ.visitVarInsn(Opcodes.ALOAD, 0);
                newZ.visitVarInsn(Opcodes.ALOAD, 1);
                newZ.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newZ.visitJumpInsn(Opcodes.IFEQ,elseLable);//*==false
                newZ.visitVarInsn(Opcodes.ALOAD, 0);
                newZ.visitVarInsn(Opcodes.ALOAD, 1);
                newZ.visitMethodInsn(Opcodes.INVOKESTATIC, ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationZAgency", newMDesc, false);
                newZ.visitInsn(Opcodes.FRETURN);
                newZ.visitLabel(elseLable);//*定义跳转标签
                rawZ.accept(newZ);//*继承旧方法
                cnode.methods.add(newX);
                cnode.methods.add(newY);
                cnode.methods.add(newZ);
                cnode.interfaces.add(ct("saltsheep.ssl.puppet.handler.IJobPuppetSSL"));
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
                cnode.accept(writer);//*把数据转手给writer
                basicClass = writer.toByteArray();
            }
        } catch (Throwable error) {
            error.printStackTrace();
        }
        return basicClass;
    }

    //*Class name Translate
    public static String ct(String className){
        return className.replace('.','/');
    }
}
