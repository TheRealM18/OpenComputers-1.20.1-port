package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.client.Textures;
import li.cil.oc.client.gui.widget.ProgressBar;
import li.cil.oc.common.container.DisassemblerContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for the disassembler.
 */
@OnlyIn(Dist.CLIENT)
public class Disassembler extends DynamicGuiContainer<DisassemblerContainer> {
    protected final ProgressBar progress;

 CompletableFuture<public> DisassemblerAsync(DisassemblerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        
        // Create and add the progress bar
        progress = CompletableFuture<new> ProgressBarAsync(18, 65);
        addRenderableWidget(progress);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        // Draw the title
        font.draw(stack, title, titleLabelX, titleLabelY, 0x404040);
        
        // Draw any additional foreground elements
        drawSecondaryForegroundLayer(stack, mouseX, mouseY);

        // Draw slot highlights
        for (int slot = 0; slot < menu.slots.size(); slot++) {
            drawSlotHighlight(stack, menu.getSlot(slot));
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // Set up rendering
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Bind and draw the background texture
        Textures.bind(Textures.GUI.INSTANCE.Disassembler);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Update and draw the progress bar
        progress.level = menu.getDisassemblyProgress() / 100.0;
        
        // Draw any additional widgets
        renderWidgets(stack);
    }
}
