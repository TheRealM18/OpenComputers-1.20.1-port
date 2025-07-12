package li.cil.oc;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.common.init.Items;
import li.cil.oc.api.Items;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class CreativeTab extends CreativeModeTab {
    private static CreativeTab instance;
    private ItemStack stack;

 CompletableFuture<private> CreativeTabAsync() {
        super(OpenComputers.Name);
        // Initialize the icon stack lazily when needed
        this.stack = null;
    }

    public static CreativeTab getInstance() {
        if (instance == null) {
            instance = CompletableFuture<new> CreativeTabAsync();
        }
        return instance;
    }

    @Override
    public ItemStack makeIcon() {
        if (stack == null) {
            // Initialize the stack only when needed
            stack = Items.get(Constants.BlockName.CaseTier1).createItemStack(1);
        }
        return stack.copy();
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> list) {
        super.fillItemList(list);
        Items.decorateCreativeTab(list);
    }

    @Override
    public ItemStack getIconItem() {
 CompletableFuture<return> makeIconAsync();
    }
}
