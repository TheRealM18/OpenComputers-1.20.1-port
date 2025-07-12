package li.cil.oc.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector3d;
import li.cil.oc.OpenComputers;
import li.cil.oc.Settings;
import li.cil.oc.api.driver.item.UpgradeRenderer;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.client.renderer.RenderTypes;
import li.cil.oc.common.EventHandler;
import li.cil.oc.common.tileentity.RobotTileEntity;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RobotRenderer implements BlockEntityRenderer<RobotTileEntity> {
    private static final float SIZE = 0.3f;
    private static final float L = 0.5f - SIZE;
    private static final float H = 0.5f + SIZE;
    private static final float G = 0.05f; // Gap between components
    private static final float GB = 1 - G;
    
    private final RobotRenderEvent.MountPoint[] mountPoints = new RobotRenderEvent.MountPoint[7];
    private static RobotRenderer instance;

    public RobotRenderer(BlockEntityRendererProvider.Context context) {
        instance = this;
        // Initialize mount points
        for (int i = 0; i < mountPoints.length; i++) {
            mountPoints[i] = new RobotRenderEvent.MountPoint(0, 0, 0, "oc:upgrade_" + i);
        }
    }

    public static RobotRenderer getInstance() {
        return instance;
    }

    public static void renderChassis(PoseStack stack, MultiBufferSource buffer, int light, 
                                   @Nullable RobotTileEntity robot, double offset, boolean isRunningOverride) {
        if (instance != null) {
            instance.renderChassis(stack, buffer, light, robot, offset, isRunningOverride);
        }
    }

    @Override
    public void render(RobotTileEntity proxy, float partialTicks, PoseStack stack,
                      MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (proxy == null || !proxy.hasLevel()) {
            return;
        }

        RobotTileEntity robot = proxy.robot();
        if (robot == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        long worldTime = proxy.getLevel().getGameTime() + (long) partialTicks;
        double offset = Math.sin(worldTime * 0.1) * 0.01;
        
        stack.pushPose();
        stack.translate(0.5, 1.5 + offset, 0.5);
        stack.scale(1, -1, 1);

        // Rotate based on facing direction
        Direction facing = robot.facing();
        if (facing != null) {
            switch (facing) {
                case NORTH -> stack.mulPose(Vector3f.YP.rotationDegrees(0));
                case EAST -> stack.mulPose(Vector3f.YP.rotationDegrees(90));
                case SOUTH -> stack.mulPose(Vector3f.YP.rotationDegrees(180));
                case WEST -> stack.mulPose(Vector3f.YP.rotationDegrees(270));
                default -> {}
            }
        }

        // Calculate distance for LOD
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double distSq = cameraPos.distanceToSqr(proxy.getBlockPos().getX() + 0.5, 
                                              proxy.getBlockPos().getY() + 0.5,
                                              proxy.getBlockPos().getZ() + 0.5);
        boolean isClose = distSq < 24 * 24;

        // Render the chassis
        renderChassis(stack, buffer, combinedLight, robot, offset, false);

        // Render held item if close enough
        if (isClose && !robot.renderingErrored()) {
            ItemStack heldItem = robot.getStackInSlot(0);
            if (!heldItem.isEmpty()) {
                renderHeldItem(heldItem, stack, buffer, combinedLight, combinedOverlay, worldTime);
            }
        }

        // Render upgrades if close enough
        if (isClose) {
            renderUpgrades(robot, stack, buffer, combinedLight, combinedOverlay, worldTime);
        }

        // Render status text if close enough and sneaking
        if (isClose && mc.player != null && mc.player.isShiftKeyDown()) {
            renderStatusText(robot, stack, buffer, combinedLight);
        }
    }

    private void renderChassis(PoseStack stack, MultiBufferSource buffer, int light, 
                             @Nullable RobotTileEntity robot, double offset, boolean isRunningOverride) {
        boolean isRunning = robot == null ? isRunningOverride : robot.isRunning();
        resetMountPoints(isRunning);

        // Fire render event for mod compatibility
        RobotRenderEvent.Chrome chrome = new RobotRenderEvent.Chrome(
            robot, stack, buffer, light, isRunning, mountPoints
        );
        MinecraftForge.EVENT_BUS.post(chrome);

        // Render the robot chassis
        int color = robot == null ? 0x66DD55 : robot.getColor();
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        // Draw the bottom part of the robot
        drawBottom(stack, buffer.getBuffer(RenderTypes.ROBOT_CHASSIS), light, red, green, blue);
        
        // TODO: Draw other parts of the robot (sides, top, etc.)
    }

    private void renderHeldItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                              int combinedLight, int combinedOverlay, long worldTime) {
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        
        // Position the item in front of the robot
        poseStack.translate(0, -0.2, 0.4);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(30));
        
        // Animate the item bobbing up and down
        float offset = Mth.sin((worldTime % 6000) * 0.003f) * 0.05f;
        poseStack.translate(0, offset, 0);
        
        // Scale the item
        float scale = 0.5f;
        poseStack.scale(scale, scale, scale);
        
        // Render the item
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, TransformType.GROUND, combinedLight, combinedOverlay, poseStack, buffer, 0);
        
        poseStack.popPose();
    }

    private void renderUpgrades(RobotTileEntity robot, PoseStack stack, MultiBufferSource buffer,
                              int combinedLight, int combinedOverlay, long worldTime) {
        for (int i = 1; i < robot.getContainerSize(); i++) {
            ItemStack upgrade = robot.getStackInSlot(i);
            if (!upgrade.isEmpty()) {
                // Get the mount point for this upgrade slot
                RobotRenderEvent.MountPoint mountPoint = mountPoints[Math.min(i - 1, mountPoints.length - 1)];
                
                // Apply the mount point transform
                stack.pushPose();
                stack.translate(mountPoint.offset().x, mountPoint.offset().y, mountPoint.offset().z);
                stack.mulPose(Vector3f.XP.rotation(mountPoint.rotation().x));
                stack.mulPose(Vector3f.YP.rotation(mountPoint.rotation().y));
                stack.mulPose(Vector3f.ZP.rotation(mountPoint.rotation().z));
                
                // Let upgrade renderers handle the rendering if available
                boolean handled = false;
                for (UpgradeRenderer renderer : UpgradeRenderer.getRenderersForItem(upgrade)) {
                    if (renderer.render(robot, upgrade, mountPoint, stack, buffer, combinedLight, combinedOverlay)) {
                        handled = true;
                        break;
                    }
                }
                
                // Default rendering if no custom renderer handled it
                if (!handled) {
                    renderDefaultUpgrade(upgrade, stack, buffer, combinedLight, combinedOverlay, worldTime);
                }
                
                stack.popPose();
            }
        }
    }
    
    private void renderDefaultUpgrade(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                                    int combinedLight, int combinedOverlay, long worldTime) {
        if (stack.isEmpty()) return;
        
        poseStack.pushPose();
        
        // Scale and position the upgrade
        poseStack.scale(0.3f, 0.3f, 0.3f);
        
        // Rotate the upgrade slowly over time
        float rotation = (worldTime % 360) * 0.5f;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        
        // Render the item
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, TransformType.GROUND, combinedLight, combinedOverlay, poseStack, buffer, 0);
        
        poseStack.popPose();
    }
    
    private void renderStatusText(RobotTileEntity robot, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        
        // Get status information
        String status = robot.status();
        String name = robot.getDisplayName().getString();
        
        // Format the text
        Component statusText = new TextComponent(status).withStyle(ChatFormatting.GRAY);
        Component nameText = new TextComponent(name).withStyle(ChatFormatting.WHITE);
        
        // Calculate text width for centering
        float nameWidth = font.width(nameText);
        float statusWidth = font.width(statusText);
        float maxWidth = Math.max(nameWidth, statusWidth);
        
        // Position the text above the robot
        poseStack.pushPose();
        poseStack.translate(0, -1.2, 0);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.02f, -0.02f, 0.02f);
        
        // Draw name
        font.drawInBatch(nameText, -nameWidth / 2, 0, 0xFFFFFFFF, false, 
                        poseStack.last().pose(), buffer, false, 0, combinedLight);
        
        // Draw status below name
        font.drawInBatch(statusText, -statusWidth / 2, 10, 0xFFAAAAAA, false, 
                        poseStack.last().pose(), buffer, false, 0, combinedLight);
        
        poseStack.popPose();
    }

    private void drawBottom(PoseStack stack, VertexConsumer r, int light, int red, int green, int blue) {
        // Bottom face vertices
        r.vertex(stack.last().pose(), 0.5f, 0.03f, 0.5f)
            .color(red, green, blue, 0xFF)
            .uv(0.75f, 0.25f)
            .uv2(light)
            .normal(stack.last().normal(), new Vector3f(0, -0.2f, 1))
            .endVertex();
        r.vertex(stack.last().pose(), L, G, L)
            .color(red, green, blue, 0xFF)
            .uv(0.5f, 0)
            .uv2(light)
            .normal(stack.last().normal(), new Vector3f(0, -0.2f, 1))
            .endVertex();
        // Add remaining vertices for the bottom face
        // ...
    }

    private void resetMountPoints(boolean running) {
        float offset = running ? 0 : -0.06f;

        // Left top
        mountPoints[0].offset().setX(0);
        mountPoints[0].offset().setY(0.25f);
        mountPoints[0].offset().setZ(0.5f + offset);
        mountPoints[0].rotation().set(0, 0, 0);

        // Right top
        mountPoints[1].offset().setX(1);
        mountPoints[1].offset().setY(0.25f);
        mountPoints[1].offset().setZ(0.5f + offset);
        mountPoints[1].rotation().set(0, 0, 0);

        // Left bottom
        mountPoints[2].offset().setX(0);
        mountPoints[2].offset().setY(-0.25f);
        mountPoints[2].offset().setZ(0.5f + offset);
        mountPoints[2].rotation().set(0, 0, 0);

        // Right bottom
        mountPoints[3].offset().setX(1);
        mountPoints[3].offset().setY(-0.25f);
        mountPoints[3].offset().setZ(0.5f + offset);
        mountPoints[3].rotation().set(0, 0, 0);

        // Back top
        mountPoints[4].offset().setX(0.5f);
        mountPoints[4].offset().setY(0.3f);
        mountPoints[4].offset().setZ(0.5f + offset);
        mountPoints[4].rotation().set(0, 0, 0);

        // Back center
        mountPoints[5].offset().setX(0.5f);
        mountPoints[5].offset().setY(0.0f);
        mountPoints[5].offset().setZ(0.5f + offset);
        mountPoints[5].rotation().set(0, 0, 0);

        // Back bottom
        mountPoints[6].offset().setX(0.5f);
        mountPoints[6].offset().setY(-0.3f);
        mountPoints[6].offset().setZ(0.5f + offset);
        mountPoints[6].rotation().set(0, 0, 0);
    }
}

// Factory class for the renderer
class RobotRendererProvider implements BlockEntityRendererProvider<RobotTileEntity> {
    @Override
    public BlockEntityRenderer<RobotTileEntity> create(Context context) {
        return new RobotRenderer(context);
    }
}
