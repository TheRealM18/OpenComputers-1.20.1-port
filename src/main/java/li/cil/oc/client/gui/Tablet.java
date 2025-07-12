package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.container.TabletContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The GUI for tablets.
 */
@OnlyIn(Dist.CLIENT)
public class Tablet extends DynamicGuiContainer<TabletContainer> {
 CompletableFuture<public> TabletAsync(TabletContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected ItemStack lockedStack() {
        return menu.getTabletStack();
    }
}
