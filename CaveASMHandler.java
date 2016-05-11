/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.classloading.FMLForgePlugin;

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

import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
@MCVersion("1.7.10")
public class CaveASMHandler implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{LegacyTransformer.class.getName()};
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

	public static class LegacyTransformer implements IClassTransformer {

		private static final HashMap<String, ClassPatch> classes = new HashMap();

		private static enum ClassPatch {
			DUNGEONRATE("net.minecraft.world.gen.ChunkProviderGenerate", "aqz"),
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
								li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/CaveControl/CaveASMHandler", "getDungeonGenAttempts", "(Lnet/minecraft/world/World;II)I", false));
								m.instructions.insert(ain, li);
								m.instructions.remove(ain);
								break;
							}
						}
						break;
					}
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

	}

	public static int getDungeonGenAttempts(World world, int x, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x << 4, z << 4);
		return (int)(8*ControlOptions.DUNGEONRATE.getValue(BiomeTypeList.getEntry(biome)));
	}
	/*
	class test extends ChunkProviderGenerate {

		private World worldObj;
		private Random rand;

		public test(World p_i2006_1_, long p_i2006_2_, boolean p_i2006_4_) {
			super(p_i2006_1_, p_i2006_2_, p_i2006_4_);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {

			int k1 = 0;
			boolean doGen = true;
			for (k1 = 0; doGen && k1 < CaveASMHandler.getDungeonGenAttempts(worldObj, p_73153_2_, p_73153_3_); ++k1)
			{
				int l1 = 0 + rand.nextInt(16) + 8;
				int i2 = rand.nextInt(256);
				int j2 = 0 + rand.nextInt(16) + 8;
				(new WorldGenDungeons()).generate(worldObj, rand, l1, i2, j2);
			}
		}

	}*/

}
