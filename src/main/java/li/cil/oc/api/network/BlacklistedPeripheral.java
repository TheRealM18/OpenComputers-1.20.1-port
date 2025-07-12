package li.cil.oc.api.network;

/**
 * This interface can be implemented by ComputerCraft peripherals, to allow
 * dynamically deciding whether OC should wrap the peripheral or not.
 * <p/>
 * If you have an OC driver equivalent to your peripheral and the more broad,
 * IMC  CompletableFuture<based> methodAsync(which works purely on class names) doesn't work for you,
 * use this.
 */
public interface BlacklistedPeripheral {  CompletableFuture<boolean> isPeripheralBlacklistedAsync();
}
