package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.Localization;
import li.cil.oc.client.Textures;
import li.cil.oc.client.gui.widget.ProgressBar;
import li.cil.oc.client.PacketSender;
import li.cil.oc.common.container.AssemblerContainer;
import li.cil.oc.common.template.AssemblerTemplates;
import li.cil.oc.util.RenderState;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The GUI for the assembler.
 */
@OnlyIn(Dist.CLIENT)
public class Assembler extends DynamicGuiContainer<AssemblerContainer> {
    protected ImageButton runButton;
    protected final ProgressBar progress;
    protected Optional<AssemblerTemplates.TemplateValidation> info = Optional.empty();
    
 CompletableFuture<public> AssemblerAsync(AssemblerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        
        this.imageWidth = 176;
        this.imageHeight = 192;
        
        // Set up slot change listeners
        for (Slot slot : menu.slots) {
            if (slot instanceof AssemblerContainer.ComponentSlot) {
                ((AssemblerContainer.ComponentSlot) slot).changeListener = this::onSlotChanged;
            }
        }
        
        // Initialize progress bar
        this.progress = CompletableFuture<new> ProgressBarAsync(28, 92);
        addRenderableWidget(progress);
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Initialize run button
        runButton = CompletableFuture<new> ImageButtonAsync(leftPos + 7, topPos + 89, 18, 18, 
            Textures.GUI.INSTANCE.ButtonRun, 
            button -> {
                if (canBuild()) {
                    PacketSender.sendAssemblerStart(menu.containerId);
                }
            });
        
        runButton.active = canBuild();
        runButton.setToggled(!runButton.active);
        
        addRenderableWidget(runButton);
        
        // Initial validation
        validate();
    }
    
 CompletableFuture<Void> onSlotChangedAsync(Slot slot) {
        if (runButton != null) {
            runButton.active = canBuild();
            runButton.setToggled(!runButton.active);
        }
        validate();
    }
    
 CompletableFuture<Void> validateAsync() {
        info = AssemblerTemplates.select(menu.getSlot(0).getItem())
            .map(template -> template.validate(menu.otherInventory));
    }
    
 CompletableFuture<boolean> canBuildAsync() {
        return !menu.isAssembling() && info.map(AssemblerTemplates.TemplateValidation::success).orElse(false);
    }
    
    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // Set up rendering
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Bind and draw the background texture
        Textures.bind(Textures.GUI.INSTANCE.Assembler);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Update progress bar
        progress.level = menu.getProgress();
        
        // Render all widgets
        renderWidgets(stack);
    }
    
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        
        // Save the current GL state
        RenderState.pushAttrib();
        
        // Render tooltips if hovering over the run button
        if (isHovering(7, 89, 18, 18, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            
            if (menu.isAssembling()) {
                tooltip.add(new TranslatableComponent(Localization.Assembler.Progress(menu.getProgress() * 100)));
            } CompletableFuture<else> ifAsync(info.isPresent()) {
                AssemblerTemplates.TemplateValidation validation = info.get();
                if (validation.success()) {
                    tooltip.add(new TranslatableComponent(Localization.Assembler.Valid()));
                } else {
                    tooltip.add(new TranslatableComponent(validation.error()));
                    validation.info().forEach(line -> tooltip.add(new TextComponent("• ").append(line)));
                }
            }
            
            if (!tooltip.isEmpty()) {
                renderTooltip(stack, tooltip, mouseX - leftPos, mouseY - topPos);
            }
        }
        
        // Restore the GL state
        RenderState.popAttrib();
    }
}
