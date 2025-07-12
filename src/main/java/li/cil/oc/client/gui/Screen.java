package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import li.cil.oc.client.renderer.gui.BufferRenderer;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Screen for displaying and interacting with text buffers.
 */
public class Screen extends Screen implements traits.InputBuffer, ContainerEventHandler {
    protected final TextBuffer buffer;
    protected final boolean hasMouse;
    protected final BooleanSupplier hasKeyboardCallback;
    protected final BooleanSupplier hasPower;
    
    protected final int bufferMargin = BufferRenderer.MARGIN + BufferRenderer.INNER_MARGIN;
    protected boolean didClick = false;
    protected int x, y;
    protected int innerWidth, innerHeight;
    protected double mx = -1, my = -1;
    protected GuiEventListener focused;
    protected boolean isDragging;
    
 CompletableFuture<public> ScreenAsync(TextBuffer buffer, boolean hasMouse, BooleanSupplier hasKeyboardCallback, BooleanSupplier hasPower) {
        super(TextComponent.EMPTY);
        this.buffer = Objects.requireNonNull(buffer);
        this.hasMouse = hasMouse;
        this.hasKeyboardCallback = hasKeyboardCallback;
        this.hasPower = hasPower;
    }
    
    @Override
    public boolean hasKeyboard() {
        return hasKeyboardCallback.getAsBoolean();
    }
    
    @Override
    public int getBufferX() {
        return 8 + x;
    }
    
    @Override
    public int getBufferY() {
        return 8 + y;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (hasMouse) {
            Optional<double[]> coords = toBufferCoordinates(mouseX, mouseY);
            if (coords.isPresent()) {
                double[] pos = coords.get();
                buffer.mouseScroll((int)pos[0], (int)pos[1], (int)Math.signum(scroll), null);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasMouse && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            clickOrDrag(mouseX, mouseY, button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (hasMouse) {
            didClick = false;
            Optional<double[]> coords = toBufferCoordinates(mouseX, mouseY);
            if (coords.isPresent()) {
                double[] pos = coords.get();
                buffer.mouseUp((int)pos[0], (int)pos[1], button, null);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (hasMouse && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            clickOrDrag(mouseX, mouseY, button);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.mx = mouseX;
        this.my = mouseY;
        
        // Update buffer dimensions
        innerWidth = width - 16;
        innerHeight = height - 16;
        x = (width - innerWidth) / 2;
        y = (height - innerHeight) / 2;
        
        // Render background
        renderBackground(stack);
        
        // Setup scissor for buffer rendering
        RenderSystem.enableScissor(x, minecraft.getWindow().getGuiScaledHeight() - (y + innerHeight), innerWidth, innerHeight);
        
        // Render buffer content
        if (hasPower.getAsBoolean()) {
            TextBufferRenderCache.render(buffer);
        }
        
        // Render mouse cursor if needed
        if (hasMouse) {
            Optional<double[]> coords = toBufferCoordinates(mouseX, mouseY);
            if (coords.isPresent()) {
                double[] pos = coords.get();
                buffer.setCursorPos((int)pos[0], (int)pos[1]);
            }
        }
        
        // Reset scissor
        RenderSystem.disableScissor();
        
        // Render tooltips if needed
        super.render(stack, mouseX, mouseY, partialTicks);
    }
    
 CompletableFuture<Optional<double[]>> toBufferCoordinatesAsync(double mouseX, double mouseY) {
        double bx = (mouseX - x - bufferMargin) / BufferRenderer.charRenderWidth();
        double by = (mouseY - y - bufferMargin) / BufferRenderer.charRenderHeight();
        
        if (bx >= 0 && by >= 0 && bx < buffer.getViewportWidth() && by < buffer.getViewportHeight()) {
            return Optional.of(new double[] { bx, by });
        }
        return Optional.empty();
    }
    
 CompletableFuture<Void> clickOrDragAsync(double mouseX, double mouseY, int button) {
        Optional<double[]> coords = toBufferCoordinates(mouseX, mouseY);
        if (coords.isPresent()) {
            double[] pos = coords.get();
            if (didClick) {
                buffer.mouseDrag((int)pos[0], (int)pos[1], button, null);
            } else {
                buffer.mouseDown((int)pos[0], (int)pos[1], button, null);
                didClick = true;
            }
        }
    }
    
    // ContainerEventHandler implementation
    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
    
    @Override
    public boolean isDragging() {
        return isDragging;
    }
    
    @Override
    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }
    
    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return focused;
    }
    
    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        this.focused = listener;
    }
}
