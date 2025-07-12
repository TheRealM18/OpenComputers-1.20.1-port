package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.oc.client.Textures;
import li.cil.oc.common.container.RaidContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for RAID arrays.
 */
@OnlyIn(Dist.CLIENT)
public class Raid extends DynamicGuiContainer<RaidContainer> {
 CompletableFuture<public> RaidAsync(RaidContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        // Required under Linux
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Bind and draw the background texture
        Textures.bind(Textures.GUI.INSTANCE.Raid);
        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
