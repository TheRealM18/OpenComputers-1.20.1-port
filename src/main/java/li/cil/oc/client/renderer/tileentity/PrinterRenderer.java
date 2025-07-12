package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import li.cil.oc.client.Textures;
import li.cil.oc.common.tileentity.PrinterTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrinterRenderer implements BlockEntityRenderer<PrinterTileEntity> {
    // Scale factor for the rendered item
    private static final float ITEM_SCALE = 0.75f;
    // Vertical offset for the rendered item
    private static final float ITEM_Y_OFFSET = 0.3f;
    // Rotation speed (degrees per millisecond)
    private static final float ROTATION_SPEED = 0.018f;
    
    public PrinterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PrinterTileEntity printer, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (printer == null || !printer.hasLevel() || printer.data().stateOff().isEmpty()) {
            return;
        }

        // Create the item stack to render
        ItemStack paperStack = printer.data().createItemStack();
        if (paperStack.isEmpty()) {
            return;
        }

        stack.pushPose();
        
        // Position the item above the center of the block
        stack.translate(0.5, 0.5 + ITEM_Y_OFFSET, 0.5);
        
        // Rotate the item continuously
        float rotation = (System.currentTimeMillis() % 20000) * ROTATION_SPEED;
        stack.mulPose(Axis.YP.rotationDegrees(rotation));
        
        // Scale the item down
        stack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        
        // Bind the item texture and render the item
        Textures.Block.bind();
        Minecraft.getInstance().getItemRenderer().renderStatic(
            paperStack, 
            ItemDisplayContext.FIXED, 
            combinedLight, 
            combinedOverlay, 
            stack, 
            buffer, 
            printer.getLevel(), 
            0
        );
        
        stack.popPose();
    }
}

// Factory class for the renderer
class PrinterRendererProvider implements BlockEntityRendererProvider<PrinterTileEntity> {
    @Override
    public BlockEntityRenderer<PrinterTileEntity> create(Context context) {
        return new PrinterRenderer(context);
    }
}
