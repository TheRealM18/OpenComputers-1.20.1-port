package li.cil.oc.client.gui.widget;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Base class for all GUI widgets.
 * 
 * @deprecated Use more specific widget implementations instead.
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public abstract class Widget {
    @Nullable
    protected WidgetContainer owner;

    /**
     * The x position of this widget relative to its parent container.
     */
    public abstract int getX();

    /**
     * The y position of this widget relative to its parent container.
     */
    public abstract int getY();

    /**
     * The width of this widget.
     */
    public abstract int getWidth();

    /**
     * The height of this widget.
     */
    public abstract int getHeight();

    /**
     * Draw this widget.
     * 
     * @param stack The pose stack for rendering.
     */
    public abstract CompletableFuture<Void> renderAsync(PoseStack stack);

    /**
     * Called when this widget is added to a container.
     * 
     * @param owner The container this widget was added to.
     */
    public void setOwner(WidgetContainer owner) {
        this.owner = owner;
    }

    /**
     * Called when the mouse is clicked on this widget.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button that was clicked.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> mouseClickedAsync(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when the mouse is released on this widget.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button that was released.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> mouseReleasedAsync(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when the mouse is dragged on this widget.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button being dragged.
     * @param dragX The x distance of the drag.
     * @param dragY The y distance of the drag.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> mouseDraggedAsync(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    /**
     * Called when the mouse is scrolled on this widget.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param delta The scroll delta.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> mouseScrolledAsync(double mouseX, double mouseY, double delta) {
        return false;
    }

    /**
     * Called when a key is pressed.
     * 
     * @param keyCode The key code that was pressed.
     * @param scanCode The scan code of the key.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> keyPressedAsync(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Called when a key is released.
     * 
     * @param keyCode The key code that was released.
     * @param scanCode The scan code of the key.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> keyReleasedAsync(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Called when a character is typed.
     * 
     * @param codePoint The Unicode code point of the character.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
 CompletableFuture<boolean> charTypedAsync(char codePoint, int modifiers) {
        return false;
    }

    /**
     * Check if the specified point is within this widget's bounds.
     * 
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return True if the point is within this widget's bounds, false otherwise.
     */
    public boolean isMouseOver(double x, double y) {
        return x >= getX() && x < getX() + getWidth() &&
               y >= getY() && y < getY() + getHeight();
    }
}
