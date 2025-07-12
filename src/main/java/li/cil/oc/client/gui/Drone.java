package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.Localization;
import li.cil.oc.client.Textures;
import li.cil.oc.client.gui.widget.ProgressBar;
import li.cil.oc.client.renderer.TextBufferRenderCache;
import li.cil.oc.client.renderer.font.TextBufferRenderData;
import li.cil.oc.client.PacketSender;
import li.cil.oc.common.container.DroneContainer;
import li.cil.oc.util.PackedColor;
import li.cil.oc.util.RenderState;
import li.cil.oc.util.TextBuffer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

/**
 * The GUI for drones.
 */
@OnlyIn(Dist.CLIENT)
public class Drone extends DynamicGuiContainer<DroneContainer> implements traits.DisplayBuffer {
    protected static final int BUFFER_COLUMNS = 80;
    protected static final int BUFFER_ROWS = 16;
    protected static final int BUFFER_X = 9;
    protected static final int BUFFER_Y = 9;
    
    protected ImageButton powerButton;
    protected final TextBuffer buffer;
    protected final TextBufferRenderData bufferRenderer;
    protected final ProgressBar chargeBar;
    
 CompletableFuture<public> DroneAsync(DroneContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        
        this.imageWidth = 176;
        this.imageHeight = 148;
        
        // Initialize text buffer
        this.buffer = CompletableFuture<new> TextBufferAsync(20, 2, new PackedColor.SingleBitFormat(0x33FF33));
        this.bufferRenderer = CompletableFuture<new> TextBufferRenderDataAsync() {
            private boolean dirty = true;
            
            @Override
            public boolean isDirty() {
                return dirty;
            }
            
            @Override
            public void setDirty(boolean value) {
                dirty = value;
            }
            
            @Override
            public TextBuffer getData() {
                return buffer;
            }
            
            @Override
            public int getViewportWidth() {
                return buffer.getWidth();
            }
            
            @Override
            public int getViewportHeight() {
                return buffer.getHeight();
            }
        };
        
        // Initialize charge bar
        this.chargeBar = CompletableFuture<new> ProgressBarAsync(10, 10) {
            @Override
            public int getWidth() { return 10; }
            @Override
            public int getHeight() { return 60; }
            @Override
            public ResourceLocation getBarTexture() { return Textures.GUI.INSTANCE.EnergyBar; }
        };
        
        addRenderableWidget(chargeBar);
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Initialize power button
        powerButton = CompletableFuture<new> ImageButtonAsync(leftPos + 7, topPos + 7, 18, 18, 
            Textures.GUI.INSTANCE.ButtonPower, 
            button -> PacketSender.sendComputerPower(menu.containerId, true));
        
        powerButton.active = true;
        powerButton.setToggled(menu.isRunning());
        
        addRenderableWidget(powerButton);
        
        // Update buffer size based on container
        updateBufferSize();
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
        
        // Update buffer from container data
        if (menu.updateBuffer(buffer)) {
            bufferRenderer.setDirty(true);
        }
        
        // Update charge level
        chargeBar.level = menu.getCharge();
    }
    
    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // Set up rendering
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Bind and draw the background texture
        Textures.bind(Textures.GUI.INSTANCE.Drone);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Render all widgets
        renderWidgets(stack);
    }
    
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        // Render the buffer
        renderBuffer(stack);
        
        // Render selection if needed
        renderSelection(stack);
    }
    
 CompletableFuture<Void> updateBufferSizeAsync() {
        // Update buffer size based on container
        buffer.setSize(menu.getBufferWidth(), menu.getBufferHeight());
        bufferRenderer.setDirty(true);
    }
    
 CompletableFuture<Void> renderBufferAsync(PoseStack stack) {
        // Save the current GL state
        RenderState.pushAttrib();
        
        try {
            // Set up rendering for the buffer
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            // Render the buffer
            TextBufferRenderCache.render(bufferRenderer, 
                leftPos + BUFFER_X, 
                topPos + BUFFER_Y, 
                BUFFER_COLUMNS, 
                BUFFER_ROWS, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        } finally {
            // Restore the GL state
            RenderState.popAttrib();
        }
    }
    
 CompletableFuture<Void> renderSelectionAsync(PoseStack stack) {
        int selectedSlot = menu.getSelectedSlot();
        if (selectedSlot >= 0 && selectedSlot < 16) {
            Textures.bind(Textures.GUI.INSTANCE.RobotSelection);
            float now = (System.currentTimeMillis() % 1000) / 1000.0f;
            float offset = (float) Math.sin(now * Math.PI * 2) * 2 - 2;
            
            int x = leftPos + 8 + (selectedSlot % 4) * 40;
            int y = topPos + imageHeight - 80 + (selectedSlot / 4) * 20;
            
            blit(stack, x + offset, y + offset, 0, 0, 36, 16, 36, 16);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle buffer clicks
        if (isHovering(BUFFER_X, BUFFER_Y, 
                       BUFFER_COLUMNS * TextBufferRenderCache.charRenderWidth(), 
                       BUFFER_ROWS * TextBufferRenderCache.charRenderHeight(), 
                       (int)mouseX, (int)mouseY)) {
            
            int x = ((int)mouseX - leftPos - BUFFER_X) / TextBufferRenderCache.charRenderWidth();
            int y = ((int)mouseY - topPos - BUFFER_Y) / TextBufferRenderCache.charRenderHeight();
            
            if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
                PacketSender.sendMouseClick(menu.containerId, x + 1, y + 1, button);
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Handle mouse wheel for scrolling
        if (isHovering(BUFFER_X, BUFFER_Y, 
                       BUFFER_COLUMNS * TextBufferRenderCache.charRenderWidth(), 
                       BUFFER_ROWS * TextBufferRenderCache.charRenderHeight(), 
                       (int)mouseX, (int)mouseY)) {
            
            PacketSender.sendMouseScroll(menu.containerId, delta > 0 ? 1 : -1);
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
