package li.cil.oc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.oc.Settings;
import li.cil.oc.server.network.WirelessNetwork;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber
public final class WirelessNetworkDebugRenderer {
    private static final int[] COLORS = {0xFF0000, 0x00FFFF, 0x00FF00, 0x0000FF, 0xFF00FF, 0xFFFF00, 0xFFFFFF, 0x000000};

    private WirelessNetworkDebugRenderer() {
    }

    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        if (!Settings.rTreeDebugRenderer) {
            return;
        }

        RenderState.checkError(WirelessNetworkDebugRenderer.class.getName() + ".onRenderWorldLastEvent: entering (aka: wasntme)");

        var world = Minecraft.getInstance().level;
        var network = WirelessNetwork.dimensions.get(world.dimension());
        if (network == null) {
            return;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        double px = player.xOld + (player.getX() - player.xOld) * event.getPartialTicks();
        double py = player.yOld + (player.getY() - player.yOld) * event.getPartialTicks();
        double pz = player.zOld + (player.getZ() - player.zOld) * event.getPartialTicks();

        var stack = event.getMatrixStack();
        RenderState.pushAttrib();
        stack.pushPose();
        stack.translate(-px, -py, -pz);
        
        RenderState.makeItBlend();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        Vector4f temp = new Vector4f();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        
        network.allBounds().forEach((bounds, level) -> {
            var min = bounds.getA();
            var max = bounds.getB();
            
            int color = COLORS[level % COLORS.length];
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = ((color >> 0) & 0xFF) / 255f;
            
            GL11.glColor4f(r, g, b, 0.25f);
            
            float size = 0.5f - level * 0.05f;
            drawBox(stack.last().pose(), temp,
                   (float)min.x() - size, (float)min.y() - size, (float)min.z() - size,
                   (float)max.x() + size, (float)max.y() + size, (float)max.z() + size);
        });
        
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        
        RenderState.popAttrib();
        stack.popPose();
        
        RenderState.checkError(WirelessNetworkDebugRenderer.class.getName() + ".onRenderWorldLastEvent: leaving");
    }

    private static void drawBox(Matrix4f matrix, Vector4f temp, 
                              float minX, float minY, float minZ,
                              float maxX, float maxY, float maxZ) {
        // Bottom face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, minX, minY, minZ);
        glVertex(matrix, temp, minX, minY, maxZ);
        glVertex(matrix, temp, maxX, minY, maxZ);
        glVertex(matrix, temp, maxX, minY, minZ);
        GL11.glEnd();
        
        // Top face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, minX, maxY, minZ);
        glVertex(matrix, temp, maxX, maxY, minZ);
        glVertex(matrix, temp, maxX, maxY, maxZ);
        glVertex(matrix, temp, minX, maxY, maxZ);
        GL11.glEnd();
        
        // North face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, minX, minY, minZ);
        glVertex(matrix, temp, maxX, minY, minZ);
        glVertex(matrix, temp, maxX, maxY, minZ);
        glVertex(matrix, temp, minX, maxY, minZ);
        GL11.glEnd();
        
        // South face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, maxX, maxY, maxZ);
        glVertex(matrix, temp, maxX, minY, maxZ);
        glVertex(matrix, temp, minX, minY, maxZ);
        glVertex(matrix, temp, minX, maxY, maxZ);
        GL11.glEnd();
        
        // West face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, minX, minY, minZ);
        glVertex(matrix, temp, minX, maxY, minZ);
        glVertex(matrix, temp, minX, maxY, maxZ);
        glVertex(matrix, temp, minX, minY, maxZ);
        GL11.glEnd();
        
        // East face
        GL11.glBegin(GL11.GL_QUADS);
        glVertex(matrix, temp, maxX, minY, minZ);
        glVertex(matrix, temp, maxX, minY, maxZ);
        glVertex(matrix, temp, maxX, maxY, maxZ);
        glVertex(matrix, temp, maxX, maxY, minZ);
        GL11.glEnd();
    }
    
    private static void glVertex(Matrix4f matrix, Vector4f temp, float x, float y, float z) {
        temp.set(x, y, z, 1);
        temp.transform(matrix);
        GL11.glVertex3f(temp.x(), temp.y(), temp.z());
    }
}
