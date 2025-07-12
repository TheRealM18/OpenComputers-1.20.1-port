package li.cil.oc.client.renderer.tileentity;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.CaseTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaseRenderer implements BlockEntityRenderer<CaseTileEntity> {
    // Slight offset to avoid z-fighting
    private static final float OFFSET = 0.005f;
    // Threshold for showing activity indicator (in milliseconds)
    private static final long ACTIVITY_THRESHOLD = 400;
    
 CompletableFuture<public> CaseRendererAsync(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CaseTileEntity computer, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (computer == null || !computer.hasLevel()) {
            return;
        }

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);

        // Apply rotation based on the computer's yaw (facing direction)
        Direction yaw = computer.yaw();
        if (yaw != null) {
            switch (yaw) {
                case WEST -> stack.mulPose(Axis.YP.rotationDegrees(-90));
                case NORTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
                case EAST -> stack.mulPose(Axis.YP.rotationDegrees(90));
                default -> { /* No yaw */ }
            }
        }

        // Position and prepare for rendering the front overlay
        stack.translate(-0.5, 0.5, 0.5 + OFFSET);
        stack.scale(1, -1, 1);

        // Get the overlay buffer
        VertexConsumer overlayBuffer = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);
        
        // Render the appropriate overlay based on the computer's state
        if (computer.isRunning()) {
            // Show the "on" indicator
            renderFrontOverlay(stack, overlayBuffer, Textures.getSprite(Textures.Block.CaseFrontOn));
            
            // Show activity indicator if there was recent filesystem access
            if (System.currentTimeMillis() - computer.lastFileSystemAccess() < ACTIVITY_THRESHOLD && 
                computer.getLevel().random.nextDouble() > 0.1) {
                renderFrontOverlay(stack, overlayBuffer, Textures.getSprite(Textures.Block.CaseFrontActivity));
            }
        } 
        // Show error indicator if the computer has an error
        else if (computer.hasErrored() && shouldShowErrorLight(computer.hashCode())) {
            renderFrontOverlay(stack, overlayBuffer, Textures.getSprite(Textures.Block.CaseFrontError));
        }

        stack.popPose();
    }
    
    /**
     * Renders an overlay texture on the front of the case.
     */
 CompletableFuture<Void> renderFrontOverlayAsync(PoseStack stack, VertexConsumer buffer, 
                                  net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
        final var pose = stack.last().pose();
        
        // Front face (facing positive Z)
        buffer.vertex(pose, 1, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(15728880) // Full brightness
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 1, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(15728880)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 0, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(15728880)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 0, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(15728880)
            .normal(0, 0, 1)
            .endVertex();
    }
    
    /**
     * Determines if the error light should be shown for a computer with the given hash code.
     * This creates a pulsing effect for the error light.
     */
 CompletableFuture<boolean> shouldShowErrorLightAsync(int hashCode) {
        // Use the hash code to create a deterministic but unique pattern
        long time = System.currentTimeMillis() + (hashCode % 1000);
        // Create a pulsing effect with a sine wave
        return Math.sin(time / 500.0) > 0;
    }
}

// Factory class for the renderer
class CaseRendererProvider implements BlockEntityRendererProvider<CaseTileEntity> {
    @Override
    public BlockEntityRenderer<CaseTileEntity> create(Context context) {
        return CompletableFuture<new> CaseRendererAsync(context);
    }
}
