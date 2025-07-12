package li.cil.oc.client.gui.traits;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.client.KeyBindings;
import li.cil.oc.client.Textures;
import li.cil.oc.integration.util.ItemSearch;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Trait for GUI screens that handle text input for text buffers.
 */
@OnlyIn(Dist.CLIENT)
public interface InputBuffer extends DisplayBuffer {
    /**
     * @return The text buffer this input is for
     */
    TextBuffer getBuffer();
    
    /**
     * @return Whether this input buffer currently has keyboard focus
     */
    boolean hasKeyboard();
    
    /**
     * Internal method to get the Minecraft instance.
     */
    default Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
    
    // Implementation of DisplayBuffer methods
    @Override
    default int getBufferColumns() {
        TextBuffer buffer = getBuffer();
        return buffer == null ? 0 : buffer.getViewportWidth();
    }
    
    @Override
    default int getBufferRows() {
        TextBuffer buffer = getBuffer();
        return buffer == null ? 0 : buffer.getViewportHeight();
    }
    
    // Key handling state
    class InputState {
        public final Map<Integer, Character> pressedKeys = new HashMap<>();
        public long showKeyboardMissing = 0;
        public boolean hasQueuedKey = false;
        public int queuedKey = 0;
        public char queuedChar = '\u0000';
        public char highSurrogate = '\u0000';
    }
    
    default InputState getInputState() {
        // This should be overridden by implementing classes to provide instance state
        throw new UnsupportedOperationException("InputState not implemented");
    }
    
    /**
     * Queue a key press for processing.
     */
    default void pushQueuedKey(int keyCode) {
        InputState state = getInputState();
        flushQueuedKey();
        state.hasQueuedKey = true;
        state.queuedKey = keyCode;
        state.queuedChar = GLFWTranslator.keyToChar(keyCode);
    }
    
    /**
     * Queue a character for processing.
     */
    default void pushQueuedChar(char ch) {
        InputState state = getInputState();
        if (state.hasQueuedKey) {
            if (!Character.isSurrogate(ch)) {
                state.queuedChar = ch;
            }
            // Flush either way as the next code point will be unrelated
            flushQueuedKey();
        } else if (state.highSurrogate != '\u0000') {
            if (Character.isSurrogatePair(state.highSurrogate, ch)) {
                int codePoint = Character.toCodePoint(state.highSurrogate, ch);
                getBuffer().keyDown('\u0000', codePoint, null);
            }
            state.highSurrogate = '\u0000';
        } else if (Character.isHighSurrogate(ch)) {
            state.highSurrogate = ch;
        } else if (!Character.isLowSurrogate(ch)) {
            getBuffer().keyDown('\u0000', ch, null);
        }
    }
    
    /**
     * Process any queued key press.
     */
    default void flushQueuedKey() {
        InputState state = getInputState();
        if (state.hasQueuedKey) {
            getBuffer().keyDown(state.queuedChar, state.queuedKey, null);
            state.hasQueuedKey = false;
        }
    }
    
    /**
     * Handle a key press event.
     */
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this instanceof Screen screen && !screen.getFocused().equals(screen)) {
            return false;
        }
        
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return false;
        }
        
        if (hasKeyboard()) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE ||
                keyCode == GLFW.GLFW_KEY_HOME || keyCode == GLFW.GLFW_KEY_END ||
                keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT ||
                keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN ||
                keyCode == GLFW.GLFW_KEY_PAGE_UP || keyCode == GLFW.GLFW_KEY_PAGE_DOWN ||
                keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_TAB) {
                
                pushQueuedKey(keyCode);
                return true;
            }
            
            // Handle copy/paste
            boolean ctrlDown = Screen.hasControlDown();
            if (ctrlDown && keyCode == GLFW.GLFW_KEY_C) {
                getMinecraft().keyboardHandler.setClipboard(getBuffer().getValue("clipboard"));
                return true;
            } else if (ctrlDown && keyCode == GLFW.GLFW_KEY_V) {
                String text = getMinecraft().keyboardHandler.getClipboard();
                if (text != null && !text.isEmpty()) {
                    for (int i = 0; i < text.length(); i++) {
                        char ch = text.charAt(i);
                        if (ch >= ' ') { // Basic filtering of control characters
                            getBuffer().keyDown(ch, 0, null);
                        }
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Handle a key release event.
     */
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this instanceof Screen screen && !screen.getFocused().equals(screen)) {
            return false;
        }
        
        InputState state = getInputState();
        Character ch = state.pressedKeys.remove(keyCode);
        if (ch != null) {
            getBuffer().keyUp(ch, keyCode, null);
            return true;
        }
        return false;
    }
    
    /**
     * Handle a character input event.
     */
    default boolean charTyped(char ch, int modifiers) {
        if (this instanceof Screen screen && !screen.getFocused().equals(screen)) {
            return false;
        }
        
        if (hasKeyboard() && ch >= 32) { // Only handle printable characters
            pushQueuedChar(ch);
            return true;
        }
        return false;
    }
}

/**
 * Helper class for GLFW key code translation.
 */
@OnlyIn(Dist.CLIENT)
class GLFWTranslator {
    /**
     * Convert a GLFW key code to a character.
     */
    public static char keyToChar(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) return '\u001B';
        if (keyCode == GLFW.GLFW_KEY_ENTER) return '\r';
        if (keyCode == GLFW.GLFW_KEY_TAB) return '\t';
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) return '\b';
        if (keyCode == GLFW.GLFW_KEY_DELETE) return '\u007F';
        return '\u0000';
    }
}
