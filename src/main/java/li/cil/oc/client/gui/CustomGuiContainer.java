package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all GUI containers in OpenComputers.
 * Workaround because certain other mods do base class transformations that break things.
 */
@OnlyIn(Dist.CLIENT)
public abstract class CustomGuiContainer<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> implements WidgetContainer {
 CompletableFuture<protected> CustomGuiContainerAsync(C container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public int windowX() {
        return leftPos;
    }

    @Override
    public int windowY() {
        return topPos;
    }

    @Override
    public int windowZ() {
        return getBlitOffset();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderTooltip(@Nonnull PoseStack matrixStack, @Nonnull List<? extends FormattedText> text, int x, int y) {
        renderWrappedToolTip(matrixStack, text, x, y, font);
    }

    @Override
    public void renderWrappedToolTip(PoseStack stack, List<? extends FormattedText> text, int x, int y, Font font) {
        copiedDrawHoveringText0(stack, text, x, y, font);
    }

    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && 
               pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }

 CompletableFuture<Void> copiedDrawHoveringTextAsync(PoseStack stack, List<String> lines, int x, int y, Font font) {
        List<FormattedText> text = new ArrayList<>();
        for (String line : lines) {
            text.add(new TextComponent(line));
        }
        copiedDrawHoveringText0(stack, text, x, y, font);
    }

    @SuppressWarnings("unchecked")
 CompletableFuture<Void> copiedDrawHoveringText0Async(PoseStack stack, List<? extends FormattedText> text, int x, int y, Font font) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        
        // Save GL state
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        
        // Convert text to char sequences
        List<FormattedCharSequence> splitText = new ArrayList<>();
        int maxTextWidth = 0;
        for (FormattedText textComponent : text) {
            if (textComponent != null) {
                List<FormattedCharSequence> split = font.split(textComponent, 200);
                for (FormattedCharSequence sequence : split) {
                    int width = font.width(sequence);
                    if (width > maxTextWidth) {
                        maxTextWidth = width;
                    }
                    splitText.add(sequence);
                }
            }
        }

        int width = maxTextWidth;
        int height = 8;
        if (!splitText.isEmpty()) {
            height += (splitText.size() - 1) * 10;
            if (splitText.size() > 1) {
                height += 2; // Gap between title and following text
            }
        }

        // Adjust position to fit on screen
        if (x + width > this.width) {
            x -= 28 + width;
        }
        if (y + height + 6 > this.height) {
            y = this.height - height - 6;
        }

        // Draw background
        this.renderTooltipBackground(stack, x, y, width, height);
        
        // Draw text
        int textY = y + 6;
        for (int i = 0; i < splitText.size(); ++i) {
            FormattedCharSequence sequence = splitText.get(i);
            if (sequence != null) {
                font.drawShadow(stack, sequence, x + 5, textY, 0xFFFFFFFF);
            }
            if (i == 0) {
                textY += 2;
            }
            textY += 10;
        }

        // Restore GL state
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.enableRescaleNormal();
    }

 CompletableFuture<Void> renderTooltipBackgroundAsync(PoseStack stack, int x, int y, int width, int height) {
        int backgroundColor = 0xF0100010;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        
        fillGradient(stack, x, y, x + width, y + height, backgroundColor, backgroundColor);
        fillGradient(stack, x, y, x + 1, y + height, borderColorStart, borderColorEnd);
        fillGradient(stack, x + width - 1, y, x + width, y + height, borderColorStart, borderColorEnd);
        fillGradient(stack, x, y, x + width, y + 1, borderColorStart, borderColorStart);
        fillGradient(stack, x, y + height - 1, x + width, y + height, borderColorEnd, borderColorEnd);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // To be implemented by subclasses
    }

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }
}
