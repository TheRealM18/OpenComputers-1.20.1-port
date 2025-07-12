package li.cil.oc.client;

import li.cil.oc.Constants;
import li.cil.oc.api.Items;
import li.cil.oc.api.internal.Colored;
import li.cil.oc.common.block.Cable;
import li.cil.oc.common.block.Case;
import li.cil.oc.common.block.ChameliumBlock;
import li.cil.oc.common.block.Screen;
import li.cil.oc.util.Color;
import li.cil.oc.util.ItemColorizer;
import li.cil.oc.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class ColorHandler {
    private ColorHandler() {
    }

    public static void init() {
        // Register block colors
        register((state, world, pos, tintIndex) -> {
            if (state.getBlock() instanceof Cable cable) {
                return cable.colorMultiplierOverride.orElse(0xFFFFFFFF);
            }
            return 0xFFFFFFFF;
        }, Items.get(Constants.BlockName.Cable).block());

        register((state, world, pos, tintIndex) -> {
            if (pos == null) return 0xFFFFFFFF;
            BlockEntity blockEntity = world != null ? world.getBlockEntity(pos) : null;
            if (blockEntity instanceof Colored colored) {
                return colored.getColor();
            }
            if (state.getBlock() instanceof Case caseBlock) {
                return Color.rgbValues[Color.byTier(caseBlock.tier())];
            }
            return 0xFFFFFFFF;
        }, 
        Items.get(Constants.BlockName.CaseTier1).block(),
        Items.get(Constants.BlockName.CaseTier2).block(),
        Items.get(Constants.BlockName.CaseTier3).block(),
        Items.get(Constants.BlockName.CaseCreative).block());

        register((state, world, pos, tintIndex) -> 
            Color.rgbValues[state.getValue(ChameliumBlock.COLOR)],
            Items.get(Constants.BlockName.ChameliumBlock).block());

        register((state, world, pos, tintIndex) -> tintIndex,
            Items.get(Constants.BlockName.Print).block());

        register((state, world, pos, tintIndex) -> {
            if (state.getBlock() instanceof Screen screen) {
                return Color.rgbValues[Color.byTier(screen.tier())];
            }
            return 0xFFFFFFFF;
        },
        Items.get(Constants.BlockName.ScreenTier1).block(),
        Items.get(Constants.BlockName.ScreenTier2).block(),
        Items.get(Constants.BlockName.ScreenTier3).block());

        // Register item colors
        register((stack, tintIndex) -> 
            ItemColorizer.hasColor(stack) ? ItemColorizer.getColor(stack) : tintIndex,
            Items.get(Constants.BlockName.Cable).block());

        register((stack, tintIndex) -> 
            Color.rgbValues[Color.byTier(ItemUtils.caseTier(stack))],
            Items.get(Constants.BlockName.CaseTier1).block(),
            Items.get(Constants.BlockName.CaseTier2).block(),
            Items.get(Constants.BlockName.CaseTier3).block(),
            Items.get(Constants.BlockName.CaseCreative).block());

        register((stack, tintIndex) -> 
            Color.rgbValues[DyeColor.byId(stack.getDamageValue()).getId()],
            Items.get(Constants.BlockName.ChameliumBlock).block());

        register((stack, tintIndex) -> tintIndex,
            Items.get(Constants.BlockName.ScreenTier1).block(),
            Items.get(Constants.BlockName.ScreenTier2).block(),
            Items.get(Constants.BlockName.ScreenTier3).block(),
            Items.get(Constants.BlockName.Print).block(),
            Items.get(Constants.BlockName.Robot).block());

        register((stack, tintIndex) -> 
            tintIndex == 1 ? 
                (ItemColorizer.hasColor(stack) ? ItemColorizer.getColor(stack) : 0x66DD55) : 
                0xFFFFFF,
            Items.get(Constants.ItemName.HoverBoots).item());
    }

    public static void register(BlockColor handler, Block... blocks) {
        Minecraft.getInstance().getBlockColors().register(handler, blocks);
    }

    public static void register(ItemColor handler, Block... blocks) {
        Minecraft.getInstance().getItemColors().register(handler, blocks);
    }

    public static void register(ItemColor handler, net.minecraft.world.item.Item... items) {
        Minecraft.getInstance().getItemColors().register(handler, items);
    }

    @FunctionalInterface
    public interface BlockColorHandler extends BlockColor {
        @Override
        default int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintIndex) {
            return getColor0(state, world, pos, tintIndex);
        }

        int getColor0(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintIndex);
    }

    @FunctionalInterface
    public interface ItemColorHandler extends ItemColor {
        @Override
        default int getColor(ItemStack stack, int tintIndex) {
            return getColor0(stack, tintIndex);
        }

        int getColor0(ItemStack stack, int tintIndex);
    }
}
