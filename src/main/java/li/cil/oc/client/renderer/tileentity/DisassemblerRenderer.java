package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.DisassemblerTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisassemblerRenderer implements BlockEntityRenderer<DisassemblerTileEntity> {
    // Slight scale factor to avoid z-fighting with the block model
    private static final float SCALE = 1.0025f;
    
    public DisassemblerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(DisassemblerTileEntity disassembler, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (disassembler == null || !disassembler.hasLevel() || !disassembler.isActive()) {
            return;
        }

        stack.pushPose();
        
        // Position and scale the model slightly larger than the block to avoid z-fighting
        stack.translate(0.5, 0.5, 0.5);
        stack.scale(SCALE, -SCALE, SCALE);
        stack.translate(-0.5f, -0.5f, -0.5f);

        // Get the overlay buffer
        VertexConsumer overlayBuffer = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);
        
        // Get the sprites for the active state
        var topSprite = Textures.getSprite(Textures.Block.DisassemblerTopOn);
        var sideSprite = Textures.getSprite(Textures.Block.DisassemblerSideOn);
        
        // Render the top face
        renderTopFace(stack, overlayBuffer, topSprite, combinedLight);
        
        // Render the side faces
        renderSideFaces(stack, overlayBuffer, sideSprite, combinedLight);
        
        stack.popPose();
    }
    
    /**
     * Renders the top face of the disassembler.
     */
    private void renderTopFace(PoseStack stack, VertexConsumer buffer, 
                             net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, 
                             int light) {
        final var pose = stack.last().pose();
        
        // Top face (facing up)
        buffer.vertex(pose, 0, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .normal(0, 1, 0)
            .endVertex();
        buffer.vertex(pose, 1, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .normal(0, 1, 0)
            .endVertex();
        buffer.vertex(pose, 1, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .normal(0, 1, 0)
            .endVertex();
        buffer.vertex(pose, 0, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .normal(0, 1, 0)
            .endVertex();
    }
    
    /**
     * Renders the four side faces of the disassembler.
     */
    private void renderSideFaces(PoseStack stack, VertexConsumer buffer, 
                               net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, 
                               int light) {
        final var pose = stack.last().pose();
        
        // North face (facing negative Z)
        buffer.vertex(pose, 1, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .normal(0, 0, -1)
            .endVertex();
        buffer.vertex(pose, 0, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .normal(0, 0, -1)
            .endVertex();
        buffer.vertex(pose, 0, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .normal(0, 0, -1)
            .endVertex();
        buffer.vertex(pose, 1, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .normal(0, 0, -1)
            .endVertex();

        // South face (facing positive Z)
        buffer.vertex(pose, 0, 1, 1).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 1, 1, 1).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 1, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
        buffer.vertex(pose, 0, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .normal(0, 0, 1)
            .endVertex();
            
        // West face (facing negative X)
        buffer.vertex(pose, 0, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .normal(-1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 0, 1, 1).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .normal(-1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 0, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .normal(-1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 0, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .normal(-1, 0, 0)
            .endVertex();
            
        // East face (facing positive X)
        buffer.vertex(pose, 1, 1, 1).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(light)
            .normal(1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 1, 1, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(light)
            .normal(1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 1, 0, 0).color(1, 1, 1, 1)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(light)
            .normal(1, 0, 0)
            .endVertex();
        buffer.vertex(pose, 1, 0, 1).color(1, 1, 1, 1)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(light)
            .normal(1, 0, 0)
            .endVertex();
    }
}

// Factory class for the renderer
class DisassemblerRendererProvider implements BlockEntityRendererProvider<DisassemblerTileEntity> {
    @Override
    public BlockEntityRenderer<DisassemblerTileEntity> create(Context context) {
        return new DisassemblerRenderer(context);
    }
}
