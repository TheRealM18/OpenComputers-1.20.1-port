package li.cil.oc.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of loaded components by ID. Used to send messages between
 * component representation on server and client without knowledge of their
 * containers. For now this is only used for screens / text buffer components.
 */
@Mod.EventBusSubscriber
public abstract class ComponentTracker {
    private final Map<ResourceKey<Level>, Cache<String, ManagedEnvironment>> worlds = new ConcurrentHashMap<>();

    protected Cache<String, ManagedEnvironment> components(Level world) {
        return worlds.computeIfAbsent(world.dimension(), k -> 
            CacheBuilder.newBuilder()
                .weakValues()
                .<String, ManagedEnvironment>build()
        );
    }

    public synchronized void add(Level world, String address, ManagedEnvironment component) {
        components(world).put(address, component);
    }

    public synchronized void remove(Level world, ManagedEnvironment component) {
        Cache<String, ManagedEnvironment> cache = components(world);
        cache.asMap().entrySet().stream()
            .filter(entry -> entry.getValue() == component)
            .map(Map.Entry::getKey)
            .forEach(cache::invalidate);
        cache.cleanUp();
    }

    public synchronized Optional<ManagedEnvironment> get(Level world, String address) {
        Cache<String, ManagedEnvironment> cache = components(world);
        cache.cleanUp();
        return Optional.ofNullable(cache.getIfPresent(address));
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level) {
            clear((Level) event.getWorld());
        }
    }

    protected static void clear(Level world) {
        // This will be implemented by the client-side tracker
    }
}
