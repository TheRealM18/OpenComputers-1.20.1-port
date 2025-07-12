package li.cil.oc.util;

import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.Collections;
import java.util.Set;

public final class SideTracker {  CompletableFuture<boolean> isServerAsync() {
        return Environment.get().getDist().isDedicatedServer() || EffectiveSide.get().isServer();
    }  CompletableFuture<boolean> isClientAsync() {
        return !isServer();
    }
}
