package li.cil.oc;

import li.cil.oc.api.IMC;
import li.cil.oc.common.Proxy;
import li.cil.oc.common.init.Blocks;
import li.cil.oc.common.init.Items;
import li.cil.oc.integration.Mods;
import li.cil.oc.util.ThreadPoolFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mod(OpenComputers.ID)
public final class OpenComputers {
    public static final String ID = "opencomputers";
    public static final String NAME = "OpenComputers";
    
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    
    private static OpenComputers instance;
    private static final String PROTOCOL_VERSION = "1.0";
    private SimpleChannel channel;
    
    private OpenComputers() {
        // Register the setup method for mod loading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::processIMC);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        
        // Initialize proxy
        Proxy.initialize();
    }
    
    public static OpenComputers getInstance() {
        if (instance == null) {
            instance = new OpenComputers();
        }
        return instance;
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("OpenComputers initializing...");
        
        // Initialize network
        channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );
        
        // Register packets
        // TODO: Update packet registration
        // NetworkHandler.initialize(channel);
        
        // Register capabilities
        // TODO: Update capability registration
        // Capabilities.initialize();
    }
    
    private void processIMC(final InterModProcessEvent event) {
        LOGGER.info("Processing IMC messages...");
        // TODO: Update IMC processing
        // IMC.processIMC(event.getIMCStream());
    }
    
    // Getters for various components
    public SimpleChannel getNetwork() {
        return channel;
    }
    
    // Proxy instance
    private static Proxy proxy;
    
    // Proxy methods
    public boolean isClient() {
        return getProxy().isClient();
    }
    
    @OnlyIn(Dist.CLIENT)
    public Level getClientWorld() {
        return getProxy().getClientWorld();
    }
    
    @OnlyIn(Dist.CLIENT)
    public Player getClientPlayer() {
        return getProxy().getClientPlayer();
    }
    
    public static Proxy getProxy() {
        if (proxy == null) {
            proxy = DistExecutor.unsafeRunForDist(
                () -> li.cil.oc.client.ClientProxy::new,
                () -> li.cil.oc.server.ServerProxy::new
            );
        }
        return proxy;
    }
    
    // Utility methods
    public static String version() {
        return ModLoadingContext.get().getActiveContainer()
            .getModInfo()
            .getVersion()
            .toString();
    }
    
    public static String minecraftVersion() {
        return "1.20.1";
    }
    
    // Moved to instance method above
    
    public static SimpleChannel getChannel() {
        return getInstance().channel;
    }
    
    public static void setChannel(SimpleChannel channel) {
        getInstance().channel = channel;
    }
    
    public static Level getWorld(Player player) {
        return player != null ? player.level() : null;
    }
    
    private CompletableFuture<Void> initializeAsync() {
        // Initialize thread pool
        ThreadPoolFactory.create("OpenComputers");
        
        // Initialize mod integration
        Mods.init();
        
        return CompletableFuture.completedFuture(null);
    }
    
    @SubscribeEvent
    public void onRegisterBlocks(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
            event.register(ForgeRegistries.Keys.BLOCKS, helper -> {
                Blocks.init(helper);
            });
        }
    }
    
    @SubscribeEvent
    public void onRegisterItems(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            event.register(ForgeRegistries.Keys.ITEMS, helper -> {
                Items.init(helper);
            });
        }
    }
    
    @SubscribeEvent
    public void onInterModProcess(InterModProcessEvent event) {
        // IMC handling will be implemented later
        // IMC.handleMessages(event.getIMCStream());
    }
}
