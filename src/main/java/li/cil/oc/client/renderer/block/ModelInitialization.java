package li.cil.oc.client.renderer.block;

import com.google.common.collect.ImmutableMap;
import li.cil.oc.Constants;
import li.cil.oc.OpenComputers;
import li.cil.oc.Settings;
import li.cil.oc.api.Items;
import li.cil.oc.common.item.CustomModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = OpenComputers.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModelInitialization {
    public static final ModelResourceLocation CABLE_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.CABLE), "");
    public static final ModelResourceLocation CABLE_ITEM_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.CABLE), "inventory");
    public static final ModelResourceLocation NET_SPLITTER_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.NET_SPLITTER), "");
    public static final ModelResourceLocation NET_SPLITTER_ITEM_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.NET_SPLITTER), "inventory");
    public static final ModelResourceLocation PRINT_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.PRINT), "");
    public static final ModelResourceLocation PRINT_ITEM_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.PRINT), "inventory");
    public static final ModelResourceLocation ROBOT_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.ROBOT), "");
    public static final ModelResourceLocation ROBOT_ITEM_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.ROBOT), "inventory");
    public static final ModelResourceLocation ROBOT_AFTERIMAGE_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.ROBOT_AFTERIMAGE), "");
    public static final ModelResourceLocation RACK_BLOCK_LOCATION = new ModelResourceLocation(
        new ResourceLocation(Settings.resourceDomain, Constants.BlockName.RACK), "");

    private static final Set<Item> MESHABLE_ITEMS = ConcurrentHashMap.newKeySet();
    private static final Map<ModelResourceLocation, ModelResourceLocation> MODEL_REMAPPINGS = new ConcurrentHashMap<>();

    private ModelInitialization() {
    }

    public static void preInit() {
        registerModel(Constants.BlockName.CABLE, CABLE_BLOCK_LOCATION, CABLE_ITEM_LOCATION);
        registerModel(Constants.BlockName.NET_SPLITTER, NET_SPLITTER_BLOCK_LOCATION, NET_SPLITTER_ITEM_LOCATION);
        registerModel(Constants.BlockName.PRINT, PRINT_BLOCK_LOCATION, PRINT_ITEM_LOCATION);
        registerModel(Constants.BlockName.ROBOT, ROBOT_BLOCK_LOCATION, ROBOT_ITEM_LOCATION);
        registerModel(Constants.BlockName.ROBOT_AFTERIMAGE, ROBOT_AFTERIMAGE_BLOCK_LOCATION, null);
    }

    @SubscribeEvent
    public static void onModelRegistration(ModelRegistryEvent event) {
        ItemModelMesher shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
        for (Item item : MESHABLE_ITEMS) {
            if (item instanceof CustomModel) {
                ((CustomModel) item).registerModelLocations();
            } else {
                Optional.ofNullable(Items.get(new ItemStack(item))).ifPresent(descriptor -> {
                    ResourceLocation location = new ResourceLocation(Settings.resourceDomain, descriptor.name());
                    shaper.register(item, new ModelResourceLocation(location, "inventory"));
                });
            }
        }
    }

    public static void registerModel(IItemProvider instance, String id) {
        MESHABLE_ITEMS.add(instance.asItem());
    }

    private static void registerModel(String blockName, ModelResourceLocation blockLocation, ModelResourceLocation itemLocation) {
        var descriptor = Items.get(blockName);
        if (descriptor == null) return;
        
        var block = descriptor.block();
        var stack = descriptor.createItemStack(1);

        if (!stack.isEmpty() && itemLocation != null) {
            var shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
            shaper.register(stack.getItem(), itemLocation);
        }
        
        if (block != null) {
            block.getStateDefinition().getPossibleStates().forEach(state -> 
                MODEL_REMAPPINGS.put(block.getStateDefinition().any().getBlockState().getModelLocation(block.defaultBlockState()), blockLocation)
            );
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        var registry = event.getModelRegistry();

        registry.put(CABLE_BLOCK_LOCATION, CableModel.INSTANCE);
        registry.put(CABLE_ITEM_LOCATION, CableModel.INSTANCE);
        registry.put(NET_SPLITTER_BLOCK_LOCATION, NetSplitterModel.INSTANCE);
        registry.put(NET_SPLITTER_ITEM_LOCATION, NetSplitterModel.INSTANCE);
        registry.put(PRINT_BLOCK_LOCATION, PrintModel.INSTANCE);
        registry.put(PRINT_ITEM_LOCATION, PrintModel.INSTANCE);
        registry.put(ROBOT_BLOCK_LOCATION, RobotModel.INSTANCE);
        registry.put(ROBOT_ITEM_LOCATION, RobotModel.INSTANCE);
        registry.put(ROBOT_AFTERIMAGE_BLOCK_LOCATION, NullModel.INSTANCE);

        for (Item item : MESHABLE_ITEMS) {
            if (item instanceof CustomModel) {
                CustomModel custom = (CustomModel) item;
                custom.bakeModels(event);
                
                ModelResourceLocation originalLocation = new ModelResourceLocation(
                    Objects.requireNonNull(custom.getRegistryName()), "inventory");
                
                IBakedModel original = registry.get(originalLocation);
                if (original != null) {
                    ItemOverrideList overrides = new ItemOverrideList() {
                        @Override
                        public IBakedModel resolve(IBakedModel base, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder) {
                            ModelResourceLocation location = custom.getModelLocation(stack);
                            return location != null ? registry.get(location) : original;
                        }
                    };
                    
                    IDynamicBakedModel fake = new IDynamicBakedModel() {
                        @Override
                        public java.util.List<net.minecraft.client.renderer.model.BakedQuad> getQuads(
                            @Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
                            return original.getQuads(state, side, rand, extraData);
                        }

                        @Override
                        public boolean useAmbientOcclusion() {
                            return original.useAmbientOcclusion();
                        }

                        @Override
                        public boolean isGui3d() {
                            return original.isGui3d();
                        }

                        @Override
                        public boolean usesBlockLight() {
                            return original.usesBlockLight();
                        }

                        @Override
                        public boolean isCustomRenderer() {
                            return original.isCustomRenderer();
                        }

                        @Override
                        public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon() {
                            return original.getParticleIcon();
                        }

                        @Override
                        public net.minecraft.client.renderer.model.ItemCameraTransforms getTransforms() {
                            return original.getTransforms();
                        }

                        @Override
                        public ItemOverrideList getOverrides() {
                            return overrides;
                        }
                    };
                    
                    registry.put(originalLocation, fake);
                }
            }
        }
        
        MESHABLE_ITEMS.clear();

        Map<String, java.util.function.Function<IBakedModel, IBakedModel>> modelOverrides = ImmutableMap.of(
            Constants.BlockName.SCREEN_TIER1, parent -> ScreenModel.INSTANCE,
            Constants.BlockName.SCREEN_TIER2, parent -> ScreenModel.INSTANCE,
            Constants.BlockName.SCREEN_TIER3, parent -> ScreenModel.INSTANCE,
            Constants.BlockName.RACK, ServerRackModel::new
        );

        for (ResourceLocation location : registry.keySet()) {
            if (location instanceof ModelResourceLocation) {
                for (Map.Entry<String, java.util.function.Function<IBakedModel, IBakedModel>> entry : modelOverrides.entrySet()) {
                    String pattern = "^" + Settings.resourceDomain + ":" + entry.getKey() + "#.*";
                    if (location.toString().matches(pattern)) {
                        registry.put((ModelResourceLocation) location, entry.getValue().apply(registry.get(location)));
                    }
                }
            }
        }
        
        MODEL_REMAPPINGS.forEach((real, virtual) -> {
            IBakedModel model = registry.get(virtual);
            if (model != null) {
                registry.put(real, model);
            }
        });
        
        MODEL_REMAPPINGS.clear();
    }
}
