/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
@MCVersion("1.7.10")
public class CaveASMHandler implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{CaveTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static class CaveTransformer implements IClassTransformer {

		private static final HashMap<String, ClassPatch> classes = new HashMap();

		private static enum ClassPatch {
			DUNGEONRATE("net.minecraft.world.gen.ChunkProviderGenerate", "aqz"),
			FLATGEN("net.minecraft.world.gen.ChunkProviderFlat", "aqu"),
			//STRONGHOLDSOLID("net.minecraft.world.gen.structure.StructureStrongholdPieces$Stronghold", "avc"),
			//STRONGHOLDSOLID("net.minecraft.world.gen.structure.StructureComponent", "avk"),
			;

			private final String obfName;
			private final String deobfName;

			private static final ClassPatch[] list = values();

			private ClassPatch(String deobf, String obf) {
				obfName = obf;
				deobfName = deobf;
			}

			private byte[] apply(byte[] data) {
				ClassNode cn = new ClassNode();
				ClassReader classReader = new ClassReader(data);
				classReader.accept(cn, 0);
				switch(this) {
					case DUNGEONRATE: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73153_a", "populate", "(Lnet/minecraft/world/chunk/IChunkProvider;II)V");
						String name = "DUNGEON";
						String own = "net/minecraftforge/event/terraingen/PopulateChunkEvent/Populate/EventType";
						boolean primed = false;
						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.GETSTATIC) {
								FieldInsnNode fin = (FieldInsnNode)ain;
								if (fin.name.equals(name)) {
									primed = true;
								}
							}
							else if (primed && ain.getOpcode() == Opcodes.BIPUSH) {
								String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_73230_p" : "worldObj";
								InsnList li = new InsnList();
								li.add(new VarInsnNode(Opcodes.ALOAD, 0));
								li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/ChunkProviderGenerate", world, "Lnet/minecraft/world/World;"));
								li.add(new VarInsnNode(Opcodes.ILOAD, 2));
								li.add(new VarInsnNode(Opcodes.ILOAD, 3));
								li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/CaveControl/CaveHooks", "getDungeonGenAttempts", "(Lnet/minecraft/world/World;II)I", false));
								m.instructions.insert(ain, li);
								m.instructions.remove(ain);
								break;
							}
						}
						break;
					}
					case FLATGEN: {
						MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73154_d", "provideChunk", "(II)Lnet/minecraft/world/chunk/Chunk;");
						AbstractInsnNode ain = ReikaASMHelper.getFirstFieldCall(cn, m, cn.name, FMLForgePlugin.RUNTIME_DEOBF ? "field_82696_f" : "structureGenerators");
						ain = ain.getPrevious();

						String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_73163_a" : "worldObj";
						InsnList li = new InsnList();
						li.add(new VarInsnNode(Opcodes.ALOAD, 0));
						li.add(new VarInsnNode(Opcodes.ALOAD, 0));
						li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/ChunkProviderFlat", world, "Lnet/minecraft/world/World;"));
						li.add(new VarInsnNode(Opcodes.ILOAD, 1));
						li.add(new VarInsnNode(Opcodes.ILOAD, 2));
						li.add(new VarInsnNode(Opcodes.ALOAD, 3)); //chunk
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/CaveControl/CaveHooks", "provideFlatChunk", "(Lnet/minecraft/world/gen/ChunkProviderFlat;Lnet/minecraft/world/World;IILnet/minecraft/world/chunk/Chunk;)V", false));

						m.instructions.insertBefore(ain, li);
						break;
					}/*
					case STRONGHOLDSOLID: {
						String n1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_151549_a" : "fillWithBlocks";
						String n2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_151556_a" : "fillWithMetadataBlocks";
						String n3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_74882_a" : "fillWithRandomizedBlocks";
						String n4 = FMLForgePlugin.RUNTIME_DEOBF ? "func_151551_a" : "randomlyFillWithBlocks";
						String sig1 = "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;IIIIIILnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V";
						String sig2 = "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;IIIIIILnet/minecraft/block/Block;ILnet/minecraft/block/Block;IZ)V";
						String sig3 = "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;IIIIIIZLjava/util/Random;Lnet/minecraft/world/gen/structure/StructureComponent$BlockSelector;)V";
						String sig4 = "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;Ljava/util/Random;FIIIIIILnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V";
						/*
						ReikaASMHelper.rerouteMethod(cn, n1, "Reika/CaveControl/CaveHooks", "fillWithBlocks", sig1, true);
						ReikaASMHelper.rerouteMethod(cn, n2, "Reika/CaveControl/CaveHooks", "fillWithMetadataBlocks", sig2, true);
						ReikaASMHelper.rerouteMethod(cn, n3, "Reika/CaveControl/CaveHooks", "fillWithRandomizedBlocks", sig3, true);
						ReikaASMHelper.rerouteMethod(cn, n4, "Reika/CaveControl/CaveHooks", "randomlyFillWithBlocks", sig4, true);
					 *//*
						modifyBoolean(cn, n1, sig1);
						modifyBoolean(cn, n2, sig2);
						modifyBoolean(cn, n3, sig3);
						modifyBoolean(cn, n4, sig4);
					}*/
				}
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
				cn.accept(writer);
				return writer.toByteArray();
			}
		}

		@Override
		public byte[] transform(String className, String className2, byte[] opcodes) {
			if (!classes.isEmpty()) {
				ClassPatch p = classes.get(className);
				if (p != null) {
					ReikaASMHelper.activeMod = "CaveControl";
					ReikaASMHelper.log("Patching class "+p.deobfName);
					opcodes = p.apply(opcodes);
					classes.remove(className); //for maximizing performance
					ReikaASMHelper.activeMod = null;
				}
			}
			return opcodes;
		}

		static {
			for (int i = 0; i < ClassPatch.list.length; i++) {
				ClassPatch p = ClassPatch.list[i];
				String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
				classes.put(s, p);
			}
		}

		private static void modifyBoolean(ClassNode cn, String name, String sig) {
			MethodNode m = ReikaASMHelper.getMethodByName(cn, name, sig);
			ArrayList<String> args = ReikaASMHelper.parseMethodArguments(m);
			int idx = 1;
			for (String s : args) {
				if (s.equals("Z")) {
					break;
				}
				idx++;
			}
			InsnList li = new InsnList();
			li.add(new VarInsnNode(Opcodes.ALOAD, 0));
			li.add(new VarInsnNode(Opcodes.ILOAD, idx));
			li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/CaveControl/CaveHooks", "shouldSkipAir", "(Lnet/minecraft/world/gen/structure/StructureComponent;Z)Z", false));
			li.add(new VarInsnNode(Opcodes.ISTORE, idx));
			m.instructions.insert(li);
		}

	}

}
