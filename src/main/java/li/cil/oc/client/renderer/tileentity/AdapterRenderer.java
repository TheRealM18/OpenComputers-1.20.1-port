package li.cil.oc.client.renderer.tileentity;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import li.cil.oc.client.Textures;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.tileentity.AdapterTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdapterRenderer implements BlockEntityRenderer<AdapterTileEntity> {
 CompletableFuture<public> AdapterRendererAsync(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AdapterTileEntity adapter, float partialTicks, PoseStack stack, 
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (adapter == null || !adapter.hasLevel()) {
            return;
        }

        // Check if any side is open
        boolean hasOpenSide = false;
        for (Direction dir : Direction.values()) {
            if (adapter.isSideOpen(dir)) {
                hasOpenSide = true;
                break;
            }
        }
        if (!hasOpenSide) {
            return;
        }

        stack.pushPose();

        // Position and scale the model
        stack.translate(0.5, 0.5, 0.5);
        stack.scale(1.0025f, -1.0025f, 1.0025f);
        stack.translate(-0.5f, -0.5f, -0.5f);

        // Get the overlay texture
        final var sideActivity = Textures.getSprite(Textures.Block.AdapterOn);
        final VertexConsumer r = buffer.getBuffer(RenderTypes.BLOCK_OVERLAY);

        // Render each open side
        if (adapter.isSideOpen(Direction.DOWN)) {
            renderSide(r, stack, sideActivity, Direction.DOWN, combinedLight);
        }
        if (adapter.isSideOpen(Direction.UP)) {
            renderSide(r, stack, sideActivity, Direction.UP, combinedLight);
        }
        if (adapter.isSideOpen(Direction.NORTH)) {
            renderSide(r, stack, sideActivity, Direction.NORTH, combinedLight);
        }
        if (adapter.isSideOpen(Direction.SOUTH)) {
            renderSide(r, stack, sideActivity, Direction.SOUTH, combinedLight);
        }
        if (adapter.isSideOpen(Direction.WEST)) {
            renderSide(r, stack, sideActivity, Direction.WEST, combinedLight);
        }
        if (adapter.isSideOpen(Direction.EAST)) {
            renderSide(r, stack, sideActivity, Direction.EAST, combinedLight);
        }

        stack.popPose();
    }

 CompletableFuture<Void> renderSideAsync(VertexConsumer buffer, PoseStack stack, net.minecraft.client.renderer.texture.TextureAtlasSprite texture, 
                           Direction side, int combinedLight) {
        final var pose = stack.last().pose();
        final var normal = side.step();
        
        // Get texture coordinates
        final float u0 = texture.getU0();
        final float u1 = texture.getU1();
        final float v0 = texture.getV0();
        final float v1 = texture.getV1();
        
        // Slightly offset the face to avoid z-fighting
        final float offset = 0.001f;
        
        switch (side) {
            case DOWN -> {
                buffer.vertex(pose, 0, offset, 0).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, offset, 0).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, offset, 1).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 0, offset, 1).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
            case UP -> {
                buffer.vertex(pose, 0, 1 - offset, 0).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 0, 1 - offset, 1).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, 1 - offset, 1).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, 1 - offset, 0).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
            case NORTH -> {
                buffer.vertex(pose, 1, 0, offset).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 0, 0, offset).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 0, 1, offset).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, 1, offset).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
            case SOUTH -> {
                buffer.vertex(pose, 0, 0, 1 - offset).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, 0, 1 - offset).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1, 1, 1 - offset).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 0, 1, 1 - offset).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
            case WEST -> {
                buffer.vertex(pose, offset, 0, 0).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, offset, 0, 1).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, offset, 1, 1).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, offset, 1, 0).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
            case EAST -> {
                buffer.vertex(pose, 1 - offset, 0, 1).color(1, 1, 1, 1).uv(u1, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1 - offset, 0, 0).color(1, 1, 1, 1).uv(u0, v1).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1 - offset, 1, 0).color(1, 1, 1, 1).uv(u0, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
                buffer.vertex(pose, 1 - offset, 1, 1).color(1, 1, 1, 1).uv(u1, v0).uv2(combinedLight).normal(normal.x, normal.y, normal.z).endVertex();
            }
        }
    }
}

// Factory class for the renderer
class AdapterRendererProvider implements BlockEntityRendererProvider<AdapterTileEntity> {
    @Override
    public BlockEntityRenderer<AdapterTileEntity> create(Context context) {
        return CompletableFuture<new> AdapterRendererAsync(context);
    }
}
