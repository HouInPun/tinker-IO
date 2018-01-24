package tinker_io.main;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import tinker_io.config.TIOConfig;
import tinker_io.packet.PacketDispatcher;
import tinker_io.plugins.waila.MainWaila;
import tinker_io.proxy.CommonProxy;
import tinker_io.registry.BlockRegistry;
import tinker_io.registry.FluidRegister;
import tinker_io.registry.ItemRegistry;
import tinker_io.registry.OreCrusherBanLiatRegistry;
import tinker_io.registry.SmelteryRecipeRegistry;
import tinker_io.registry.SoundEventRegistry;
import tinker_io.registry.RecipeRegistry;
import tinker_io.registry.RegisterUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
		modid = Main.MODID,
		version = Main.VERSION,
		name = Main.Name,
		dependencies="required-after:forge@[14.23.1.2577,);"
				//"required-after:Forge@[12.18.2.2115,);"
				+ "required-after:tconstruct@[1.12.2-2.9.0.55,);"
				+ "required-after:redstoneflux@[1.12-2.0.1.2,);"
				+ "after:waila;"
				+ "after:jei@[4.8.5.138,)",
		acceptedMinecraftVersions = "[1.12.2,]")
public class Main
{
    public static final String MODID = "tinker_io";
    public static final String VERSION = "release 2.6.0";
    public static final String Name = "Tinker I/O";
    
    //public static boolean iguanas_support;
    
    //Proxy
    @SidedProxy(modId=Main.MODID, clientSide="tinker_io.proxy.ClientProxy", serverSide="tinker_io.proxy.ServerProxy")
	public static CommonProxy proxy;
    
    public static Configuration config;
    public static Logger logger = LogManager.getLogger(Main.Name);
    
    //MOD
    @Instance(Main.MODID)
    public static Main instance;
    
	
    //Creative Tabs
    public static CreativeTabs TinkerIOTabs = new CreativeTabs("TinkerIO_Tabs"){
    		public ItemStack getTabIconItem() {
    			return new ItemStack(TinkerTools.largePlate);
    		}
        };
        
    /**
     * read your config file, create Blocks, Items, etc. and register them with the GameRegistry.
     * @param event
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.preInit(event);
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	TIOConfig.syncConfig();
    	
		/*
		 * iguanas_support_cofg and Smart Output Eliminate List list will be removed to come.
		 * 
		 * Configuration config = new Configuration(event.getSuggestedConfigurationFile());
	  	
    	config.load();
    	
    	Property iguanas_support_cofg = config.get(Configuration.CATEGORY_GENERAL, "iguanas_support", true);
    	iguanas_support = config.get(Configuration.CATEGORY_GENERAL, "iguanas_support", true).getBoolean(true);
    	
    	config.save();
    	
    	if (!Loader.isModLoaded("IguanaTweaksTConstruct")) {
    		iguanas_support = false;
    	}*/
    	
		//magma heater
		FluidRegister.mainRegistry(event);
    	/*BlockRegistry.mainRegistry();
    	ItemRegistry.mainRegistry();*/
    	SoundEventRegistry.registerSounds();
    	RegisterUtil.registerAll(event);

    	PacketDispatcher.registerPackets();
    	
    	
		proxy.registerTileEntities();
		//These was moved to ClientProxy
		//proxy.registerRenderThings();
		
		MainWaila.startPlugin();
    }
    
    /**
     * build up data structures, add Crafting Recipes and register new handler
     * @param event
     */
	@Mod.EventHandler
	public static void init(FMLInitializationEvent event)
	{
		proxy.init(event);
		//Register model in Client Proxy instead of here!
    	RecipeRegistry.mainRegistry();
    	OreCrusherBanLiatRegistry.registry();
	}
    
	/**
	 * communicate with other mods and adjust your setup based on this
	 * @param event
	 */
	  @Mod.EventHandler
	  public void postInit(FMLPostInitializationEvent event)
	  {
		  proxy.postInit(event);
		  proxy.registerNetworkStuff();
		  SmelteryRecipeRegistry.registerMeltingCasting();
		  
		  ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
          for(Block block : TinkerSmeltery.validSmelteryBlocks)
          {
              builder.add(block);
          }
          builder.add(RegisterUtil.fuelInputMachine);
          TinkerSmeltery.validSmelteryBlocks = builder.build();
	  }
}
