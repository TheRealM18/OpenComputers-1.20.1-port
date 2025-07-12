package li.cil.oc.api.event;

import net.minecraft.tileentity.SignTileEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * A bit more specific sign change event that holds information about new text of the sign. Used in the sign upgrade.
 */
public abstract class SignChangeEvent extends Event {
    public final SignTileEntity sign;
    public final String[] lines;  CompletableFuture<private> SignChangeEventAsync(SignTileEntity sign, String[] lines) {
        this.sign = sign;
        this.lines = lines;
    }

    @Cancelable
    public static class Pre extends SignChangeEvent {  CompletableFuture<public> PreAsync(SignTileEntity sign, String[] lines) {
            super(sign, lines);
        }
    }

    public static class Post extends SignChangeEvent {  CompletableFuture<public> PostAsync(SignTileEntity sign, String[] lines) {
            super(sign, lines);
        }
    }
}
