package li.cil.oc.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.client.renderer.tileentity.RobotRenderer;
import li.cil.oc.util.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public final class PetRenderer {
    private static final Set<String> HIDDEN = new java.util.HashSet<>();
    private static final AtomicBoolean IS_INITIALIZED = new AtomicBoolean(false);

    // Player UUID to (r, g, b) color mapping
    private static final Map<String, Color> ENTITLED_PLAYERS = Map.of(
        "9f1f262f-0d68-4e13-9161-9eeaf4a0a1a8", new Color(0.3, 0.9, 0.6),   // Sangar
        "18f8bed4-f027-44af-8947-6a3a2317645a", new Color(1.0, 0.0, 0.0),   // Jodarion
        "36123742-2cf6-4cfc-8b65-278581b3caeb", new Color(0.5, 0.7, 1.0),   // DaKaTotal
        "2c0c214b-96f4-4565-b513-de90d5fbc977", new Color(1.0, 0.0, 0.0),   // MichiRavencroft
        "f3ba6ec8-c280-4950-bb08-1fcb2eab3a9c", new Color(0.18, 0.95, 0.922), // Vexatos
        "9d636bdd-b9f4-4b80-b9ce-586ca04bd4f3", new Color(0.8, 0.77, 0.75), // StoneNomad
        "23c7ed71-fb13-4abe-abe7-f355e1de6e62", new Color(0.3, 0.3, 1.0),   // LizzyTheSiren
        "076541f1-f10a-46de-a127-dfab8adfbb75", new Color(0.2, 1.0, 0.1),   // vifino
        "e7e90198-0ccf-4662-a827-192ec8f4419d", new Color(0.0, 0.2, 0.6),   // Izaya
        "f514ee69-7bbb-4e46-9e94-d8176324cec2", new Color(0.098, 0.471, 0.784), // Wobbo
        "f812c043-78ba-4324-82ae-e8f05c52ae6e", new Color(0.1, 0.8, 0.5),   // payonel
        "1db17ee7-8830-4bac-8018-de154340aae6", new Color(0.0, 0.5, 1.0)    // Kosmos
    );

    private static final Cache<Entity, PetLocation> PET_LOCATIONS = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    private static Vector3d currentRenderingColor = null;

    private PetRenderer() {
    }

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        String uuid = player.getUUID().toString();
        
        if (HIDDEN.contains(uuid) || !ENTITLED_PLAYERS.containsKey(uuid)) {
            return;
        }

        Color color = ENTITLED_PLAYERS.get(uuid);
        currentRenderingColor = new Vector3d(color.r, color.g, color.b);

        long worldTime = player.level.getGameTime();
        int timeJitter = player.hashCode() ^ 0xFF;
        float offset = timeJitter + worldTime / 20.0f;
        float hover = (float) Math.sin(timeJitter + (worldTime + event.getPartialRenderTick()) / 20.0) * 0.03f;

        try {
            PetLocation location = PET_LOCATIONS.get(player, () -> new PetLocation(player));

            MatrixStack stack = event.getMatrixStack();
            stack.pushPose();

            ClientPlayerEntity self = Minecraft.getInstance().player;
            if (self == null) return;

            double px = player.xOld + (player.getX() - player.xOld) * event.getPartialRenderTick();
            double py = player.yOld + (player.getY() - player.yOld) * event.getPartialRenderTick() + 
                       player.getEyeHeight(player.getPose());
            double pz = player.zOld + (player.getZ() - player.zOld) * event.getPartialRenderTick();
            
            stack.translate(px - self.getX(), py - self.getY(), pz - self.getZ());
            location.applyInterpolatedTransformations(stack, event.getPartialRenderTick());

            stack.scale(0.3f, 0.3f, 0.3f);
            stack.translate(0, hover, 0);

            RobotRenderer.renderChassis(stack, event.getBuffers(), event.getLight(), offset, true);
            stack.popPose();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            currentRenderingColor = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRobotRender(RobotRenderEvent event) {
        if (currentRenderingColor != null) {
            event.setLightColor((float) currentRenderingColor.x, 
                              (float) currentRenderingColor.y, 
                              (float) currentRenderingColor.z);
            event.multiplyColors((float) currentRenderingColor.x, 
                               (float) currentRenderingColor.y, 
                               (float) currentRenderingColor.z);
        }
    }

    @SubscribeEvent
    public static void tickStart(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PET_LOCATIONS.cleanUp();
            PET_LOCATIONS.asMap().values().forEach(PetLocation::update);
        }
    }

    private static class PetLocation {
        private final Entity owner;
        private double x, y, z;
        private float yaw;
        private double lastX, lastY, lastZ;
        private float lastYaw;

        public PetLocation(Entity owner) {
            this.owner = owner;
            this.yaw = owner.yRot;
            this.lastYaw = yaw;
        }

        public void update() {
            double dx = owner.xOld - owner.getX();
            double dy = owner.yOld - owner.getY();
            double dz = owner.zOld - owner.getZ();
            float dYaw = owner.yRot - yaw;
            
            lastX = x;
            lastY = y;
            lastZ = z;
            lastYaw = yaw;
            
            x += dx;
            y += dy;
            z += dz;
            x *= 0.05;
            y *= 0.05;
            z *= 0.05;
            yaw += dYaw * 0.2f;
        }

        public void applyInterpolatedTransformations(MatrixStack stack, float partialTicks) {
            double ix = lastX + (x - lastX) * partialTicks;
            double iy = lastY + (y - lastY) * partialTicks;
            double iz = lastZ + (z - lastZ) * partialTicks;
            float iYaw = lastYaw + (yaw - lastYaw) * partialTicks;

            stack.translate(ix, iy, iz);
            if (!isForInventory()) {
                stack.mulPose(Vector3f.YP.rotationDegrees(-iYaw));
            } else {
                stack.mulPose(Vector3f.YP.rotationDegrees(-owner.yRot));
            }
            stack.translate(0.3, -0.1, -0.2);
        }

        private boolean isForInventory() {
            return Minecraft.getInstance().screen != null && 
                   owner == Minecraft.getInstance().player;
        }
    }

    private static class Color {
        public final double r, g, b;

        public Color(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
