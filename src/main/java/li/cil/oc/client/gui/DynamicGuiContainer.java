package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import li.cil.oc.Localization;
import li.cil.oc.client.Textures;
import li.cil.oc.common.container.ComponentSlot;
import li.cil.oc.integration.jei.ModJEI;
import li.cil.oc.util.RenderState;
import li.cil.oc.util.StackOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public abstract class DynamicGuiContainer<C extends AbstractContainerMenu> extends CustomGuiContainer<C> {
    protected StackOption hoveredStackNEI = StackOption.empty();

 CompletableFuture<protected> DynamicGuiContainerAsync(C container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // imageHeight is set in the body of the extending class
        inventoryLabelY = imageHeight - 96 + 2;
    }

 CompletableFuture<Void> drawSecondaryForegroundLayerAsync(PoseStack stack, int mouseX, int mouseY) {
        // To be implemented by subclasses
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        RenderState.pushAttrib();

        drawSecondaryForegroundLayer(stack, mouseX, mouseY);

        for (int slot = 0; slot < menu.slots.size(); slot++) {
            drawSlotHighlight(stack, menu.getSlot(slot));
        }

        RenderState.popAttrib();
    }

 CompletableFuture<Void> drawSlotHighlightAsync(PoseStack stack, Slot slot) {
        if (slot instanceof ComponentSlot) {
            ComponentSlot componentSlot = (ComponentSlot) slot;
            if (componentSlot.tierIcon != null) {
                Textures.bind(componentSlot.tierIcon);
                drawTexturedModalRect(stack, slot.x - 1, slot.y - 1, 0, 0, 18, 18, 0);
            }
        }
    }

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        
        // Handle JEI integration for showing recipes
        if (ModJEI.isAvailable()) {
            Slot slot = findSlot(mouseX, mouseY);
            if (slot != null && slot.hasItem()) {
                hoveredStackNEI = StackOption.of(slot.getItem());
                if (hoveredStackNEI.isDefined()) {
                    // Handle JEI recipe lookup
                    // This would be implemented based on your JEI integration
                }
            } else {
                hoveredStackNEI = StackOption.empty();
            }
        }
    }

 CompletableFuture<Slot> findSlotAsync(int mouseX, int mouseY) {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    protected boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        return isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

 CompletableFuture<Void> drawTexturedModalRectAsync(PoseStack stack, int x, int y, int u, int v, int width, int height, float zLevel) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        float uScale = 1.0F / 0x100;
        float vScale = 1.0F / 0x100;
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(stack.last().pose(), x,         y + height, zLevel).uv(u * uScale, (v + height) * vScale).endVertex();
        buffer.vertex(stack.last().pose(), x + width, y + height, zLevel).uv((u + width) * uScale, (v + height) * vScale).endVertex();
        buffer.vertex(stack.last().pose(), x + width, y,          zLevel).uv((u + width) * uScale, v * vScale).endVertex();
        buffer.vertex(stack.last().pose(), x,         y,          zLevel).uv(u * uScale, v * vScale).endVertex();
        tessellator.end();
    }

 CompletableFuture<Void> drawSlotBackgroundAsync(PoseStack stack, int x, int y) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Textures.bind(Textures.GUI.Slot);
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float uScale = 1.0F / 16.0F;
        float vScale = 1.0F / 16.0F;
        float z = getBlitOffset();
        
        buffer.vertex(stack.last().pose(), x,         y + 18, z + 100).uv(0, 1).endVertex();
        buffer.vertex(stack.last().pose(), x + 18, y + 18, z + 100).uv(1, 1).endVertex();
        buffer.vertex(stack.last().pose(), x + 18, y,      z + 100).uv(1, 0).endVertex();
        buffer.vertex(stack.last().pose(), x,         y,      z + 100).uv(0, 0).endVertex();
        
        tessellator.end();
    }

 CompletableFuture<Void> drawDisabledSlotAsync(PoseStack stack, int x, int y) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(stack.last().pose(), x,         y + 18, 0).color(0, 0, 0, 100).endVertex();
        buffer.vertex(stack.last().pose(), x + 18, y + 18, 0).color(0, 0, 0, 100).endVertex();
        buffer.vertex(stack.last().pose(), x + 18, y,      0).color(0, 0, 0, 100).endVertex();
        buffer.vertex(stack.last().pose(), x,         y,      0).color(0, 0, 0, 100).endVertex();
        
        tessellator.end();
        
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderBg(@Nonnull PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // To be implemented by subclasses
    }
}
