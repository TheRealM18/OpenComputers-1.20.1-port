package li.cil.oc.client;

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
import li.cil.oc.common.PacketHandler;
import li.cil.oc.common.Proxy;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Client-side proxy for OpenComputers.
 */
public class ClientProxy implements Proxy {
    public ClientProxy() {
        super();
        
        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register mod event bus listeners
        modEventBus.register(this);
        modEventBus.register(GuiTypes.class);
        modEventBus.register(ModelInitialization.class);
        modEventBus.register(NetSplitterModel.class);
        modEventBus.register(Textures.class);
        
        // Register client setup handler
        modEventBus.addListener(this::onClientSetup);
        
        // Register entity renderers
        modEventBus.addListener(this::registerEntityRenderers);
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
    
    @Override
    public void init() {
        // Initialize key bindings
        KeyBindings.init();
        
        // Initialize textures
        Textures.initialize();
    }
    
    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        // Run on the main thread
        event.enqueueWork(this::init);
    }
    
    @SubscribeEvent
    public void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // Register drone renderer
        event.registerEntityRenderer(EntityTypes.DRONE.get(), DroneRenderer::new);
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
    public boolean isClient() {
        return true;
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
