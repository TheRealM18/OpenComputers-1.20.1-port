package li.cil.oc.client.gui.traits;

import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.util.RenderState;
import net.minecraft.client.gui.screens.Screen;

/**
 * Interface for GUI screens that display text buffers.
 */
public interface DisplayBuffer {
    /**
     * @return The X position of the buffer in screen coordinates
     */
    int getBufferX();

    /**
     * @return The Y position of the buffer in screen coordinates
     */
    int getBufferY();

    /**
     * @return The number of text columns in the buffer
     */
    int getBufferColumns();

    /**
     * @return The number of text rows in the buffer
     */
    int getBufferRows();

    /**
     * Renders the buffer layer with proper scaling and transformations.
     * 
     * @param stack The pose stack for rendering transformations
     */
    default void drawBufferLayer(PoseStack stack) {
        double scale = changeSize(getBufferColumns(), getBufferRows());
        
        RenderState.checkError(getClass().getName() + ".drawBufferLayer: entering (aka: wasntme)");
        
        stack.pushPose();
        drawBuffer(stack);
        stack.popPose();
        
        RenderState.checkError(getClass().getName() + ".drawBufferLayer: buffer layer");
    }
    
    /**
     * Draws the buffer content.
     * 
     * @param stack The pose stack for rendering transformations
     */
    void drawBuffer(PoseStack stack);
    
    /**
     * Called when the buffer size changes.
     * 
     * @param width The new width in characters
     * @param height The new height in characters
     * @return The scale factor to apply to the buffer
     */
    double changeSize(double width, double height);
}
