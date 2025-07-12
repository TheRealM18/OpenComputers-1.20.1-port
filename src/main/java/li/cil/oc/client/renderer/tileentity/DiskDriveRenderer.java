package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import li.cil.oc.common.tileentity.DiskDriveTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiskDriveRenderer implements BlockEntityRenderer<DiskDriveTileEntity> {
    // Offset and scale for the disk item rendering
    private static final float DISK_Y_OFFSET = 3.5f / 16f;
    private static final float DISK_Z_OFFSET = 6f / 16f;
    private static final float DISK_SCALE = 0.5f;
    
    private final ItemRenderer itemRenderer;
    
    public DiskDriveRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(DiskDriveTileEntity drive, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (drive == null || !drive.hasLevel()) {
            return;
        }

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);

        // Apply rotation based on the drive's yaw (facing direction)
        Direction yaw = drive.yaw();
        if (yaw != null) {
            switch (yaw) {
                case WEST -> stack.mulPose(Axis.YP.rotationDegrees(-90));
                case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
                case EAST -> stack.mulPose(Axis.YP.rotationDegrees(90));
                default -> { /* No yaw */ }
            }
        }

        // Render the disk item if present in the first slot
        ItemStack diskStack = drive.getItem(0);
        if (!diskStack.isEmpty()) {
            stack.pushPose();
            // Position the disk item
            stack.translate(0, DISK_Y_OFFSET, DISK_Z_OFFSET);
            // Rotate to lay flat
            stack.mulPose(Axis.XN.rotationDegrees(90));
            // Scale down the item
            stack.scale(DISK_SCALE, DISK_SCALE, DISK_SCALE);
            
            // Render the item
            itemRenderer.renderStatic(diskStack, ItemDisplayContext.FIXED, combinedLight, 
                                   combinedOverlay, stack, buffer, drive.getLevel(), 0);
            
            stack.popPose();
        }
        
        // Render the activity indicator if the drive is active
        if (drive.isActive()) {
            renderActivityIndicator(stack, buffer, combinedLight, combinedOverlay);
        }
        
        stack.popPose();
    }
    
    /**
     * Renders the activity indicator on the front of the disk drive.
     */
    private void renderActivityIndicator(PoseStack stack, MultiBufferSource buffer, 
                                       int combinedLight, int combinedOverlay) {
        // Implementation would go here if there's a visual indicator to render
        // Currently, the original Scala code doesn't show any indicator rendering,
        // but we've left this method here for consistency with other renderers
        // and in case it's needed in the future.
    }
}

// Factory class for the renderer
class DiskDriveRendererProvider implements BlockEntityRendererProvider<DiskDriveTileEntity> {
    @Override
    public BlockEntityRenderer<DiskDriveTileEntity> create(Context context) {
        return new DiskDriveRenderer(context);
    }
}
