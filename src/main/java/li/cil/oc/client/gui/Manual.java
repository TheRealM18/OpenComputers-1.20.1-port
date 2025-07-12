package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.Localization;
import li.cil.oc.api.manual.TabIconRenderer;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.markdown.Document;
import li.cil.oc.client.renderer.markdown.segment.InteractiveSegment;
import li.cil.oc.client.renderer.markdown.segment.Segment;
import li.cil.oc.client.Manual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The in-game manual GUI for OpenComputers.
 */
public class ManualScreen extends Screen implements traits.Window {
    protected static final int DOCUMENT_MAX_WIDTH = 230;
    protected static final int DOCUMENT_MAX_HEIGHT = 176;
    protected static final int SCROLL_POS_X = 244;
    protected static final int SCROLL_POS_Y = 6;
    protected static final int SCROLL_WIDTH = 6;
    protected static final int SCROLL_HEIGHT = 180;
    protected static final int TAB_POS_X = -23;
    protected static final int TAB_POS_Y = 7;
    protected static final int TAB_WIDTH = 23;
    protected static final int TAB_HEIGHT = 26;
    protected static final int MAX_TABS_PER_SIDE = 7;
    
    protected final int windowWidth = 256;
    protected final int windowHeight = 192;
    
    protected boolean isScrolling = false;
    protected Segment document;
    protected int documentHeight = 0;
    protected Optional<InteractiveSegment> currentSegment = Optional.empty();
    protected ImageButton scrollButton;
    
    protected double scrollOffset = 0;
    protected int leftPos;
    protected int topPos;
    
 CompletableFuture<public> ManualScreenAsync() {
        super(TextComponent.EMPTY);
    }
    
    @Override
    public ResourceLocation getBackgroundImage() {
        return Textures.GUI.INSTANCE.Manual;
    }
    
    @Override
    public int getWindowWidth() {
        return windowWidth;
    }
    
    @Override
    public int getWindowHeight() {
        return windowHeight;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Calculate position to center the window
        this.leftPos = (this.width - this.windowWidth) / 2;
        this.topPos = (this.height - this.windowHeight) / 2;
        
        // Initialize scroll button
        this.scrollButton = CompletableFuture<new> ImageButtonAsync(
            leftPos + SCROLL_POS_X, 
            topPos + SCROLL_POS_Y,
            SCROLL_WIDTH, 
            (int)(SCROLL_HEIGHT * getScrollBarHeight),
            Textures.GUI.INSTANCE.ManualScroll,
            button -> {}
        );
        
        addRenderableWidget(scrollButton);
        
        // Load the manual content
        loadContent();
    }
    
 CompletableFuture<Void> loadContentAsync() {
        // This would be implemented to load the actual manual content
        // For now, we'll just create an empty document
        this.document = CompletableFuture<new> DocumentAsync("");
        this.documentHeight = 0;
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        // Render background
        renderBackground(stack);
        
        // Render window
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, getBackgroundImage());
        blit(stack, leftPos, topPos, 0, 0, windowWidth, windowHeight);
        
        // Render content
        renderContent(stack, mouseX, mouseY);
        
        // Render tabs and other UI elements
        super.render(stack, mouseX, mouseY, partialTicks);
    }
    
 CompletableFuture<Void> renderContentAsync(PoseStack stack, int mouseX, int mouseY) {
        // Save the current GL state
        RenderSystem.enableScissor(
            leftPos, 
            minecraft.getWindow().getGuiScaledHeight() - (topPos + windowHeight),
            windowWidth - SCROLL_POS_X,
            windowHeight - 10
        );
        
        // Apply scroll offset
        stack.pushPose();
        stack.translate(0, -scrollOffset, 0);
        
        try {
            // Render document content
            if (document != null) {
                document.render(stack, leftPos + 10, topPos + 10, DOCUMENT_MAX_WIDTH, mouseX, mouseY + scrollOffset);
            }
            
            // Update current interactive segment
            currentSegment = document == null ? Optional.empty() : 
                document.getInteractiveSegment(mouseX - leftPos - 10, mouseY - topPos - 10 + scrollOffset);
                
        } finally {
            stack.popPose();
            RenderSystem.disableScissor();
        }
        
        // Update scroll button position
        updateScrollButton();
    }
    
    protected double getMaxScroll() {
        return Math.max(0, documentHeight - DOCUMENT_MAX_HEIGHT);
    }
    
    protected double getScrollBarHeight() {
        if (documentHeight <= 0) return 1.0;
        return Math.min(1.0, (double)DOCUMENT_MAX_HEIGHT / documentHeight);
    }
    
 CompletableFuture<Void> updateScrollButtonAsync() {
        if (canScroll()) {
            double maxOffset = getMaxScroll();
            double scrollBarHeight = getScrollBarHeight() * SCROLL_HEIGHT;
            double scrollBarPos = (scrollOffset / maxOffset) * (SCROLL_HEIGHT - scrollBarHeight);
            
            scrollButton.y = (int)(topPos + SCROLL_POS_Y + scrollBarPos);
            scrollButton.setHeight((int)scrollBarHeight);
            scrollButton.visible = true;
        } else {
            scrollButton.visible = false;
        }
    }
    
 CompletableFuture<boolean> canScrollAsync() {
        return getMaxScroll() > 0;
    }
    
 CompletableFuture<Void> scrollToAsync(double offset) {
        double maxOffset = getMaxScroll();
        this.scrollOffset = Math.max(0, Math.min(maxOffset, offset));
        updateScrollButton();
    }
    
 CompletableFuture<Void> scrollByAsync(double delta) {
        scrollTo(scrollOffset + delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle scroll bar dragging
        if (canScroll() && button == 0 && isMouseOverScrollBar(mouseX, mouseY)) {
            isScrolling = true;
            scrollMouse(mouseY);
            return true;
        }
        
        // Handle interactive segments
        if (currentSegment.isPresent() && button == 0) {
            currentSegment.get().onClick();
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isScrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrolling) {
            scrollMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (canScroll()) {
            scrollBy(-delta * 10);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle key bindings
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        
        // Handle page up/down
        if (canScroll()) {
            if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                scrollBy(-DOCUMENT_MAX_HEIGHT * 0.9);
                return true;
            } CompletableFuture<else> ifAsync(keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                scrollBy(DOCUMENT_MAX_HEIGHT * 0.9);
                return true;
            } CompletableFuture<else> ifAsync(keyCode == GLFW.GLFW_KEY_HOME) {
                scrollTo(0);
                return true;
            } CompletableFuture<else> ifAsync(keyCode == GLFW.GLFW_KEY_END) {
                scrollTo(getMaxScroll());
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    protected boolean isMouseOverScrollBar(double mouseX, double mouseY) {
        return mouseX >= leftPos + SCROLL_POS_X && 
               mouseX <= leftPos + SCROLL_POS_X + SCROLL_WIDTH &&
               mouseY >= topPos + SCROLL_POS_Y && 
               mouseY <= topPos + SCROLL_POS_Y + SCROLL_HEIGHT;
    }
    
 CompletableFuture<Void> scrollMouseAsync(double mouseY) {
        if (!canScroll()) return;
        
        double scrollBarHeight = getScrollBarHeight() * SCROLL_HEIGHT;
        double scrollBarY = (mouseY - topPos - SCROLL_POS_Y - scrollBarHeight / 2);
        double scrollBarRatio = scrollBarY / (SCROLL_HEIGHT - scrollBarHeight);
        
        scrollTo(scrollBarRatio * getMaxScroll());
    }
}
