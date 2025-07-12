package li.cil.oc.client.gui.traits;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Trait for GUI screens that provide a windowed interface with a background.
 */
@OnlyIn(Dist.CLIENT)
public interface Window {
    /**
     * @return The X position of the window
     */
    int getLeftPos();
    
    /**
     * Set the X position of the window.
     */
    void setLeftPos(int leftPos);
    
    /**
     * @return The Y position of the window
     */
    int getTopPos();
    
    /**
     * Set the Y position of the window.
     */
    void setTopPos(int topPos);
    
    /**
     * @return The width of the window image
     */
    int getImageWidth();
    
    /**
     * Set the width of the window image.
     */
    void setImageWidth(int imageWidth);
    
    /**
     * @return The height of the window image
     */
    int getImageHeight();
    
    /**
     * Set the height of the window image.
     */
    void setImageHeight(int imageHeight);
    
    /**
     * @return The width of the window
     */
    default int getWindowWidth() {
        return 176;
    }
    
    /**
     * @return The height of the window
     */
    default int getWindowHeight() {
        return 166;
    }
    
    /**
     * @return The resource location of the background image
     */
    ResourceLocation getBackgroundImage();
    
    /**
     * Initialize the window position and size.
     * 
     * @param screen The screen implementing this trait
     */
    default void initWindow(Screen screen) {
        setImageWidth(getWindowWidth());
        setImageHeight(getWindowHeight());
        setLeftPos((screen.width - getImageWidth()) / 2);
        setTopPos((screen.height - getImageHeight()) / 2);
    }
    
    /**
     * Render the window background.
     * 
     * @param screen The screen implementing this trait
     * @param stack The pose stack for rendering
     * @param mouseX The X position of the mouse
     * @param mouseY The Y position of the mouse
     * @param partialTicks Partial ticks for animation
     */
    default void renderWindow(Screen screen, PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, getBackgroundImage());
        // Texture width and height are intentionally backwards.
        GuiComponent.blit(
            stack,
            getLeftPos(),
            getTopPos(),
            screen.getBlitOffset(),
            0,
            0,
            getImageWidth(),
            getImageHeight(),
            getWindowHeight(),
            getWindowWidth()
        );
    }
    
    /**
     * Default implementation of isPauseScreen.
     * 
     * @return Always returns false
     */
    default boolean isPauseScreen() {
        return false;
    }
}
