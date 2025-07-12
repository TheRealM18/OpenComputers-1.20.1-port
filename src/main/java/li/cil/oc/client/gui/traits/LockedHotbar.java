package li.cil.oc.client.gui.traits;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Trait for container screens that want to lock a specific hotbar slot.
 * Prevents interaction with the specified locked stack in the hotbar.
 *
 * @param <T> The container type
 */
@OnlyIn(Dist.CLIENT)
public interface LockedHotbar<T extends AbstractContainerMenu> {
    /**
     * Get the item stack that should be locked in the hotbar.
     *
     * @return The locked item stack
     */
    ItemStack getLockedStack();
    
    /**
     * Handle slot clicking, with special handling for locked slots.
     *
     * @param slot The slot that was clicked, or null if clicked outside the GUI
     * @param slotId The ID of the slot that was clicked
     * @param mouseButton The mouse button that was used
     * @param clickType The type of click
     * @return True if the click was handled, false otherwise
     */
    default boolean handleSlotClicked(Slot slot, int slotId, int mouseButton, ClickType clickType) {
        if (slot == null || !ItemStack.isSame(slot.getItem(), getLockedStack())) {
            return false; // Let the default handling occur
        }
        return true; // Click was handled (ignored) for the locked slot
    }
}
