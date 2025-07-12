package li.cil.oc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import li.cil.oc.Constants;
import li.cil.oc.Settings;
import li.cil.oc.api.Items;
import li.cil.oc.util.BlockPosition;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class MFUTargetRenderer {
    private static final float DRAW_RED = 0.0f;
    private static final float DRAW_GREEN = 1.0f;
    private static final float DRAW_BLUE = 0.0f;

    private static final ItemStack MFU_ITEM = Items.get(Constants.ItemName.MFU).createItemStack(1);

    private MFUTargetRenderer() {
    }

    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getItemInHand(Hand.MAIN_HAND);
        if (!ItemStack.matches(stack, MFU_ITEM) || !stack.hasTag()) {
            return;
        }

        var data = stack.getTag();
        if (!data.contains(Settings.namespace + "coord", NBT.TAG_INT_ARRAY)) {
            return;
        }

        var dimension = new ResourceLocation(data.getString(Settings.namespace + "dimension"));
        if (!mc.level.dimension().location().equals(dimension)) {
            return;
        }

        int[] coords = data.getIntArray(Settings.namespace + "coord");
        if (coords.length < 4) return;

        int x = coords[0];
        int y = coords[1];
        int z = coords[2];
        int side = coords[3];

        if (mc.player.distanceToSqr(x, y, z) > 64 * 64) {
            return;
        }

        var bounds = new BlockPosition(x, y, z, mc.level).bounds().inflate(0.1, 0.1, 0.1);

        RenderState.checkError(MFUTargetRenderer.class.getName() + ".onRenderWorldLastEvent: entering (aka: wasntme)");

        var matrix = event.getMatrixStack();
        matrix.pushPose();
        
        Vector3d camPos = mc.gameRenderer.getMainCamera().getPosition();
        matrix.translate(-camPos.x, -camPos.y, -camPos.z);

        RenderSystem.disableDepthTest();
        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
        
        drawBox(matrix.last().pose(), 
               buffer.getBuffer(RenderTypes.MFU_LINES),
               (float)bounds.minX, (float)bounds.minY, (float)bounds.minZ,
               (float)bounds.maxX, (float)bounds.maxY, (float)bounds.maxZ,
               DRAW_RED, DRAW_GREEN, DRAW_BLUE);
               
        drawFace(matrix.last().pose(),
               buffer.getBuffer(RenderTypes.MFU_QUADS),
               (float)bounds.minX, (float)bounds.minY, (float)bounds.minZ,
               (float)bounds.maxX, (float)bounds.maxY, (float)bounds.maxZ,
               side, DRAW_RED, DRAW_GREEN, DRAW_BLUE);
               
        buffer.endBatch();
        matrix.popPose();
        RenderState.checkError(MFUTargetRenderer.class.getName() + ".onRenderWorldLastEvent: leaving");
    }

    private static void drawBox(Matrix4f matrix, IVertexBuilder builder, 
                              float minX, float minY, float minZ,
                              float maxX, float maxY, float maxZ,
                              float r, float g, float b) {
        // Bottom square
        addVertex(builder, matrix, minX, minY, minZ, r, g, b);
        addVertex(builder, matrix, minX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, minZ, r, g, b);
        addVertex(builder, matrix, minX, minY, minZ, r, g, b);

        // Vertical bars
        addVertex(builder, matrix, minX, minY, minZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, maxY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, maxX, maxY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, minY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, maxZ, r, g, b);

        // Top square
        addVertex(builder, matrix, maxX, maxY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, maxY, maxZ, r, g, b);
        addVertex(builder, matrix, maxX, maxY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, maxZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, minZ, r, g, b);
        addVertex(builder, matrix, minX, maxY, minZ, r, g, b);
        addVertex(builder, matrix, maxX, maxY, minZ, r, g, b);
    }

    private static void drawFace(Matrix4f matrix, IVertexBuilder builder,
                               float minX, float minY, float minZ,
                               float maxX, float maxY, float maxZ,
                               int side, float r, float g, float b) {
        switch (side) {
            case 0: // Down
                addQuad(builder, matrix, 
                       minX, minY, minZ,
                       minX, minY, maxZ,
                       maxX, minY, maxZ,
                       maxX, minY, minZ,
                       r, g, b);
                break;
            case 1: // Up
                addQuad(builder, matrix,
                       maxX, maxY, minZ,
                       maxX, maxY, maxZ,
                       minX, maxY, maxZ,
                       minX, maxY, minZ,
                       r, g, b);
                break;
            case 2: // North
                addQuad(builder, matrix,
                       minX, minY, minZ,
                       maxX, minY, minZ,
                       maxX, maxY, minZ,
                       minX, maxY, minZ,
                       r, g, b);
                break;
            case 3: // South
                addQuad(builder, matrix,
                       maxX, maxY, maxZ,
                       maxX, minY, maxZ,
                       minX, minY, maxZ,
                       minX, maxY, maxZ,
                       r, g, b);
                break;
            case 4: // East
                addQuad(builder, matrix,
                       minX, minY, minZ,
                       minX, maxY, minZ,
                       minX, maxY, maxZ,
                       minX, minY, maxZ,
                       r, g, b);
                break;
            case 5: // West
                addQuad(builder, matrix,
                       maxX, minY, minZ,
                       maxX, minY, maxZ,
                       maxX, maxY, maxZ,
                       maxX, maxY, minZ,
                       r, g, b);
                break;
        }
    }

    private static void addQuad(IVertexBuilder builder, Matrix4f matrix,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float r, float g, float b) {
        addVertex(builder, matrix, x1, y1, z1, r, g, b);
        addVertex(builder, matrix, x2, y2, z2, r, g, b);
        addVertex(builder, matrix, x3, y3, z3, r, g, b);
        addVertex(builder, matrix, x4, y4, z4, r, g, b);
    }

    private static void addVertex(IVertexBuilder builder, Matrix4f matrix, float x, float y, float z, float r, float g, float b) {
        builder.vertex(matrix, x, y, z)
              .color(r, g, b, 0.5f)
              .endVertex();
    }
}
