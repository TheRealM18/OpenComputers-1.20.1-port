package li.cil.oc.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import li.cil.oc.Constants;
import li.cil.oc.Settings;
import li.cil.oc.api.Items;
import li.cil.oc.client.Textures;
import li.cil.oc.util.BlockPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public final class HighlightRenderer {
    private static final Random RANDOM = new Random();
    private static final ItemStack TABLET = Items.get(Constants.ItemName.TABLET).createItemStack(1);
    private static final RenderType HOLOGRAM_EFFECT = RenderType.create(
        "hologram_effect",
        DefaultVertexFormats.POSITION_COLOR_TEX,
        com.mojang.blaze3d.vertex.IVertexBuilder.Mode.QUADS,
        256,
        false,
        true,
        RenderType.State.builder()
            .setTextureState(new RenderState.TextureState(Textures.Model.HOLOGRAM_EFFECT, false, false))
            .setTransparencyState(RenderState.TRANSLUCENT_TRANSPARENCY)
            .setLightmapState(RenderState.LIGHTMAP)
            .setWriteMaskState(RenderState.COLOR_WRITE)
            .createCompositeState(false)
    );

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(HighlightRenderer.class);
    }

    @SubscribeEvent
    public static void onDrawBlockHighlight(DrawHighlightEvent.HighlightBlock event) {
        if (event.getTarget() == null || event.getTarget().getBlockPos() == null) {
            return;
        }

        final var hitInfo = event.getTarget();
        final var world = Minecraft.getInstance().level;
        if (world == null) return;

        final var blockPos = new BlockPosition(hitInfo.getBlockPos(), world);
        final var stack = event.getMatrix();
        final var player = Minecraft.getInstance().player;
        if (player == null) return;

        if (ItemStack.matches(player.getItemInHand(Hand.MAIN_HAND), TABLET)) {
            if (!world.isEmptyBlock(blockPos)) {
                final var blockState = world.getBlockState(blockPos);
                final var shape = blockState.getShape(world, blockPos, ISelectionContext.of(player));
                if (shape.isEmpty()) return;

                final float minX = (float) shape.min(Direction.Axis.X);
                final float minY = (float) shape.min(Direction.Axis.Y);
                final float minZ = (float) shape.min(Direction.Axis.Z);
                final float maxX = (float) shape.max(Direction.Axis.X);
                final float maxY = (float) shape.max(Direction.Axis.Y);
                final float maxZ = (float) shape.max(Direction.Axis.Z);
                final var sideHit = hitInfo.getDirection();
                final var view = event.getInfo().getPosition();

                stack.pushPose();
                stack.translate(
                    blockPos.getX() - view.x,
                    blockPos.getY() - view.y,
                    blockPos.getZ() - view.z
                );
                stack.scale(1.002f, 1.002f, 1.002f);

                if (Settings.get().hologramFlickerFrequency() > 0 && 
                    RANDOM.nextDouble() < Settings.get().hologramFlickerFrequency()) {
                    final float sx = 1 - Math.abs(sideHit.getStepX());
                    final float sy = 1 - Math.abs(sideHit.getStepY());
                    final float sz = 1 - Math.abs(sideHit.getStepZ());
                    
                    stack.scale(
                        1f + (float)(RANDOM.nextGaussian() * 0.01),
                        1f + (float)(RANDOM.nextGaussian() * 0.001),
                        1f + (float)(RANDOM.nextGaussian() * 0.01)
                    );
                    stack.translate(
                        (float)(RANDOM.nextGaussian() * 0.01 * sx),
                        (float)(RANDOM.nextGaussian() * 0.01 * sy),
                        (float)(RANDOM.nextGaussian() * 0.01 * sz)
                    );
                }

                final var buffer = event.getBuffers().bufferSource().getBuffer(HOLOGRAM_EFFECT);
                final var matrix = stack.last().pose();
                final float offset = 0.002f;

                switch (sideHit) {
                    case UP -> {
                        buffer.vertex(matrix, maxX, maxY + offset, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, maxX * 16).endVertex();
                        buffer.vertex(matrix, maxX, maxY + offset, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, maxX * 16).endVertex();
                        buffer.vertex(matrix, minX, maxY + offset, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, minX * 16).endVertex();
                        buffer.vertex(matrix, minX, maxY + offset, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, minX * 16).endVertex();
                    }
                    case DOWN -> {
                        buffer.vertex(matrix, maxX, minY - offset, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, maxX * 16).endVertex();
                        buffer.vertex(matrix, maxX, minY - offset, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, maxX * 16).endVertex();
                        buffer.vertex(matrix, minX, minY - offset, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, minX * 16).endVertex();
                        buffer.vertex(matrix, minX, minY - offset, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, minX * 16).endVertex();
                    }
                    case EAST -> {
                        buffer.vertex(matrix, maxX + offset, maxY, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, maxX + offset, maxY, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, maxX + offset, minY, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, minY * 16).endVertex();
                        buffer.vertex(matrix, maxX + offset, minY, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, minY * 16).endVertex();
                    }
                    case WEST -> {
                        buffer.vertex(matrix, minX - offset, maxY, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, minX - offset, maxY, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, minX - offset, minY, minZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minZ * 16, minY * 16).endVertex();
                        buffer.vertex(matrix, minX - offset, minY, maxZ).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxZ * 16, minY * 16).endVertex();
                    }
                    case SOUTH -> {
                        buffer.vertex(matrix, maxX, maxY, maxZ + offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxX * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, minX, maxY, maxZ + offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minX * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, minX, minY, maxZ + offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minX * 16, minY * 16).endVertex();
                        buffer.vertex(matrix, maxX, minY, maxZ + offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxX * 16, minY * 16).endVertex();
                    }
                    case NORTH -> {
                        buffer.vertex(matrix, minX, maxY, minZ - offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minX * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, maxX, maxY, minZ - offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxX * 16, maxY * 16).endVertex();
                        buffer.vertex(matrix, maxX, minY, minZ - offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(maxX * 16, minY * 16).endVertex();
                        buffer.vertex(matrix, minX, minY, minZ - offset).color(0.0f, 1.0f, 0.0f, 0.4f).uv(minX * 16, minY * 16).endVertex();
                    }
                }

                stack.popPose();
            }
        }
    }
}
