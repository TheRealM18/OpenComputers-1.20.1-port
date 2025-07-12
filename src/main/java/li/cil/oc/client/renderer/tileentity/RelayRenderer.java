package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.RelayTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RelayRenderer implements BlockEntityRenderer<RelayTileEntity> {
    // Scale factor to avoid z-fighting with the block model
    private static final float SCALE = 1.0025f;
    // Duration of the activity indicator in milliseconds
    private static final long ACTIVITY_DURATION = 1000;
    
    public RelayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RelayTileEntity relay, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (relay == null || !relay.hasLevel()) {
            return;
        }

        // Calculate activity level (1.0 when just activated, fading to 0.0 over ACTIVITY_DURATION)
        double activity = Math.max(0, 1 - (System.currentTimeMillis() - relay.lastMessage()) / (double) ACTIVITY_DURATION);
        if (activity <= 0) {
            return; // No activity to render
        }

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.scale(SCALE, -SCALE, SCALE); // Negative Y scale to flip the texture
        stack.translate(-0.5f, -0.5f, -0.5f);

        // Get the overlay texture
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);
        var icon = Textures.getSprite(Textures.Block.SWITCH_SIDE_ON);

        // Front face (Z-)
        vertexBuilder.vertex(stack.last().pose(), 1, 1, 0)
            .uv(icon.getU0(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 1, 0)
            .uv(icon.getU1(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 0, 0)
            .uv(icon.getU1(), icon.getV0())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 0, 0)
            .uv(icon.getU0(), icon.getV0())
            .endVertex();

        // Back face (Z+)
        vertexBuilder.vertex(stack.last().pose(), 0, 1, 1)
            .uv(icon.getU0(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 1, 1)
            .uv(icon.getU1(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 0, 1)
            .uv(icon.getU1(), icon.getV0())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 0, 1)
            .uv(icon.getU0(), icon.getV0())
            .endVertex();

        // Right face (X+)
        vertexBuilder.vertex(stack.last().pose(), 1, 1, 1)
            .uv(icon.getU0(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 1, 0)
            .uv(icon.getU1(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 0, 0)
            .uv(icon.getU1(), icon.getV0())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 1, 0, 1)
            .uv(icon.getU0(), icon.getV0())
            .endVertex();

        // Left face (X-)
        vertexBuilder.vertex(stack.last().pose(), 0, 1, 0)
            .uv(icon.getU0(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 1, 1)
            .uv(icon.getU1(), icon.getV1())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 0, 1)
            .uv(icon.getU1(), icon.getV0())
            .endVertex();
        vertexBuilder.vertex(stack.last().pose(), 0, 0, 0)
            .uv(icon.getU0(), icon.getV0())
            .endVertex();

        stack.popPose();
    }
}

// Factory class for the renderer
class RelayRendererProvider implements BlockEntityRendererProvider<RelayTileEntity> {
    @Override
    public BlockEntityRenderer<RelayTileEntity> create(Context context) {
        return new RelayRenderer(context);
    }
}
