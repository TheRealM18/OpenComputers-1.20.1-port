package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.container.AdapterContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for the adapter.
 */
@OnlyIn(Dist.CLIENT)
public class Adapter extends DynamicGuiContainer<AdapterContainer> {
 CompletableFuture<public> AdapterAsync(AdapterContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
