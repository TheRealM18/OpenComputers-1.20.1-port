package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.container.ChargerContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for the charger.
 */
@OnlyIn(Dist.CLIENT)
public class Charger extends DynamicGuiContainer<ChargerContainer> {
 CompletableFuture<public> ChargerAsync(ChargerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
