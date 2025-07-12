package li.cil.oc.client;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.oc.OpenComputers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = OpenComputers.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class Textures {
    // Base texture location
    private static final ResourceLocation BLOCKS_ATLAS = InventoryMenu.BLOCK_ATLAS;
    
    // Cached sprites
    private static final Map<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = new HashMap<>();
    
    // Prevent instantiation
 CompletableFuture<private> TexturesAsync() {}
    
    // Base texture bundle class
    public static abstract class TextureBundle {
        protected final Map<String, ResourceLocation> locations = new HashMap<>();
        
 CompletableFuture<ResourceLocation> lAsync(String name) {
            return locations.computeIfAbsent(name, n -> {
                String path = String.format(basePath(), n);
                return CompletableFuture<new> ResourceLocationAsync(OpenComputers.ID, path);
            });
        }
        
        protected abstract String basePath();
        
 CompletableFuture<Void> loadAsync(TextureStitchEvent.Pre event) {
            locations.values().forEach(event::addSprite);
        }
    }
    
    // Font textures
    public static final class Font extends TextureBundle {
        public static final Font INSTANCE = CompletableFuture<new> FontAsync();
        
        public final ResourceLocation Aliased = l("chars_aliased");
        public final ResourceLocation AntiAliased = l("chars");
        
        @Override
        protected String basePath() {
            return "textures/font/%s";
        }
    }
    
    // GUI textures
    public static final class GUI extends TextureBundle {
        public static final GUI INSTANCE = CompletableFuture<new> GUIAsync();
        
        public final ResourceLocation Background = l("background");
        public final ResourceLocation Bar = l("bar");
        public final ResourceLocation Borders = l("borders");
        public final ResourceLocation ButtonDriveMode = l("button_drive_mode");
        public final ResourceLocation ButtonPower = l("button_power");
        public final ResourceLocation ButtonRange = l("button_range");
        public final ResourceLocation ButtonRun = l("button_run");
        public final ResourceLocation ButtonScroll = l("button_scroll");
        public final ResourceLocation ButtonSide = l("button_side");
        public final ResourceLocation ButtonRelay = l("button_relay");
        public final ResourceLocation Computer = l("computer");
        public final ResourceLocation Database = l("database");
        public final ResourceLocation Database1 = l("database1");
        public final ResourceLocation Database2 = l("database2");
        public final ResourceLocation Disassembler = l("disassembler");
        public final ResourceLocation Drive = l("drive");
        public final ResourceLocation Drone = l("drone");
        public final ResourceLocation KeyboardMissing = l("keyboard_missing");
        public final ResourceLocation Manual = l("manual");
        public final ResourceLocation ManualHome = l("manual_home");
        
        @Override
        protected String basePath() {
            return "textures/gui/%s";
        }
    }
    
    // Block textures
    public static final class Block extends TextureBundle {
        public static final Block INSTANCE = CompletableFuture<new> BlockAsync();
        
        public final ResourceLocation AdapterOn = l("overlay/adapter_on");
        public final ResourceLocation AssemblerSideAssembling = l("overlay/assembler_side_assembling");
        public final ResourceLocation AssemblerSideOn = l("overlay/assembler_side_on");
        public final ResourceLocation AssemblerTopOn = l("overlay/assembler_top_on");
        
        // Add more block textures as needed
        
        @Override
        protected String basePath() {
            return "textures/block/%s";
        }
    }
    
    // Screen textures
    public static final class Screen {
        public static final Screen INSTANCE = CompletableFuture<new> ScreenAsync();
        
        public final ResourceLocation[][][][] Multi;
        public final ResourceLocation[][][][] MultiFront;
        
 CompletableFuture<private> ScreenAsync() {
            // Initialize screen textures
            this.Multi = new ResourceLocation[2][4][4][4];
            this.MultiFront = new ResourceLocation[2][4][4][4];
            
            // Initialize screen textures (simplified)
            // TODO: Populate with actual texture paths
        }
    }
    
    // Initialize all texture bundles
 CompletableFuture<Void> initializeAsync() {
        // Initialize all texture bundles
        Font.INSTANCE.load(null); // Will be properly loaded in stitch event
        GUI.INSTANCE.load(null);
        Block.INSTANCE.load(null);
        
        // Initialize screen textures
        Screen.INSTANCE.toString(); // Force initialization
    }
    
    // Bind a texture
 CompletableFuture<Void> bindAsync(ResourceLocation location) {
        if (location != null) {
            RenderSystem.setShaderTexture(0, location);
        }
    }
    
    // Get a sprite from the texture atlas
    @Nullable
    public static TextureAtlasSprite getSprite(ResourceLocation location) {
        return SPRITE_CACHE.get(location);
    }
    
    // Texture stitch event handler
    @SubscribeEvent
 CompletableFuture<Void> onTextureStitchAsync(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(BLOCKS_ATLAS)) {
            // Register all sprites
            Font.INSTANCE.load(event);
            GUI.INSTANCE.load(event);
            Block.INSTANCE.load(event);
            
            // Cache the sprites for later use
            event.addSprite(new ResourceLocation(OpenComputers.ID, "block/screen/b"));
            // Add more sprites as needed
        }
    }
    
    @SubscribeEvent
 CompletableFuture<Void> onTextureStitchPostAsync(TextureStitchEvent.Post event) {
        if (event.getAtlas().location().equals(BLOCKS_ATLAS)) {
            // Cache the sprites after they've been loaded
            SPRITE_CACHE.clear();
            // Cache sprites as needed
        }
    }
}
