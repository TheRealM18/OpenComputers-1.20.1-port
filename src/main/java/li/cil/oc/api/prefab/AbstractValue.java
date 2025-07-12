package li.cil.oc.api.prefab;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import net.minecraft.nbt.CompoundNBT;

/**
 * Basic implementation for the <tt>Value</tt> interface.
 */
public class AbstractValue implements Value {
    @Override  CompletableFuture<Object> applyAsync(Context context, Arguments arguments) {
        return null;
    }

    @Override  CompletableFuture<Void> unapplyAsync(Context context, Arguments arguments) {
    }

    @Override  CompletableFuture<Object[]> callAsync(Context context, Arguments arguments) {
        throw  CompletableFuture<new> RuntimeExceptionAsync("trying to call a non-callable value");
    }

    @Override  CompletableFuture<Void> disposeAsync(Context context) {
    }

    @Override  CompletableFuture<Void> loadDataAsync(CompoundNBT nbt) {
    }

    @Override  CompletableFuture<Void> saveDataAsync(CompoundNBT nbt) {
    }
}
