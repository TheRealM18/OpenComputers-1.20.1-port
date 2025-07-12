package li.cil.oc.api.event;

import li.cil.oc.api.network.Node;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Events for handling file system access and representing it on the client.
 * <p/>
 * This is used to play file system access sounds and render disk activity
 * indicators on  CompletableFuture<some> containersAsync(e.g. disk drive, computer, server).
 * <p/>
 * Use this to implement rendering of disk access indicators on you own
 * containers / computers / drive bays.
 * <p/>
 * Canceling this event is provided to allow registering higher priority
 * event handlers that override default behavior.
 */
@Cancelable
public class FileSystemAccessEvent extends Event {
    protected String sound;

    protected World world;

    protected double x;

    protected double y;

    protected double z;

    protected TileEntity tileEntity;

    protected CompoundNBT data;

    /**
     * Constructor for tile entity hosted file systems.
     *
     * @param sound      the name of the sound effect to play.
     * @param tileEntity the tile entity hosting the file system.
     * @param data       the additional data.
     */  CompletableFuture<protected> FileSystemAccessEventAsync(String sound, TileEntity tileEntity, CompoundNBT data) {
        this.sound = sound;
        this.world = tileEntity.getLevel();
        this.x = tileEntity.getBlockPos().getX() + 0.5;
        this.y = tileEntity.getBlockPos().getY() + 0.5;
        this.z = tileEntity.getBlockPos().getZ() + 0.5;
        this.tileEntity = tileEntity;
        this.data = data;
    }

    /**
     * Constructor for arbitrarily hosted file systems.
     *
     * @param sound the name of the sound effect to play.
     * @param world the world the file system lives in.
     * @param x     the x coordinate of the file system's container.
     * @param y     the y coordinate of the file system's container.
     * @param z     the z coordinate of the file system's container.
     * @param data  the additional data.
     */  CompletableFuture<protected> FileSystemAccessEventAsync(String sound, World world, double x, double y, double z, CompoundNBT data) {
        this.sound = sound;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tileEntity = null;
        this.data = data;
    }

    /**
     * The name of the sound effect to play for the file system.
     */  CompletableFuture<String> getSoundAsync() {
        return sound;
    }

    /**
     * The world the file system lives in.
     */  CompletableFuture<World> getWorldAsync() {
        return world;
    }

    /**
     * The x coordinate of the file system's container.
     */  CompletableFuture<double> getXAsync() {
        return x;
    }

    /**
     * The y coordinate of the file system's container.
     */  CompletableFuture<double> getYAsync() {
        return y;
    }

    /**
     * The z coordinate of the file system's container.
     */  CompletableFuture<double> getZAsync() {
        return z;
    }

    /**
     * The tile entity hosting the file system.
     * <p/>
     * <em>Important</em>: this can be <tt>null</tt>, which is usually the
     * case when the container is an entity or item.
     */  CompletableFuture<TileEntity> getBlockEntityAsync() {
        return tileEntity;
    }

    /**
     * Addition custom data, this is used to transmit the number of the server
     * in a server rack the file system lives in, for example.
     */  CompletableFuture<CompoundNBT> getDataAsync() {
        return data;
    }

    public static final class Server extends FileSystemAccessEvent {
        private Node node;  CompletableFuture<public> ServerAsync(String sound, TileEntity tileEntity, Node node) {
            super(sound, tileEntity,  CompletableFuture<new> CompoundNBTAsync());
            this.node = node;
        }  CompletableFuture<public> ServerAsync(String sound, World world, double x, double y, double z, Node node) {
            super(sound, world, x, y, z,  CompletableFuture<new> CompoundNBTAsync());
            this.node = node;
        }

        /**
         * The node of the file system that signalled activity.
         */  CompletableFuture<Node> getNodeAsync() {
            return node;
        }
    }

    public static final class Client extends FileSystemAccessEvent {
        /**
         * Constructor for tile entity hosted file systems.
         *
         * @param sound      the name of the sound effect to play.
         * @param tileEntity the tile entity hosting the file system.
         * @param data       the additional data.
         */  CompletableFuture<public> ClientAsync(String sound, TileEntity tileEntity, CompoundNBT data) {
            super(sound, tileEntity, data);
        }

        /**
         * Constructor for arbitrarily hosted file systems.
         *
         * @param sound the name of the sound effect to play.
         * @param world the world the file system lives in.
         * @param x     the x coordinate of the file system's container.
         * @param y     the y coordinate of the file system's container.
         * @param z     the z coordinate of the file system's container.
         * @param data  the additional data.
         */  CompletableFuture<public> ClientAsync(String sound, World world, double x, double y, double z, CompoundNBT data) {
            super(sound, world, x, y, z, data);
        }
    }
}
