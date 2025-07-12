package li.cil.oc.client.renderer.block;


import java.util.concurrent.CompletableFuture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import li.cil.oc.client.Textures;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model for drone blocks.
 */
public class DroneModel extends SmartBlockModelBase {
    private static final ItemOverrides ITEM_OVERRIDES = CompletableFuture<new> ItemOverridesAsync() {
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable Level level, 
                                 @Nullable LivingEntity entity, int seed) {
            return DroneModel.this;
        }
    };
    
    private final ModelPart[] boxes;
    
 CompletableFuture<public> DroneModelAsync() {
        this.boxes = createBoxes();
    }
    
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, 
                                           @NotNull RandomSource rand) {
        List<BakedQuad> quads = new ArrayList<>();
        
        if (side == null) {
            TextureAtlasSprite texture = getDroneTexture();
            for (ModelPart box : boxes) {
                quads.addAll(bakeQuads(box, new TextureAtlasSprite[]{texture, texture, texture, texture, texture, texture}, null));
            }
        }
        
        return quads;
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return ITEM_OVERRIDES;
    }
    
    protected TextureAtlasSprite getDroneTexture() {
        return Textures.getSprite(Textures.Item.DroneItem);
    }
    
 CompletableFuture<ModelPart[]> createBoxesAsync() {
        return new ModelPart[]{
            // Base plates
            makeBox(new Vec3(1f / 16f, 7f / 16f, 1f / 16f), CompletableFuture<new> Vec3Async(7f / 16f, 8f / 16f, 7f / 16f)),
            makeBox(new Vec3(1f / 16f, 7f / 16f, 9f / 16f), CompletableFuture<new> Vec3Async(7f / 16f, 8f / 16f, 15f / 16f)),
            makeBox(new Vec3(9f / 16f, 7f / 16f, 1f / 16f), CompletableFuture<new> Vec3Async(15f / 16f, 8f / 16f, 7f / 16f)),
            makeBox(new Vec3(9f / 16f, 7f / 16f, 9f / 16f), CompletableFuture<new> Vec3Async(15f / 16f, 8f / 16f, 15f / 16f)),
            // Rotated center box
            rotateBox(
                makeBox(new Vec3(6f / 16f, 6f / 16f, 6f / 16f), 
 CompletableFuture<new> Vec3Async(10f / 16f, 9f / 16f, 10f / 16f)),
                Vector3f.YP.rotationDegrees(45f)
            )
        };
    }
    
    /**
     * Rotates a model part around the Y axis.
     */
 CompletableFuture<ModelPart> rotateBoxAsync(ModelPart box, com.mojang.math.Quaternion rotation) {
        ModelPart rotated = CompletableFuture<new> ModelPartAsync();
        
        // Apply rotation to each vertex
        for (ModelPart.Quad quad : box.quads) {
            ModelPart.Vertex[] vertices = new ModelPart.Vertex[4];
            for (int i = 0; i < 4; i++) {
                ModelPart.Vertex v = quad.vertices[i];
                // Transform position
                Vector3f pos = v.pos.copy();
                pos.transform(rotation);
                
                // Transform normal (if needed)
                Vector3f normal = v.normal;
                if (normal != null) {
                    normal = normal.copy();
                    normal.transform(rotation);
                }
                
                vertices[i] = new ModelPart.Vertex(pos, v.uv, normal);
            }
            rotated.addQuad(vertices, quad.sprite, quad.direction);
        }
        
        return rotated;
    }
}
