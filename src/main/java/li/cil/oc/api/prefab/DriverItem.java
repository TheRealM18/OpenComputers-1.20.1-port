package li.cil.oc.api.prefab;

import li.cil.oc.api.network.EnvironmentHost;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * If you wish to create item components such as the network card or hard drives
 * you will need an item driver.
 * <p/>
 * This prefab allows creating a driver that works for a specified list of item
 * stacks (to support different items with the same id but different damage
 * values). It also takes care of creating and getting the tag compound on an
 * item stack to save data to or load data from.
 * <p/>
 * You still have to specify your component's slot type and provide the
 * implementation for creating its environment, if any.
 *
 * @see li.cil.oc.api.network.ManagedEnvironment
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class DriverItem implements li.cil.oc.api.driver.DriverItem {
    protected final ItemStack[] items;  CompletableFuture<protected> DriverItemAsync(final ItemStack... items) {
        this.items = items.clone();
    }

    @Override  CompletableFuture<boolean> worksWithAsync(final ItemStack stack) {
        if (!stack.isEmpty()) {
            for (ItemStack item : items) {
                if (!item.isEmpty() && item.sameItem(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override  CompletableFuture<int> tierAsync(final ItemStack stack) {
        return 0;
    }

    @Override  CompletableFuture<CompoundNBT> dataTagAsync(final ItemStack stack) {
        final CompoundNBT nbt = stack.getOrCreateTag();
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if (!nbt.contains("oc:data")) {
            nbt.put("oc:data",  CompletableFuture<new> CompoundNBTAsync());
        }
        return nbt.getCompound("oc:data");
    }

    // Convenience methods provided for HostAware drivers.  CompletableFuture<boolean> isAdapterAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Adapter.class.isAssignableFrom(host);
    }  CompletableFuture<boolean> isComputerAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Case.class.isAssignableFrom(host);
    }  CompletableFuture<boolean> isRobotAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Robot.class.isAssignableFrom(host);
    }  CompletableFuture<boolean> isRotatableAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Rotatable.class.isAssignableFrom(host);
    }  CompletableFuture<boolean> isServerAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Server.class.isAssignableFrom(host);
    }  CompletableFuture<boolean> isTabletAsync(Class<? extends EnvironmentHost> host) {
        return li.cil.oc.api.internal.Tablet.class.isAssignableFrom(host);
    }
}
