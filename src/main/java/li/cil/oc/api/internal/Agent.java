package li.cil.oc.api.internal;

import li.cil.oc.api.machine.MachineHost;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;

import java.util.UUID;

/**
 * General marker interface for autonomous agents such as robots and drones.
 */
public interface Agent extends MachineHost, Rotatable {
    /**
     * The equipment inventory of this agent.
     * <p/>
     * For example, for the robot this is the tool slot as well as slots
     * provided by containers installed in the robot, if any.
     * <p/>
     * If an agent has no equipment slots this will be a zero-sized inventory.
     */  CompletableFuture<IInventory> equipmentInventoryAsync();

    /**
     * The main inventory of this agent,  CompletableFuture<which> itAsync(usually) also can
     * interact with on its own.
     * <p/>
     * If an agent has no inventory slots this will be a zero-sized inventory.
     */  CompletableFuture<IInventory> mainInventoryAsync();

    /**
     * Provides access to the tanks of the agent.
     * <p/>
     * If an agent has no tanks this will be a zero-sized multi-tank.
     */  CompletableFuture<MultiTank> tankAsync();

    /**
     * Gets the index of the currently selected slot in the agent's inventory.
     */  CompletableFuture<int> selectedSlotAsync();

    /**
     * Set the index of the currently selected slot.
     */  CompletableFuture<Void> setSelectedSlotAsync(int index);

    /**
     * Get the index of the currently selected tank.
     */  CompletableFuture<int> selectedTankAsync();

    /**
     * Set the index of the currently selected tank.
     */  CompletableFuture<Void> setSelectedTankAsync(int index);

    /**
     * Returns the fake player used to represent the agent as an entity for
     * certain actions that require one.
     * <p/>
     * This will automatically be positioned and rotated to represent the
     * agent's current position and rotation in the world. Use this to trigger
     * events involving the agent that require a player entity.
     * <p/>
     * Note that this <em>may</em> be the common OpenComputers fake player.
     *
     * @return the fake player for the agent.
     */  CompletableFuture<PlayerEntity> playerAsync();

    /**
     * Get the name of this agent.
     */  CompletableFuture<String> nameAsync();

    /**
     * Set the name of the agent.
     */  CompletableFuture<Void> setNameAsync(String name);

    /**
     * The name of the player owning this agent, e.g. the player that placed it.
     */  CompletableFuture<String> ownerNameAsync();

    /**
     * The UUID of the player owning this agent, e.g. the player that placed it.
     */  CompletableFuture<UUID> ownerUUIDAsync();
}
