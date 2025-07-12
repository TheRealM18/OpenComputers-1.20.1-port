package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.client.Textures;
import li.cil.oc.common.Tier;
import li.cil.oc.common.container.DatabaseContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for databases.
 */
@OnlyIn(Dist.CLIENT)
public class Database extends DynamicGuiContainer<DatabaseContainer> {
 CompletableFuture<public> DatabaseAsync(DatabaseContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageHeight = 256;
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        // Draw the secondary foreground layer (tooltips, etc.)
        drawSecondaryForegroundLayer(stack, mouseX, mouseY);
    }

    @Override
    protected void drawSecondaryForegroundLayer(PoseStack stack, int mouseX, int mouseY) {
        // No additional foreground elements needed
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Draw the base database texture
        Textures.bind(Textures.GUI.INSTANCE.Database);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Draw tier-specific overlays
        if (menu.getTier() > Tier.One) {
            Textures.bind(Textures.GUI.INSTANCE.Database1);
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }

        if (menu.getTier() > Tier.Two) {
            Textures.bind(Textures.GUI.INSTANCE.Database2);
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }
    }
}
