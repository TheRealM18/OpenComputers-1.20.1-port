package li.cil.oc.client.gui;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.container.DiskDriveContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI for disk drives, which allows inserting/removing disks.
 */
@OnlyIn(Dist.CLIENT)
public class DiskDrive extends DynamicGuiContainer<DiskDriveContainer> {
    /**
     * Create a new disk drive GUI.
     *
     * @param container        The container for this GUI
     * @param playerInventory The player's inventory
     * @param title           The title of the GUI
     */
 CompletableFuture<public> DiskDriveAsync(DiskDriveContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
