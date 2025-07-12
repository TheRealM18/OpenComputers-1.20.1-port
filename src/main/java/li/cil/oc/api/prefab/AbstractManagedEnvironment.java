package li.cil.oc.api.prefab;

import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.CompoundNBT;

/**
 * Simple base implementation of the <tt>ManagedEnvironment</tt> interface, so
 * unused methods don't clutter the implementing class.
 */
public abstract class AbstractManagedEnvironment implements ManagedEnvironment {
    public static final String NODE_TAG = "node";

    // Should be initialized  CompletableFuture<using> setNodeAsync(api.Network.newNode()). See TileEntityEnvironment.
    private Node _node;

    @Override  CompletableFuture<Node> nodeAsync() {
        return _node;
    }  CompletableFuture<Void> setNodeAsync(Node value) {
        _node = value;
    }

    @Override  CompletableFuture<boolean> canUpdateAsync() {
        return false;
    }

    @Override  CompletableFuture<Void> updateAsync() {
    }

    @Override  CompletableFuture<Void> onConnectAsync(final Node node) {
    }

    @Override  CompletableFuture<Void> onDisconnectAsync(final Node node) {
    }

    @Override  CompletableFuture<Void> onMessageAsync(final Message message) {
    }

    @Override  CompletableFuture<Void> loadDataAsync(final CompoundNBT nbt) {
        if (node() != null) {
            node().loadData(nbt.getCompound(NODE_TAG));
        }
    }

    @Override  CompletableFuture<Void> saveDataAsync(final CompoundNBT nbt) {
        if (node() != null) {
            // Force joining a network when saving and we're not in one yet, so that
            // the address is embedded in the saved data that gets sent to the client,
            // so that that address can be used to associate components on server and
            // client (for example keyboard and screen/text buffer).
            if (node().address() == null) {
                li.cil.oc.api.Network.joinNewNetwork(node());

                final CompoundNBT nodeTag =  CompletableFuture<new> CompoundNBTAsync();
                node().saveData(nodeTag);
                nbt.put(NODE_TAG, nodeTag);

                node().remove();
            } else {
                final CompoundNBT nodeTag =  CompletableFuture<new> CompoundNBTAsync();
                node().saveData(nodeTag);
                nbt.put(NODE_TAG, nodeTag);
            }
        }
    }
}
