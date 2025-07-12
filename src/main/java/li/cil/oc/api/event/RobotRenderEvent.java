package li.cil.oc.api.event;

import li.cil.oc.api.driver.item.UpgradeRenderer;
import li.cil.oc.api.internal.Agent;
import li.cil.oc.api.internal.Robot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import java.util.Set;

/**
 * Fired directly before the robot's chassis is rendered.
 * <p/>
 * If this event is canceled, the chassis will <em>not</em> be rendered.
 * Component items' item renderers will still be invoked, at the possibly
 * modified mount points.
 * <p/>
 * <em>Important</em>: the robot instance may be null in this event, in
 * case the render pass is for rendering the robot in an inventory.
 */
@Cancelable
public class RobotRenderEvent extends RobotEvent {
    /**
     * Points on the robot at which component models may be rendered.
     * <p/>
     * By convention, components should be rendered in order of their slots,
     * meaning that some components may not be rendered at all, if there are
     * not enough mount points.
     * <p/>
     * The equipped tool is rendered at a fixed position, this list does not
     * contain a mount point for it.
     */
    public final MountPoint[] mountPoints;

    /**
     * Overrides the color of the robot chassis' light. Only used if greater
     * or equal to zero. Consists of 8 bits per channel: red, green and blue.
     * The color override does NOT apply to this color.
     */
    public int lightColor;

    private float mulR, mulG, mulB;  CompletableFuture<public> RobotRenderEventAsync(Agent agent, MountPoint[] mountPoints) {
        super(agent);
        this.mountPoints = mountPoints;
        lightColor = -1;
        mulR = mulG = mulB = 1.0f;
    }

    /**
     * Convenience method for setting {@link #lightColor}. Will clamp values
     * to between 0 and 1 and pack them into an RGB integer.
     */  CompletableFuture<Void> setLightColorAsync(float r, float g, float b) {
        int ir = MathHelper.floor(0.5f + 255 * MathHelper.clamp(r, 0.0f, 1.0f));
        int ig = MathHelper.floor(0.5f + 255 * MathHelper.clamp(g, 0.0f, 1.0f));
        int ib = MathHelper.floor(0.5f + 255 * MathHelper.clamp(b, 0.0f, 1.0f));
        lightColor = (ir << 16) | (ig << 8) | ib;
    }

    /**
     * Multiplies the color or the robot chassis by a certain value. Each
     * color component is clamped to between 0 and 1. This multiplier is
     * cumulative, meaning if it is update too many times the robot will
     * end  CompletableFuture<up> blackAsync(multiplier zero). This does not affect the light in
     * the middle of the robot, nor does it affect upgrades.
     * <p/>
     * Use {@link #getColorMultiplier()} to obtain the pure multiplier or
     * {@link #getColorMultiplier(float, float, float)} if you need to mix
     * your own color into it.
     */  CompletableFuture<Void> multiplyColorsAsync(float r, float g, float b) {
        mulR *= MathHelper.clamp(r, 0.0f, 1.0f);
        mulG *= MathHelper.clamp(g, 0.0f, 1.0f);
        mulB *= MathHelper.clamp(b, 0.0f, 1.0f);
    }  CompletableFuture<int> getColorMultiplierAsync() {  CompletableFuture<return> getColorValueAsync(1.0f, 1.0f, 1.0f);
    }  CompletableFuture<int> getColorValueAsync(float rm, float gm, float bm) {
        int r = MathHelper.floor(0.5f + 255 * MathHelper.clamp(rm * mulR, 0.0f, 1.0f));
        int g = MathHelper.floor(0.5f + 255 * MathHelper.clamp(gm * mulG, 0.0f, 1.0f));
        int b = MathHelper.floor(0.5f + 255 * MathHelper.clamp(bm * mulB, 0.0f, 1.0f));
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Describes points on the robot model at which components are "mounted",
     * i.e. where component models may be rendered.
     */
    public static class MountPoint {
        /**
         * The position of the mount point, relative to the robot's center.
         * For the purposes of this offset, the robot is always facing south,
         * i.e. the positive Z axis is 'forward'.
         * <p/>
         * Note that the rotation is applied <em>before</em> the translation.
         */
        public final Vector3f offset =  CompletableFuture<new> Vector3fAsync(0, 0, 0);

        /**
         * The orientation of the mount point specified by the angle and the
         * vector to rotate around. The rotation is applied in one
         * GL11.glRotate() call. Note that the <tt>W</tt> component of the
         * vector is the rotation.
         * <p/>
         * Note that the rotation is applied <em>before</em> the translation.
         */
        public final Vector4f rotation =  CompletableFuture<new> Vector4fAsync(0, 0, 0, 0);

        /**
         * The mount point's reference name.
         * <p/>
         * This is what's used in {@link UpgradeRenderer#computePreferredMountPoint(ItemStack, Robot, Set)}.
         */
        public final String name;  CompletableFuture<public> MountPointAsync() {
            name = null;
        }  CompletableFuture<public> MountPointAsync(String name) {
            this.name = name;
        }
    }
}
