package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.ChargerTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChargerRenderer implements BlockEntityRenderer<ChargerTileEntity> {
    // Slight offset to avoid z-fighting
    private static final float OFFSET = 0.005f;
    
    public ChargerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ChargerTileEntity charger, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (charger == null || !charger.hasLevel()) {
            return;
        }

        // Only render the charging effect if the charger is active
        if (charger.chargeSpeed() > 0) {
            stack.pushPose();
            stack.translate(0.5, 0.5, 0.5);

            // Apply rotation based on the charger's yaw (facing direction)
            Direction yaw = charger.yaw();
            if (yaw != null) {
                switch (yaw) {
                    case WEST -> stack.mulPose(Axis.YP.rotationDegrees(-90));
                    case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
                    case EAST -> stack.mulPose(Axis.YP.rotationDegrees(90));
                    default -> { /* No yaw */ }
                }
            }

            // Position and prepare for rendering the front overlay
            stack.translate(-0.5f, 0.5f, 0.5f);
            stack.scale(1, -1, 1);

            // Get the overlay buffer
            VertexConsumer overlayBuffer = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);
            
            // Calculate the charge level for the animation (inverse of charge speed)
            float inverse = 1 - charger.chargeSpeed();
            
            // Get the charging texture
            var icon = Textures.getSprite(Textures.Block.ChargerFrontOn);
            
            // Render the charging bar
            renderChargingBar(stack, overlayBuffer, icon, inverse, combinedLight);
            
            stack.popPose();
        }
    }
    
    /**
     * Renders the charging bar on the front of the charger.
     * 
     * @param stack The pose stack
     * @param buffer The vertex buffer
     * @param icon The sprite to use for the charging bar
     * @param chargeLevel The current charge level (0 = empty, 1 = full)
     * @param light The light level
     */
    private void renderChargingBar(PoseStack stack, VertexConsumer buffer, 
                                 net.minecraft.client.renderer.texture.TextureAtlasSprite icon,
                                 float chargeLevel, int light) {
        final var pose = stack.last().pose();
        
        // Calculate the V texture coordinates based on the charge level
        float v0 = icon.getV(chargeLevel * 16);
        float v1 = icon.getV1();
        
        // Front face (facing positive Z)
        buffer.vertex(pose, 0, 1, OFFSET).color(1, 1, 1, 1)
            .uv(icon.getU0(), v1)
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 1, 1, OFFSET).color(1, 1, 1, 1)
            .uv(icon.getU1(), v1)
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 1, chargeLevel, OFFSET).color(1, 1, 1, 1)
            .uv(icon.getU1(), v0)
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 0, chargeLevel, OFFSET).color(1, 1, 1, 1)
            .uv(icon.getU0(), v0)
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
    }
}

// Factory class for the renderer
class ChargerRendererProvider implements BlockEntityRendererProvider<ChargerTileEntity> {
    @Override
    public BlockEntityRenderer<ChargerTileEntity> create(Context context) {
        return new ChargerRenderer(context);
    }
}
