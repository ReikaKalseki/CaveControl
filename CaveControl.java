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

import java.io.File;
import java.net.URL;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.MinecraftForge;
import Reika.CaveControl.Generators.ControllableCaveGen;
import Reika.CaveControl.Generators.ControllableMineshaftGen;
import Reika.CaveControl.Generators.ControllableRavineGen;
import Reika.CaveControl.Generators.ControllableStrongholdGen;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = "CaveControl", name="CaveControl", version = "v@MAJOR_VERSION@@MINOR_VERSION@", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI", acceptableRemoteVersions="*")

public class CaveControl extends DragonAPIMod {

	@Instance("CaveControl")
	public static CaveControl instance = new CaveControl();

	public static final ControlledConfig config = new ControlledConfig(instance, CaveOptions.optionList, null);

	public static ModLogger logger;

	public static ControllableCaveGen caveGen;
	public static ControllableMineshaftGen mineGen;
	public static ControllableStrongholdGen strongholdGen;
	public static ControllableRavineGen ravineGen;

	//@SidedProxy(clientSide="Reika.CaveCraft.CaveClient", serverSide="Reika.CaveCraft.CaveCommon")
	//public static CaveCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		logger = new ModLogger(instance, false);
		if (DragonOptions.FILELOG.getState())
			logger.setOutput("**_Loading_Log.log");

		MinecraftForge.EVENT_BUS.register(CaveEvents.instance);
		MinecraftForge.TERRAIN_GEN_BUS.register(CaveEvents.instance);

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		caveGen = new ControllableCaveGen();
		mineGen = new ControllableMineshaftGen();
		strongholdGen = new ControllableStrongholdGen();
		ravineGen = new ControllableRavineGen();
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);
		CaveLoader.instance.generateGlobalFile();
		CaveLoader.instance.loadConfigs();
		this.finishTiming();
	}

	@Override
	public String getDisplayName() {
		return "CaveControl";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return null;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@Override
	public File getConfigFolder() {
		return config.getConfigFolder();
	}

	@Override
	protected Class<? extends IClassTransformer> getASMClass() {
		return CaveASMHandler.CaveTransformer.class;
	}

}
