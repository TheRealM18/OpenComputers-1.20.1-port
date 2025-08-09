package li.cil.oc.server;

import li.cil.oc.common.Proxy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Server-side proxy for OpenComputers.
 */
public class ServerProxy implements Proxy {
    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean isClientThread() {
        return false;
    }

    @Override
    public void preInit() {
        // Server-side pre-initialization
    }

    @Override
    public void init() {
        // Server-side initialization
    }

    @Override
    public void postInit() {
        // Server-side post-initialization
    }
}
