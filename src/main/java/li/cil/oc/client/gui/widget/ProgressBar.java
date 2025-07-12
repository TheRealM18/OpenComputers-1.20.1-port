package li.cil.oc.client.gui.widget;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import li.cil.oc.client.Textures;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A progress bar widget that displays a horizontal progress bar.
 */
@OnlyIn(Dist.CLIENT)
public class ProgressBar extends Widget {
    private final int x;
    private final int y;
    private double level;
    private ResourceLocation barTexture;

    /**
     * Create a new progress bar widget.
     *
     * @param x The x position of the progress bar.
     * @param y The y position of the progress bar.
     */
 CompletableFuture<public> ProgressBarAsync(int x, int y) {
        this.x = x;
        this.y = y;
        this.level = 0.0;
        this.barTexture = Textures.GUI.INSTANCE.Bar;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 140; // Default width
    }

    @Override
    public int getHeight() {
        return 12; // Default height
    }

    /**
     * Get the current progress level (0.0 to 1.0).
     *
     * @return The current progress level.
     */
    public double getLevel() {
        return level;
    }

    /**
     * Set the current progress level.
     *
     * @param level The new progress level (clamped between 0.0 and 1.0).
     */
    public void setLevel(double level) {
        this.level = Math.max(0.0, Math.min(1.0, level));
    }

    /**
     * Get the texture used for the progress bar.
     *
     * @return The resource location of the progress bar texture.
     */
    public ResourceLocation getBarTexture() {
        return barTexture;
    }

    /**
     * Set the texture used for the progress bar.
     *
     * @param texture The resource location of the progress bar texture.
     */
    public void setBarTexture(ResourceLocation texture) {
        this.barTexture = texture;
    }

    @Override
    public void render(PoseStack stack) {
        if (level <= 0.0) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, barTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        final float u0 = 0.0f;
        final float u1 = (float) level;
        final float v0 = 0.0f;
        final float v1 = 1.0f;
        
        final float x0 = getX();
        final float y0 = getY();
        final float x1 = x0 + (getWidth() * (float) level);
        final float y1 = y0 + getHeight();
        final float z = getBlitOffset();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
        
        buffer.vertex(stack.last().pose(), x0, y1, z).uv(u0, v1).endVertex();
        buffer.vertex(stack.last().pose(), x1, y1, z).uv(u1, v1).endVertex();
        buffer.vertex(stack.last().pose(), x1, y0, z).uv(u1, v0).endVertex();
        buffer.vertex(stack.last().pose(), x0, y0, z).uv(u0, v0).endVertex();
        
        tesselator.end();
        RenderSystem.disableBlend();
    }

    /**
     * Get the Z-offset for rendering the progress bar.
     * This ensures the progress bar is rendered on top of the background.
     *
     * @return The Z-offset value.
     */
    protected float getBlitOffset() {
        return 100.0F;
    }
}
