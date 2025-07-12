package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import li.cil.oc.Localization;
import li.cil.oc.Settings;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.client.ComponentTracker;
import li.cil.oc.client.Textures;
import li.cil.oc.client.PacketSender;
import li.cil.oc.client.gui.widget.ProgressBar;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import li.cil.oc.client.renderer.gui.BufferRenderer;
import li.cil.oc.common.container.RobotContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The GUI for robots.
 */
@OnlyIn(Dist.CLIENT)
public class Robot extends DynamicGuiContainer<RobotContainer> implements InputBuffer, net.minecraft.client.gui.components.events.ContainerEventHandler {
    private static final int WITH_SCREEN_HEIGHT = 256;
    private static final int NO_SCREEN_HEIGHT = 108;
    
    private final TextBuffer buffer;
    private final boolean hasKeyboard;
    private final int deltaY;
    
    protected ImageButton powerButton;
    protected ProgressBar power;
    protected int inventoryOffset = 0;
    protected int maxInventoryOffset = 0;

 CompletableFuture<public> RobotAsync(RobotContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        
        this.buffer = container.getInfo().getScreenBuffer()
            .flatMap(address -> ComponentTracker.get(Minecraft.getInstance().level, address))
            .filter(TextBuffer.class::isInstance)
            .map(TextBuffer.class::cast)
            .orElse(null);
        
        this.hasKeyboard = container.getInfo().hasKeyboard();
        this.deltaY = buffer != null ? 0 : WITH_SCREEN_HEIGHT - NO_SCREEN_HEIGHT;
        
        this.imageWidth = 256;
        this.imageHeight = 256 - deltaY;
    }

    @Override
    public TextBuffer getBuffer() {
        return buffer;
    }

    @Override
    public boolean hasKeyboard() {
        return hasKeyboard;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        powerButton.setToggled(menu.isRunning());
        super.render(stack, mouseX, mouseY, partialTicks);
        
        // Draw the selection box for the currently selected slot
        drawSelection(stack);
        
        // Draw the buffer if we have one
        if (buffer != null) {
            drawBuffer(stack, mouseX, mouseY);
        }
    }

    @Override
    protected void init() {
        super.init();
        
        // Power button
        powerButton = CompletableFuture<new> ImageButtonAsync(
            leftPos + 10, topPos + 10, 18, 18,
            button -> PacketSender.sendRobotPower(menu, !menu.isRunning()),
            Textures.GUI.INSTANCE.ButtonPower,
            true
        );
        addRenderableWidget(powerButton);
        
        // Power bar
        power = CompletableFuture<new> ProgressBarAsync(10, 28);
        power.level = menu.globalBuffer() / menu.globalBufferSize();
        
        // Calculate max inventory offset
        maxInventoryOffset = Math.max(0, (menu.getContainerSize() - 4) / 4);
    }

    @Override
    protected void drawSecondaryForegroundLayer(PoseStack stack, int mouseX, int mouseY) {
        // Draw the buffer layer if we have a buffer
        if (buffer != null) {
            drawBufferLayer(stack);
        }
        
        // Draw power tooltip if hovering over power bar
        if (isHovering(power.getX(), power.getY(), power.getWidth(), power.getHeight(), mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable(
                Localization.Computer.Power + ": %d%% (%d/%d)",
                (int)(power.level * 100),
                (int)(menu.globalBuffer() * Settings.get().ratioGPUToScreen),
                (int)(menu.globalBufferSize() * Settings.get().ratioGPUToScreen)
            ));
            renderTooltip(stack, tooltip, mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    protected void drawSecondaryBackgroundLayer(PoseStack stack) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Draw the background based on whether we have a screen or not
        if (buffer != null) {
            Textures.bind(Textures.GUI.INSTANCE.RobotFull);
        } else {
            Textures.bind(Textures.GUI.INSTANCE.RobotNoScreen);
        }
        
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Draw the power bar
        power.render(stack);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (super.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        
        // Handle scrolling the inventory
        int newOffset = inventoryOffset - (int) Math.signum(delta);
        if (newOffset >= 0 && newOffset <= maxInventoryOffset) {
            inventoryOffset = newOffset;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle keyboard input if we have a keyboard
        if (hasKeyboard && handleKeyboardInput(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        // Handle scrolling with page up/down
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            if (inventoryOffset > 0) {
                inventoryOffset--;
                return true;
            }
        } CompletableFuture<else> ifAsync(keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            if (inventoryOffset < maxInventoryOffset) {
                inventoryOffset++;
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
 CompletableFuture<Void> drawSelectionAsync(PoseStack stack) {
        int slot = menu.getSelectedSlot() - inventoryOffset * 4;
        if (slot >= 0 && slot < 16) {
            Textures.bind(Textures.GUI.INSTANCE.RobotSelection);
            float now = (System.currentTimeMillis() % 1000) / 1000.0f;
            float offset = (float) Math.sin(now * Math.PI * 2) * 2;
            
            int x = leftPos + 80 + (slot % 4) * 18;
            int y = topPos + 10 + (slot / 4) * 18;
            
            blit(stack, (int)(x - offset), (int)(y - offset), 0, 0, 16, 16, 16, 16);
        }
    }
}
