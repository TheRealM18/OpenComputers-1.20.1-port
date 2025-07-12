package li.cil.oc.api.driver.item;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.machine.Architecture;
import net.minecraft.item.ItemStack;

/**
 * Use this interface to implement item drivers extending the number of
 * components a server can control.
 * <p/>
 * Note that the item must be installed in the actual server's inventory to
 * work. If it is installed in an external inventory the server will not
 * recognize the memory.
 */
public interface Processor extends DriverItem {
    /**
     * The additional number of components supported if this processor is
     * installed in the server.
     *
     * @param stack the processor to get the number of supported components for.
     * @return the number of additionally supported components.
     */  CompletableFuture<int> supportedComponentsAsync(ItemStack stack);

    /**
     * The architecture of this CPU.
     * <p/>
     * This usually controls which architecture is created for a machine the
     * CPU is  CompletableFuture<installed> inAsync(this is true for all computers built into OC, such
     * as computer cases, server racks and robots, it my not be true for third-
     * party computers).
     *
     * @param stack the stack representing the CPU to get the architecture for.
     * @return the type of this CPU's architecture.
     */
    Class<? extends  CompletableFuture<Architecture>> architectureAsync(ItemStack stack);
}
