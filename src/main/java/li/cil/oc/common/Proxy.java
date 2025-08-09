package li.cil.oc.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Common proxy interface for OpenComputers.
 * This is the base interface for both client and server proxies.
 */
public interface Proxy {
    /**
     * Called during mod initialization.
     */
    default void preInit() {}

    /**
     * Called during mod loading.
     */
    default void init() {}

    /**
     * Called after mod loading is complete.
     */
    default void postInit() {}

    /**
     * Check if this is the client side proxy.
     * @return True if this is the client side proxy, false otherwise.
     */
    boolean isClient();

    /**
     * Get the client world. Only works on the client side.
     * @return The client world, or null if not on the client side.
     */
    default Level getClientWorld() {
        return null;
    }

    /**
     * Get the client player. Only works on the client side.
     * @return The client player, or null if not on the client side.
     */
    default Player getClientPlayer() {
        return null;
    }

    /**
     * Initialize the proxy. This is called during mod construction.
     */
    static void initialize() {
        // The actual implementation is handled by the specific proxy implementation
    }

    /**
     * Check if the current thread is the client thread.
     * @return True if this is the client thread, false otherwise.
     */
    default boolean isClientThread() {
        return false;
    }
}
