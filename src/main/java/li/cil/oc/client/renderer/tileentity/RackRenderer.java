package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import li.cil.oc.api.event.RackMountableRenderEvent;
import li.cil.oc.common.tileentity.RackTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RackRenderer implements BlockEntityRenderer<RackTileEntity> {
    // Vertical offset for the first slot
    private static final float VERTICAL_OFFSET = 2 / 16f;
    // Height of each slot
    private static final float SLOT_HEIGHT = 3 / 16f;
    // Z offset to avoid z-fighting with the block model
    private static final float Z_OFFSET = 0.505f - 0.5f / 16f;
    
    public RackRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RackTileEntity rack, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (rack == null || !rack.hasLevel()) {
            return;
        }

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);

        // Apply rotation based on the rack's yaw (facing direction)
        Direction yaw = rack.yaw();
        if (yaw != null) {
            switch (yaw) {
                case WEST -> stack.mulPose(Axis.YP.rotationDegrees(-90));
                case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
                case EAST -> stack.mulPose(Axis.YP.rotationDegrees(90));
                default -> { /* No yaw */ }
            }
        }

        // Position and prepare for rendering the front overlay
        stack.translate(-0.5, 0.5, Z_OFFSET);
        stack.scale(1, -1, 1);

        // Note: we manually sync the rack inventory for this to work.
        for (int i = 0; i < rack.getContainerSize(); i++) {
            if (!rack.getItem(i).isEmpty()) {
                float v0 = VERTICAL_OFFSET + i * SLOT_HEIGHT;
                float v1 = VERTICAL_OFFSET + (i + 1) * SLOT_HEIGHT;
                
                // Fire event for mountable rendering
                RackMountableRenderEvent.TileEntity event = new RackMountableRenderEvent.TileEntity(
                    rack, i, rack.lastData[i], stack, buffer, combinedLight, combinedOverlay, v0, v1
                );
                MinecraftForge.EVENT_BUS.post(event);
            }
        }

        stack.popPose();
    }
}

// Factory class for the renderer
class RackRendererProvider implements BlockEntityRendererProvider<RackTileEntity> {
    @Override
    public BlockEntityRenderer<RackTileEntity> create(Context context) {
        return new RackRenderer(context);
    }
}
