package saltsheep.ssl.puppet.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import saltsheep.ssl.core.DescriptorUtils;

public class Trans implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        try {
            boolean isTrans = false;
            if (transformedName.equals("noppes.npcs.roles.JobPuppet")) {
                isTrans = true;
                ClassReader reader = new ClassReader(basicClass);
                ClassNode cnode = new ClassNode();
                reader.accept((ClassVisitor) cnode, 0);

                String dataD = DescriptorUtils.getFieldDescriptor("saltsheep.ssl.puppet.handler.JobPuppetSSLData");
                FieldNode dataF = new FieldNode(2, "sslData", dataD, null, null);
                cnode.fields.add(dataF);
                MethodNode getDataM = new MethodNode(1, "getSSLData", DescriptorUtils.getMethodDescriptor("saltsheep.ssl.puppet.handler.JobPuppetSSLData", new String[0]), null, null);
                getDataM.visitVarInsn(25, 0);
                getDataM.visitFieldInsn(180, DescriptorUtils.ct("noppes.npcs.roles.JobPuppet"), "sslData", dataD);
                getDataM.visitInsn(176);
                cnode.methods.add(getDataM);
                MethodNode setDataM = new MethodNode(1, "setSSLData", DescriptorUtils.getMethodDescriptor("void", new String[]{"saltsheep.ssl.puppet.handler.JobPuppetSSLData"}), null, null);
                setDataM.visitVarInsn(25, 0);
                setDataM.visitVarInsn(25, 1);
                setDataM.visitFieldInsn(181, DescriptorUtils.ct("noppes.npcs.roles.JobPuppet"), "sslData", dataD);
                setDataM.visitInsn(177);
                cnode.methods.add(setDataM);

                MethodNode rawX = null;
                MethodNode rawY = null;
                MethodNode rawZ = null;
                String methodDesc = DescriptorUtils.getMethodDescriptor("float", new String[]{"noppes.npcs.roles.JobPuppet$PartConfig", "noppes.npcs.roles.JobPuppet$PartConfig", "float"});
                for (MethodNode node : cnode.methods) {
                    if (node.name.startsWith("getRotation") && node.desc.equals(methodDesc)) {
                        switch (node.name) {
                            case "getRotationX":
                                rawX = node;

                            case "getRotationY":
                                rawY = node;

                            case "getRotationZ":
                                rawZ = node;
                        }

                    }
                }
                cnode.methods.remove(rawX);
                cnode.methods.remove(rawY);
                cnode.methods.remove(rawZ);
                String newMCanUseDesc = DescriptorUtils.getMethodDescriptor("boolean", new String[]{"noppes.npcs.roles.JobPuppet"});
                String newMDesc = DescriptorUtils.getMethodDescriptor("float", new String[]{"noppes.npcs.roles.JobPuppet", "noppes.npcs.roles.JobPuppet$PartConfig"});
                MethodNode newX = new MethodNode(1, "getRotationX", methodDesc, null, null);
                Label elseLable = new Label();
                newX.visitVarInsn(25, 0);
                newX.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newX.visitJumpInsn(153, elseLable);
                newX.visitVarInsn(25, 0);
                newX.visitVarInsn(25, 1);
                newX.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationXAgency", newMDesc, false);
                newX.visitInsn(174);
                newX.visitLabel(elseLable);
                rawX.accept((MethodVisitor) newX);
                MethodNode newY = new MethodNode(1, "getRotationY", methodDesc, null, null);
                elseLable = new Label();
                newY.visitVarInsn(25, 0);
                newY.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newY.visitJumpInsn(153, elseLable);
                newY.visitVarInsn(25, 0);
                newY.visitVarInsn(25, 1);
                newY.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationYAgency", newMDesc, false);
                newY.visitInsn(174);
                newY.visitLabel(elseLable);
                rawY.accept((MethodVisitor) newY);
                MethodNode newZ = new MethodNode(1, "getRotationZ", methodDesc, null, null);
                elseLable = new Label();
                newZ.visitVarInsn(25, 0);
                newZ.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "canReplaceRotation", newMCanUseDesc, false);
                newZ.visitJumpInsn(153, elseLable);
                newZ.visitVarInsn(25, 0);
                newZ.visitVarInsn(25, 1);
                newZ.visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "getRotationZAgency", newMDesc, false);
                newZ.visitInsn(174);
                newZ.visitLabel(elseLable);
                rawZ.accept((MethodVisitor) newZ);
                cnode.methods.add(newX);
                cnode.methods.add(newY);
                cnode.methods.add(newZ);
                cnode.interfaces.add(DescriptorUtils.ct("saltsheep.ssl.puppet.handler.IJobPuppetSSL"));
                ClassWriter writer = new ClassWriter(3);
                cnode.accept(writer);
                basicClass = writer.toByteArray();
            } else if (transformedName.equals("noppes.npcs.client.renderer.RenderNPCInterface")) {
                isTrans = true;
                ClassReader reader = new ClassReader(basicClass);
                ClassNode cnode = new ClassNode();
                reader.accept((ClassVisitor) cnode, 0);
                MethodNode applyRotationsRaw = null;
                for (MethodNode method : cnode.methods) {
                    if (method.name.equals("applyRotations")) {
                        applyRotationsRaw = method;
                        break;
                    }
                }
                if (applyRotationsRaw == null)
                    throw new RuntimeException("noppes.npcs.client.renderer.RenderNPCInterface should have method applyRotations(LT;FFF)V");
                final String newDesc = DescriptorUtils.getMethodDescriptor("void", new String[]{"noppes.npcs.client.renderer.RenderNPCInterface", "java.lang.Object", "float"});
                MethodNode applyRotationsNew = new MethodNode(327680, applyRotationsRaw.access, applyRotationsRaw.name, applyRotationsRaw.desc, applyRotationsRaw.signature, (String[]) applyRotationsRaw.exceptions.toArray((Object[]) new String[applyRotationsRaw.exceptions.size()])) {
                    public void visitInsn(int i) {
                        if (i == 177) {
                            visitVarInsn(25, 0);
                            visitVarInsn(25, 1);
                            visitVarInsn(23, 4);
                            visitMethodInsn(184, DescriptorUtils.ct("saltsheep.ssl.puppet.handler.NeoPuppetHandler"), "applyRotationsAfter", newDesc, false);
                        }
                        super.visitInsn(i);
                    }
                };
                applyRotationsRaw.accept((MethodVisitor) applyRotationsNew);
                cnode.methods.remove(applyRotationsRaw);
                cnode.methods.add(applyRotationsNew);
                ClassWriter writer = new ClassWriter(3);
                cnode.accept(writer);
                basicClass = writer.toByteArray();
            }
            if (isTrans)
                System.out.println(transformedName + " has been transformed by SheepScriptLib.");
        } catch (Throwable error) {
            error.printStackTrace();
        }
        return basicClass;
    }
}
