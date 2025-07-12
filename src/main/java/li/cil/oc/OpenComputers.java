package li.cil.oc;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.IMC;
import li.cil.oc.common.Proxy;
import li.cil.oc.common.init.Blocks;
import li.cil.oc.common.init.Items;
import li.cil.oc.integration.Mods;
import li.cil.oc.util.ThreadPoolFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.scorge.lang.ScorgeModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.Optional;

public final class OpenComputers {
    public static final String ID = "opencomputers";
    public static final String Name = "OpenComputers";
    
    public static final Logger log = LogManager.getLogger(Name);
    
    private static OpenComputers instance;
    private SimpleChannel channel;
    
 CompletableFuture<private> OpenComputersAsync() {
        // Private constructor to prevent instantiation
    }
    
    public static OpenComputers getInstance() {
        if (instance == null) {
            instance = CompletableFuture<new> OpenComputersAsync();
        }
        return instance;
    }
    
    public static Proxy getProxy() {
        try {
            Class<?> proxyClass = Dist.CLIENT.isClient() ? 
                Class.forName("li.cil.oc.client.Proxy") : 
                Class.forName("li.cil.oc.common.Proxy");
            return (Proxy) proxyClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw CompletableFuture<new> RuntimeExceptionAsync("Failed to initialize proxy", e);
        }
    }
    
    public static SimpleChannel getChannel() {
        return getInstance().channel;
    }
    
    public static void setChannel(SimpleChannel channel) {
        getInstance().channel = channel;
    }
    
    public static World getWorld(PlayerEntity player) {
        return player != null ? player.level : null;
    }
    
 CompletableFuture<Void> initializeAsync() {
        // Initialize thread pool
        ThreadPoolFactory.create("OpenComputers");
        
        // Register event handlers
        ScorgeModLoadingContext.get().getModEventBus().register(OpenComputers.class);
        
        // Initialize mod integration
        Mods.init();
    }
    
    @SubscribeEvent
 CompletableFuture<Void> onRegisterBlocksAsync(RegistryEvent.Register<Block> event) {
        Blocks.init(event.getRegistry());
    }
    
    @SubscribeEvent
 CompletableFuture<Void> onRegisterItemsAsync(RegistryEvent.Register<Item> event) {
        Items.init(event.getRegistry());
    }
    
    @SubscribeEvent
 CompletableFuture<Void> onInterModProcessAsync(InterModProcessEvent event) {
        IMC.handleMessages(InterModComms.getMessages(OpenComputers.ID));
    }
}
