package li.cil.oc.client.renderer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.Settings;
import li.cil.oc.client.renderer.font.TextBufferRenderData;
import li.cil.oc.util.RenderState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.TimeUnit;

/**
 * Handles caching of rendered text buffers for improved performance.
 */
@Mod.EventBusSubscriber
public class TextBufferRenderCache {
    private static final FontRenderer RENDERER = createRenderer();
    
    private static FontRenderer createRenderer() {
        return Settings.get().fontRenderer().equals("texture") ? 
            new font.StaticFontRenderer() : 
            new font.DynamicFontRenderer();
    }
    
    private static final LoadingCache<TextBufferRenderData, RenderCache> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(2, TimeUnit.SECONDS)
        .build(new CacheLoader<>() {
            @Override
            public RenderCache load(TextBufferRenderData key) {
                return new RenderCache();
            }
        });
    
    /**
     * Renders a text buffer using the cache.
     * 
     * @param stack The pose stack for rendering
     * @param buffer The buffer to render
     */
    public static void render(PoseStack stack, TextBufferRenderData buffer) {
        RenderState.checkError(TextBufferRenderCache.class.getName() + ".render: entering (aka: wasntme)");
        
        try {
            RenderCache cached = CACHE.get(buffer);
            if (buffer.isDirty() || cached.isEmpty()) {
                // Generate characters for all lines
                for (String line : buffer.getData().buffer) {
                    RENDERER.generateChars(line);
                }
                
                buffer.setDirty(false);
                
                // Update the cache
                cached.clear();
                RENDERER.drawBuffer(new PoseStack(), cached, buffer.getData(), 
                    buffer.getViewport().getFirst(), buffer.getViewport().getSecond());
                cached.finish();
                
                RenderState.checkError(TextBufferRenderCache.class.getName() + ".render: compiled buffer");
            }
            
            // Render the cached buffer
            cached.render(stack);
            
        } catch (Exception e) {
            // Handle any errors during cache access
            e.printStackTrace();
        }
        
        RenderState.checkError(TextBufferRenderCache.class.getName() + ".render: leaving");
    }
    
    /**
     * Cleans up the cache periodically.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CACHE.cleanUp();
        }
    }
    
    /**
     * Interface for font renderers used by the cache.
     */
    public interface FontRenderer {
        void generateChars(String text);
        void drawBuffer(PoseStack stack, RenderCache cache, Object data, int viewportWidth, int viewportHeight);
    }
    
    /**
     * Interface for render caches used by the text buffer renderer.
     */
    public interface RenderCache {
        boolean isEmpty();
        void clear();
        void finish();
        void render(PoseStack stack);
    }
}
