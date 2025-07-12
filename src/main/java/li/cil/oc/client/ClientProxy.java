package li.cil.oc.client;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.OpenComputers;
import li.cil.oc.api.API;
import li.cil.oc.client.gui.GuiTypes;
import li.cil.oc.client.renderer.HighlightRenderer;
import li.cil.oc.client.renderer.MFUTargetRenderer;
import li.cil.oc.client.renderer.PetRenderer;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import li.cil.oc.client.renderer.WirelessNetworkDebugRenderer;
import li.cil.oc.client.renderer.block.ModelInitialization;
import li.cil.oc.client.renderer.block.NetSplitterModel;
import li.cil.oc.client.renderer.entity.DroneRenderer;
import li.cil.oc.common.CommonProxy;
import li.cil.oc.common.PacketHandler;
import li.cil.oc.common.entity.Drone;
import li.cil.oc.common.entity.EntityTypes;
import li.cil.oc.common.event.NanomachinesHandler;
import li.cil.oc.common.event.RackMountableRenderHandler;
import li.cil.oc.util.Audio;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {
 CompletableFuture<public> ClientProxyAsync() {
        super();
        
        // Register mod event bus listeners
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(GuiTypes.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(ModelInitialization.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(NetSplitterModel.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(Textures.class);
    }

    @Override
    public void preInit() {
        super.preInit();
        
        // Set up manual
        API.manual = Manual.INSTANCE;
        
        // Register renderers
        MinecraftForge.EVENT_BUS.register(HighlightRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MFUTargetRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PetRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TextBufferRenderCache.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WirelessNetworkDebugRenderer.INSTANCE);
        
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new NanomachinesHandler.Client());
        MinecraftForge.EVENT_BUS.register(new RackMountableRenderHandler());
        
        // Initialize audio system
        Audio.init();
    }
    
    @SubscribeEvent
 CompletableFuture<Void> onClientSetupAsync(FMLClientSetupEvent event) {
        // Register entity renderers
        event.enqueueWork(() -> {
            // Register drone renderer
            net.minecraft.client.renderer.entity.EntityRenderers.register(
                EntityTypes.DRONE.get(),
                DroneRenderer::new
            );
            
            // Initialize key bindings
            KeyBindings.init();
            
            // Initialize textures
            Textures.initialize();
        });
    }
    
    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }
    
    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    
    @Override
    public boolean isClientThread() {
        return Minecraft.getInstance().isSameThread();
    }
    
    @Override
    public void handlePacket(PacketHandler packet, Level world, Player player) {
        // Handle client-side packets
        if (world == null) {
            world = getClientWorld();
        }
        if (player == null) {
            player = getClientPlayer();
        }
        if (world != null && player != null) {
            try {
                packet.handleClientSide(player, world);
            } catch (Exception e) {
                OpenComputers.log.error("Error handling client packet: " + packet, e);
            }
        }
    }
    
    @Override
    public void openScreen(Player player, String name, Object... args) {
        if (player instanceof net.minecraft.client.player.LocalPlayer) {
            // Handle GUI opening on client side
            Screen screen = GuiTypes.createScreen(name, player, args);
            if (screen != null) {
                Minecraft.getInstance().setScreen(screen);
            }
        }
    }
}
