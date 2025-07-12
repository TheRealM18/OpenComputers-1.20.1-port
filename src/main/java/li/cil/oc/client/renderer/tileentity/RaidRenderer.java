package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.RaidTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidRenderer implements BlockEntityRenderer<RaidTileEntity> {
    // Texture coordinates for slot positioning
    private static final float U1 = 2 / 16f;
    // Size of each disk slot
    private static final float SLOT_SIZE = 4 / 16f;
    // Z offset to avoid z-fighting with the block model
    private static final float Z_OFFSET = 0.505f;
    
    public RaidRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RaidTileEntity raid, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (raid == null || !raid.hasLevel()) {
            return;
        }

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);

        // Apply rotation based on the raid controller's yaw (facing direction)
        Direction yaw = raid.yaw();
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

        VertexConsumer vertexBuilder = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);

        // Render error indicators for empty slots
        TextureAtlasSprite errorIcon = Textures.getSprite(Textures.Block.RAID_FRONT_ERROR);
        for (int slot = 0; slot < raid.getContainerSize(); slot++) {
            if (!raid.presence(slot)) {
                renderSlot(stack, vertexBuilder, slot, errorIcon);
            }
        }

        // Render activity indicators
        if (System.currentTimeMillis() - raid.lastAccess() < 400) {
            TextureAtlasSprite activityIcon = Textures.getSprite(Textures.Block.RAID_FRONT_ACTIVITY);
            int activeSlot = (int) (raid.lastAccess() % raid.getContainerSize());
            if (raid.getLevel().getRandom().nextDouble() > 0.1) {
                renderSlot(stack, vertexBuilder, activeSlot, activityIcon);
            }
        }

        stack.popPose();
    }

    private void renderSlot(PoseStack stack, VertexConsumer vertexBuilder, int slot, TextureAtlasSprite icon) {
        float left = U1 + slot * SLOT_SIZE;
        float right = U1 + (slot + 1) * SLOT_SIZE;
        
        // Top-left vertex
        vertexBuilder.vertex(stack.last().pose(), left, 1, 0)
            .uv(icon.getU(left * 16), icon.getV1())
            .endVertex();
            
        // Top-right vertex
        vertexBuilder.vertex(stack.last().pose(), right, 1, 0)
            .uv(icon.getU(right * 16), icon.getV1())
            .endVertex();
            
        // Bottom-right vertex
        vertexBuilder.vertex(stack.last().pose(), right, 0, 0)
            .uv(icon.getU(right * 16), icon.getV0())
            .endVertex();
            
        // Bottom-left vertex
        vertexBuilder.vertex(stack.last().pose(), left, 0, 0)
            .uv(icon.getU(left * 16), icon.getV0())
            .endVertex();
    }
}

// Factory class for the renderer
class RaidRendererProvider implements BlockEntityRendererProvider<RaidTileEntity> {
    @Override
    public BlockEntityRenderer<RaidTileEntity> create(Context context) {
        return new RaidRenderer(context);
    }
}
