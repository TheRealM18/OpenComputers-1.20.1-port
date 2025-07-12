package li.cil.oc.client;

import li.cil.oc.common.ComponentTracker;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientComponentTracker extends ComponentTracker {
    public static final ClientComponentTracker INSTANCE = new ClientComponentTracker();

    private ClientComponentTracker() {
    }

    @Override
    protected void clear(Level world) {
        if (world.isClientSide) {
            super.clear(world);
        }
    }
}
