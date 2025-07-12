package li.cil.oc.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for containers that can hold and manage widgets.
 * 
 * @deprecated Use more specific container implementations instead.
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public interface WidgetContainer {
    /**
     * Add a custom widget to this container.
     * 
     * @param widget The widget to add.
     * @param <T> The type of the widget.
     * @return The added widget for method chaining.
     */
    default <T extends Widget> T addCustomWidget(T widget) {
        getWidgets().add(widget);
        widget.setOwner(this);
        return widget;
    }

    /**
     * Remove a widget from this container.
     * 
     * @param widget The widget to remove.
     */
    default void removeWidget(Widget widget) {
        getWidgets().remove(widget);
    }

    /**
     * Clear all widgets from this container.
     */
    default void clearWidgets() {
        getWidgets().clear();
    }

    /**
     * Get the x position of this container's window.
     * 
     * @return The x position in screen coordinates.
     */
    default int windowX() {
        return 0;
    }

    /**
     * Get the y position of this container's window.
     * 
     * @return The y position in screen coordinates.
     */
    default int windowY() {
        return 0;
    }

    /**
     * Get the z position of this container's window.
     * 
     * @return The z position for rendering.
     */
    default float windowZ() {
        return 0;
    }

    /**
     * Draw all widgets in this container.
     * 
     * @param stack The pose stack for rendering.
     */
    default void drawWidgets(@Nonnull PoseStack stack) {
        for (Widget widget : getWidgets()) {
            widget.render(stack);
        }
    }

    /**
     * Handle a mouse click event.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button that was clicked.
     * @return True if the event was handled, false otherwise.
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Process in reverse order to handle top-most widgets first
        for (int i = getWidgets().size() - 1; i >= 0; i--) {
            Widget widget = getWidgets().get(i);
            if (widget.isMouseOver(mouseX - windowX(), mouseY - windowY())) {
                if (widget.mouseClicked(mouseX - windowX(), mouseY - windowY(), button)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handle a mouse release event.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button that was released.
     * @return True if the event was handled, false otherwise.
     */
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Process in reverse order to handle top-most widgets first
        for (int i = getWidgets().size() - 1; i >= 0; i--) {
            Widget widget = getWidgets().get(i);
            if (widget.mouseReleased(mouseX - windowX(), mouseY - windowY(), button)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a mouse drag event.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The mouse button being dragged.
     * @param dragX The x distance of the drag.
     * @param dragY The y distance of the drag.
     * @return True if the event was handled, false otherwise.
     */
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Process in reverse order to handle top-most widgets first
        for (int i = getWidgets().size() - 1; i >= 0; i--) {
            Widget widget = getWidgets().get(i);
            if (widget.mouseDragged(mouseX - windowX(), mouseY - windowY(), button, dragX, dragY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a mouse scroll event.
     * 
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param delta The scroll delta.
     * @return True if the event was handled, false otherwise.
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Process in reverse order to handle top-most widgets first
        for (int i = getWidgets().size() - 1; i >= 0; i--) {
            Widget widget = getWidgets().get(i);
            if (widget.isMouseOver(mouseX - windowX(), mouseY - windowY())) {
                if (widget.mouseScrolled(mouseX - windowX(), mouseY - windowY(), delta)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handle a key press event.
     * 
     * @param keyCode The key code that was pressed.
     * @param scanCode The scan code of the key.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Widget widget : getWidgets()) {
            if (widget.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a key release event.
     * 
     * @param keyCode The key code that was released.
     * @param scanCode The scan code of the key.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Widget widget : getWidgets()) {
            if (widget.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle a character typed event.
     * 
     * @param codePoint The Unicode code point of the character.
     * @param modifiers The keyboard modifiers.
     * @return True if the event was handled, false otherwise.
     */
    default boolean charTyped(char codePoint, int modifiers) {
        for (Widget widget : getWidgets()) {
            if (widget.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the list of widgets in this container.
     * 
     * @return The list of widgets.
     */
    List<Widget> getWidgets();
}
