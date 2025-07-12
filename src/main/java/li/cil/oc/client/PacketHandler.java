package li.cil.oc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.oc.Localization;
import li.cil.oc.OpenComputers;
import li.cil.oc.Settings;
import li.cil.oc.api.event.FileSystemAccessEvent;
import li.cil.oc.api.event.NetworkActivityEvent;
import li.cil.oc.client.renderer.PetRenderer;
import li.cil.oc.common.Loot;
import li.cil.oc.common.PacketType;
import li.cil.oc.common.PacketHandler;
import li.cil.oc.common.item.Tablet;
import li.cil.oc.common.nanomachines.ControllerImpl;
import li.cil.oc.common.tileentity.*;
import li.cil.oc.common.tileentity.traits.*;
import li.cil.oc.integration.Mods;
import li.cil.oc.integration.jei.ModJEI;
import li.cil.oc.util.Audio;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class PacketHandler extends CommonPacketHandler {
    @Override
    protected Optional<World> world(PlayerEntity player, ResourceLocation dimension) {
        World world = player.level;
        return world.dimension().location().equals(dimension) ? Optional.of(world) : Optional.empty();
    }

    @Override
    protected void dispatch(CommonPacketHandler.PacketParser p) {
        try {
            switch (p.packetType) {
                case AdapterState -> onAdapterState(p);
                case Analyze -> onAnalyze(p);
                case ChargerState -> onChargerState(p);
                case ClientLog -> onClientLog(p);
                case Clipboard -> onClipboard(p);
                case ColorChange -> onColorChange(p);
                case ComputerState -> onComputerState(p);
                case ComputerUserList -> onComputerUserList(p);
                case ContainerUpdate -> onContainerUpdate(p);
                case CyclingDisk -> onCyclingDisk(p);
                case DisassemblerActiveChange -> onDisassemblerActiveChange(p);
                case DiskDriveMounts -> onDiskDriveMounts(p);
                case DriveMount -> onDriveMount(p);
                case DriveUnmount -> onDriveUnmount(p);
                case FileSystemActivity -> onFileSystemActivity(p);
                case FloppyChange -> onFloppyChange(p);
                case HologramArea -> onHologramArea(p);
                case HologramClear -> onHologramClear(p);
                case HologramColor -> onHologramColor(p);
                case HologramPositionOffsetY -> onHologramPositionOffsetY(p);
                case HologramPowerChange -> onHologramPowerChange(p);
                case HologramRotation -> onHologramRotation(p);
                case HologramRotationSpeed -> onHologramRotationSpeed(p);
                case HologramScale -> onHologramScale(p);
                case HologramValues -> onHologramValues(p);
                case LootDisk -> onLootDisk(p);
                case NanomachinesConfiguration -> onNanomachinesConfiguration(p);
                case NanomachinesInputs -> onNanomachinesInputs(p);
                case NanomachinesPower -> onNanomachinesPower(p);
                case NetworkActivity -> onNetworkActivity(p);
                case NetSplitterState -> onNetSplitterState(p);
                case ParticleEffect -> onParticleEffect(p);
                case PetVisibility -> onPetVisibility(p);
                case PowerState -> onPowerState(p);
                case PrinterState -> onPrinterState(p);
                case RaidState -> onRaidState(p);
                case RaidData -> onRaidData(p);
                case RedstoneState -> onRedstoneState(p);
                case RobotAnimateSwing -> onRobotAnimateSwing(p);
                case RobotAnimateTurn -> onRobotAnimateTurn(p);
                case RobotLightChange -> onRobotLightChange(p);
                case RobotMove -> onRobotMove(p);
                case RobotSelectedSlotChange -> onRobotSelectedSlotChange(p);
                case RobotXp -> onRobotXp(p);
                case ScreenColorChange -> onScreenColorChange(p);
                case ScreenCopy -> onScreenCopy(p);
                case ScreenDepth -> onScreenDepth(p);
                case ScreenFill -> onScreenFill(p);
                case ScreenPowerChange -> onScreenPowerChange(p);
                case ScreenResolutionChange -> onScreenResolutionChange(p);
                case ScreenSet -> onScreenSet(p);
                case Sound -> onSound(p);
                case SoundDisposable -> onSoundDisposable(p);
                case SoundPattern -> onSoundPattern(p);
                case TabletPC -> onTabletPC(p);
                case TextBufferInit -> onTextBufferInit(p);
                case TextBufferMulti -> onTextBufferMulti(p);
                case TextBufferPowerChange -> onTextBufferPowerChange(p);
                case WaypointLabel -> onWaypointLabel(p);
                case WaypointLabelTarget -> onWaypointLabelTarget(p);
                case WaypointPower -> onWaypointPower(p);
                case WaypointVisibility -> onWaypointVisibility(p);
                case WirelessNetworkDebug -> onWirelessNetworkDebug(p);
                case WirelessNetworkStrength -> onWirelessNetworkStrength(p);
                case WirelessNetworkTarget -> onWirelessNetworkTarget(p);
                default -> OpenComputers.log().warn("Unhandled packet type: " + p.packetType);
            }
        } catch (final Throwable t) {
            OpenComputers.log().warn("Error handling packet type " + p.packetType, t);
        }
    }

    @Override
    protected CommonPacketHandler.PacketParser createParser(InputStream stream, PlayerEntity player) {
        return new PacketParser(stream, player);
    }

    private void onAdapterState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Adapter.class).ifPresent(t -> {
            t.openSides = t.uncompressSides(p.readByte());
            t.getLevel().sendBlockUpdated(t.getBlockPos(), t.getBlockState(), t.getBlockState(), 3);
        });
    }

    private void onAnalyze(CommonPacketHandler.PacketParser p) throws IOException {
        String address = p.readUTF();
        if (KeyBindings.isAnalyzeCopyingAddress) {
            RenderSystem.recordRenderCall(() -> {
                Minecraft mc = Minecraft.getInstance();
                mc.keyboardHandler.setClipboard(address);
                mc.gui.handleChat(ChatType.SYSTEM, Localization.Analyzer.AddressCopied, Util.NIL_UUID);
            });
        }
    }

    private void onChargerState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Charger.class).ifPresent(t -> {
            t.chargeSpeed = p.readDouble();
            t.hasPower = p.readBoolean();
            t.getLevel().sendBlockUpdated(t.getBlockPos(), t.getBlockState(), t.getBlockState(), 3);
        });
    }

    private void onClientLog(CommonPacketHandler.PacketParser p) throws IOException {
        OpenComputers.log.info(p.readUTF());
    }

    private void onClipboard(CommonPacketHandler.PacketParser p) throws IOException {
        String contents = p.readUTF();
        RenderSystem.recordRenderCall(() -> Minecraft.getInstance().keyboardHandler.setClipboard(contents));
    }

    private void onColorChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Colored.class).ifPresent(t -> {
            t.setColor(p.readInt());
            t.getLevel().sendBlockUpdated(t.getBlockPos(), t.getBlockState(), t.getBlockState(), 3);
        });
    }

    private void onComputerState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Computer.class).ifPresent(t -> {
            t.setRunning(p.readBoolean());
            t.hasErrored = p.readBoolean();
        });
    }

    private void onComputerUserList(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Computer.class).ifPresent(t -> {
            int count = p.readInt();
            String[] users = new String[count];
            for (int i = 0; i < count; i++) {
                users[i] = p.readUTF();
            }
            t.setUsers(users);
        });
    }

    private void onContainerUpdate(CommonPacketHandler.PacketParser p) throws IOException {
        int containerId = p.readInt();
        if (p.player.containerMenu != null && p.player.containerMenu.containerId == containerId && p.player.containerMenu instanceof container.Player) {
            ((container.Player) p.player.containerMenu).updateCustomData(p.readNBT());
        }
    }

    private void onDisassemblerActiveChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Disassembler.class).ifPresent(t -> t.isActive = p.readBoolean());
    }

    private void onFileSystemActivity(CommonPacketHandler.PacketParser p) throws IOException {
        String sound = p.readUTF();
        CompoundNBT data = p.readNBT();
        if (p.readBoolean()) {
            p.readBlockEntity(net.minecraft.tileentity.TileEntity.class).ifPresent(t ->
                MinecraftForge.EVENT_BUS.post(new FileSystemAccessEvent.Client(sound, t, data)));
        } else {
            world(p.player, new ResourceLocation(p.readUTF())).ifPresent(world -> {
                double x = p.readDouble();
                double y = p.readDouble();
                double z = p.readDouble();
                MinecraftForge.EVENT_BUS.post(new FileSystemAccessEvent.Client(sound, world, x, y, z, data));
            });
        }
    }

    private void onNetworkActivity(CommonPacketHandler.PacketParser p) throws IOException {
        CompoundNBT data = p.readNBT();
        if (p.readBoolean()) {
            p.readBlockEntity(net.minecraft.tileentity.TileEntity.class).ifPresent(t ->
                MinecraftForge.EVENT_BUS.post(new NetworkActivityEvent.Client(t, data)));
        } else {
            world(p.player, new ResourceLocation(p.readUTF())).ifPresent(world -> {
                double x = p.readDouble();
                double y = p.readDouble();
                double z = p.readDouble();
                MinecraftForge.EVENT_BUS.post(new NetworkActivityEvent.Client(world, x, y, z, data));
            });
        }
    }

    private void onFloppyChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(t -> t.setItem(0, p.readItem()));
    }

    private void onHologramClear(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            for (int i = 0; i < t.volume.length; i++) {
                t.volume[i] = 0;
            }
            t.needsRendering = true;
        });
    }

    private void onHologramColor(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            int index = p.readInt();
            int value = p.readInt();
            t.colors[index] = value & 0xFFFFFF;
            t.needsRendering = true;
        });
    }

    private void onHologramPowerChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> t.hasPower = p.readBoolean());
    }

    private void onHologramScale(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> t.scale = p.readDouble());
    }

    private void onHologramArea(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            int fromX = p.readByte() & 0xFF;
            int untilX = p.readByte() & 0xFF;
            int fromZ = p.readByte() & 0xFF;
            int untilZ = p.readByte() & 0xFF;
            for (int x = fromX; x < untilX; x++) {
                for (int z = fromZ; z < untilZ; z++) {
                    t.volume[x + z * t.width] = p.readInt();
                    t.volume[x + z * t.width + t.width * t.width] = p.readInt();
                }
            }
            t.needsRendering = true;
        });
    }

    private void onHologramValues(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            int count = p.readInt();
            for (int i = 0; i < count; i++) {
                int xz = p.readShort() & 0xFFFF;
                int x = (xz >> 8) & 0xFF;
                int z = xz & 0xFF;
                t.volume[x + z * t.width] = p.readInt();
                t.volume[x + z * t.width + t.width * t.width] = p.readInt();
            }
            t.needsRendering = true;
        });
    }

    private void onHologramPositionOffsetY(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            double x = p.readDouble();
            double y = p.readDouble();
            double z = p.readDouble();
            t.translation = new Vector3d(x, y, z);
        });
    }

    private void onHologramRotation(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            t.rotationAngle = p.readFloat();
            t.rotationX = p.readFloat();
            t.rotationY = p.readFloat();
            t.rotationZ = p.readFloat();
        });
    }

    private void onHologramRotationSpeed(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(t -> {
            t.rotationSpeed = p.readFloat();
            t.rotationSpeedX = p.readFloat();
            t.rotationSpeedY = p.readFloat();
            t.rotationSpeedZ = p.readFloat();
        });
    }

    private void onLootDisk(CommonPacketHandler.PacketParser p) throws IOException {
        ItemStack stack = p.readItem();
        if (!stack.isEmpty()) {
            Loot.disksForClient.add(stack);
            if (Mods.JustEnoughItems.isModAvailable()) {
                ModJEI.addDiskAtRuntime(stack);
            }
        }
    }

    private void onCyclingDisk(CommonPacketHandler.PacketParser p) throws IOException {
        ItemStack stack = p.readItem();
        if (!stack.isEmpty()) {
            Loot.disksForCyclingClient.add(stack);
        }
    }

    private void onNanomachinesConfiguration(CommonPacketHandler.PacketParser p) throws IOException {
        p.readEntity(PlayerEntity.class).ifPresent(player -> {
            boolean hasController = p.readBoolean();
            if (hasController) {
                api.Nanomachines.installController(player).ifPresent(controller -> {
                    if (controller instanceof ControllerImpl) {
                        ((ControllerImpl) controller).loadData(p.readNBT());
                    }
                });
            } else {
                api.Nanomachines.uninstallController(player);
            }
        });
    }

    private void onNanomachinesInputs(CommonPacketHandler.PacketParser p) throws IOException {
        p.readEntity(PlayerEntity.class).ifPresent(player -> {
            api.Nanomachines.getController(player).ifPresent(controller -> {
                if (controller instanceof ControllerImpl) {
                    ControllerImpl impl = (ControllerImpl) controller;
                    int count = p.readInt();
                    byte[] inputs = new byte[count];
                    p.readFully(inputs);
                    synchronized (impl.configuration) {
                        for (int i = 0; i < Math.min(inputs.length, impl.configuration.triggers.length); i++) {
                            impl.configuration.triggers[i].isActive = inputs[i] == 1;
                        }
                        impl.activeBehaviorsDirty = true;
                    }
                }
            });
        });
    }

    private void onNanomachinesPower(CommonPacketHandler.PacketParser p) throws IOException {
        p.readEntity(PlayerEntity.class).ifPresent(player -> {
            api.Nanomachines.getController(player).ifPresent(controller -> {
                if (controller instanceof ControllerImpl) {
                    ((ControllerImpl) controller).storedEnergy = p.readDouble();
                }
            });
        });
    }

    private void onNetSplitterState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(NetSplitter.class).ifPresent(t -> {
            t.isInverted = p.readBoolean();
            t.openSides = t.uncompressSides(p.readByte());
            t.getLevel().sendBlockUpdated(t.getBlockPos(), t.getBlockState(), t.getBlockState(), 3);
        });
    }

    private void onParticleEffect(CommonPacketHandler.PacketParser p) throws IOException {
        world(p.player, new ResourceLocation(p.readUTF())).ifPresent(world -> {
            int x = p.readInt();
            int y = p.readInt();
            int z = p.readInt();
            double velocity = p.readDouble();
            Optional<Direction> direction = p.readDirection();
            IParticleData particleType = p.readRegistryEntry(ForgeRegistries.PARTICLE_TYPES);
            if (particleType != null) {
                int count = p.readUnsignedByte();
                for (int i = 0; i < count; i++) {
                    double vx = direction.map(d -> world.random.nextFloat() - 0.5 + d.getStepX() * 0.5)
                            .orElseGet(() -> world.random.nextFloat() * 2.0 - 1);
                    double vy = direction.map(d -> world.random.nextFloat() - 0.5 + d.getStepY() * 0.5)
                            .orElseGet(() -> world.random.nextFloat() * 2.0 - 1);
                    double vz = direction.map(d -> world.random.nextFloat() - 0.5 + d.getStepZ() * 0.5)
                            .orElseGet(() -> world.random.nextFloat() * 2.0 - 1);

                    if (vx * vx + vy * vy + vz * vz < 1) {
                        double px = x + 0.5 + vx * velocity * (direction.isPresent() ? 0.5 : 1);
                        double py = y + 0.5 + vy * velocity * (direction.isPresent() ? 0.5 : 1);
                        double pz = z + 0.5 + vz * velocity * (direction.isPresent() ? 0.5 : 1);
                        world.addParticle(particleType, px, py, pz, vx, vy + velocity * 0.25, vz);
                    }
                }
            }
        });
    }

    private void onPetVisibility(CommonPacketHandler.PacketParser p) throws IOException {
        if (!PetRenderer.isInitialized) {
            PetRenderer.isInitialized = true;
            if (Settings.get().hideOwnPet()) {
                PetRenderer.hidden.add(Minecraft.getInstance().player.getName().getString());
            }
            PacketSender.sendPetVisibility();
        }

        int count = p.readInt();
        for (int i = 0; i < count; i++) {
            String name = p.readUTF();
            if (p.readBoolean()) {
                PetRenderer.hidden.remove(name);
            } else {
                PetRenderer.hidden.add(name);
            }
        }
    }

    private void onPowerState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(PowerInformation.class).ifPresent(t -> {
            t.globalBuffer = p.readDouble();
            t.globalBufferSize = p.readDouble();
        });
    }

    private void onPrinterState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Printer.class).ifPresent(t -> {
            t.requiredEnergy = p.readBoolean() ? 9001 : 0;
        });
    }

    private void onRaidState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Raid.class).ifPresent(t -> {
            t.setStatus(p.readInt(), p.readUTF());
        });
    }

    private void onRaidData(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Raid.class).ifPresent(t -> {
            t.setData(p.readNBT());
        });
    }

    private void onRedstoneState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RedstoneAware.class).ifPresent(t -> {
            t.setOutput(p.readByte() & 0xFF);
        });
    }

    private void onRobotAnimateSwing(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.setSwingingArms(true);
                t.robot.swingTime = 5;
            }
        });
    }

    private void onRobotAnimateTurn(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.turnAxis = p.readByte();
                t.robot.turnDirection = p.readByte();
                t.robot.turnSpeed = p.readFloat();
                t.robot.turnSteps = p.readByte() & 0xFF;
            }
        });
    }

    private void onRobotMove(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.moveForward = p.readByte();
                t.robot.moveStrafing = p.readByte();
                t.robot.moveVertical = p.readByte();
                t.robot.moveTime = 10;
            }
        });
    }

    private void onRobotSelectedSlotChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.setSelectedSlot(p.readByte() & 0xFF);
            }
        });
    }

    private void onRobotXp(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.xp = p.readFloat();
            }
        });
    }

    private void onHologramArea(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            int index = p.readInt();
            int width = p.readInt();
            int height = p.readInt();
            hologram.setArea(index, width, height);
        });
    }

    private void onHologramClear(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(Hologram::clear);
    }

    private void onHologramColor(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            int index = p.readInt();
            int value = p.readInt();
            hologram.setColor(index, value);
        });
    }

    private void onHologramPositionOffsetY(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            float value = p.readFloat();
            hologram.setPositionOffsetY(value);
        });
    }

    private void onHologramRotation(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            float angle = p.readFloat();
            float x = p.readFloat();
            float y = p.readFloat();
            float z = p.readFloat();
            hologram.setRotation(angle, x, y, z);
        });
    }

    private void onHologramRotationSpeed(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            float value = p.readFloat();
            hologram.setRotationSpeed(value);
        });
    }

    private void onHologramScale(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            float value = p.readFloat();
            hologram.setScale(value);
        });
    }

    private void onHologramValues(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            int index = p.readInt();
            int count = p.readInt();
            byte[] values = p.readByteArray();
            hologram.setValues(index, values);
        });
    }

    private void onScreenColorChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int foreground = p.readInt();
            int background = p.readInt();
            screen.setColor(foreground, background);
        });
    }

    private void onScreenCopy(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            int w = p.readInt();
            int h = p.readInt();
            int tx = p.readInt();
            int ty = p.readInt();
            screen.copy(col, row, w, h, tx, ty);
        });
    }

    private void onScreenDepth(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            Screen.Tier tier = Screen.Tier.values()[p.readInt()];
            screen.setTier(tier);
        });
    }

    private void onScreenFill(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            int w = p.readInt();
            int h = p.readInt();
            char c = p.readChar();
            screen.fill(col, row, w, h, c);
        });
    }

    private void onScreenPowerChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            boolean hasPower = p.readBoolean();
            screen.setHasPower(hasPower);
        });
    }

    private void onScreenResolutionChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int w = p.readInt();
            int h = p.readInt();
            screen.setResolution(w, h);
        });
    }

    private void onScreenSet(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            int width = p.readInt();
            String value = p.readUTF();
            screen.set(col, row, width, value);
        });
    }

    private void onWaypointLabel(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            String label = p.readUTF();
            waypoint.setLabel(label);
        });
    }

    private void onWaypointLabelTarget(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            String label = p.readUTF();
            waypoint.setLabelTarget(label);
        });
    }

    private void onWaypointPower(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            boolean hasPower = p.readBoolean();
            waypoint.setHasPower(hasPower);
        });
    }

    private void onWaypointVisibility(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            boolean isVisible = p.readBoolean();
            waypoint.setVisible(isVisible);
        });
    }

    private void onWirelessNetworkDebug(CommonPacketHandler.PacketParser p) throws IOException {
        // Toggle wireless network debug rendering
        boolean enabled = p.readBoolean();
        // Implementation would go here to toggle debug rendering
    }

    private void onWirelessNetworkStrength(CommonPacketHandler.PacketParser p) throws IOException {
        // Update wireless network strength for a position
        BlockPos pos = p.readBlockPos();
        double strength = p.readDouble();
        // Implementation would go here to update strength visualization
    }

    private void onWirelessNetworkTarget(CommonPacketHandler.PacketParser p) throws IOException {
        // Set wireless network target
        BlockPos pos = p.readBlockPos();
        BlockPos target = p.readBoolean() ? p.readBlockPos() : null;
        // Implementation would go here to set target visualization
    }

    private void onTabletPC(CommonPacketHandler.PacketParser p) throws IOException {
        // Handle tablet PC state updates
        ItemStack stack = p.readItemStack();
        if (!stack.isEmpty()) {
            TabletWrapper.Data data = new TabletWrapper.Data(stack);
            data.load(p.readNBT());
            // Update tablet state
        }
    }

    private void onSoundDisposable(CommonPacketHandler.PacketParser p) throws IOException {
        // Play a disposable sound effect
        String sound = p.readUTF();
        float volume = p.readFloat();
        float pitch = p.readFloat();
        
        if (Minecraft.getInstance().level != null) {
            BlockPos pos = p.readBlockPos();
            Minecraft.getInstance().level.playLocalSound(
                pos.getX() + 0.5, 
                pos.getY() + 0.5, 
                pos.getZ() + 0.5,
                new ResourceLocation(sound),
                SoundSource.BLOCKS,
                volume,
                pitch,
                false
            );
        }
    }

    private void onRaidState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Raid.class).ifPresent(raid -> {
            boolean hasDisk = p.readBoolean();
            raid.setHasDisk(hasDisk);
        });
    }

    private void onRaidData(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Raid.class).ifPresent(raid -> {
            int size = p.readInt();
            byte[] data = p.readByteArray();
            raid.setData(data);
        });
    }

    private void onPrinterState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Printer.class).ifPresent(printer -> {
            boolean isPrinting = p.readBoolean();
            printer.setPrinting(isPrinting);
        });
    }

    private void onParticleEffect(CommonPacketHandler.PacketParser p) throws IOException {
        if (Minecraft.getInstance().level != null) {
            String name = p.readUTF();
            double x = p.readDouble();
            double y = p.readDouble();
            double z = p.readDouble();
            double vx = p.readDouble();
            double vy = p.readDouble();
            double vz = p.readDouble();
            
            // Implementation would create the appropriate particle effect
            // This is a simplified example
            Minecraft.getInstance().particleEngine.createParticle(
                new ResourceLocation(name),
                x, y, z,
                vx, vy, vz
            );
        }
    }

    private void onPetVisibility(CommonPacketHandler.PacketParser p) throws IOException {
        boolean visible = p.readBoolean();
        PetRenderer.setRenderingEnabled(visible);
    }

    private void onHologramPowerChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Hologram.class).ifPresent(hologram -> {
            boolean hasPower = p.readBoolean();
            hologram.setHasPower(hasPower);
        });
    }

    private void onFileSystemActivity(CommonPacketHandler.PacketParser p) throws IOException {
        BlockPos pos = p.readBlockPos();
        // Implementation would update activity indicators
    }

    private void onFloppyChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(diskDrive -> {
            int slot = p.readInt();
            ItemStack stack = p.readItemStack();
            diskDrive.setItem(slot, stack);
        });
    }

    private void onDisassemblerActiveChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Disassembler.class).ifPresent(disassembler -> {
            boolean active = p.readBoolean();
            disassembler.setActive(active);
        });
    }

    private void onCyclingDisk(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(diskDrive -> {
            int slot = p.readInt();
            diskDrive.setSelectedSlot(slot);
        });
    }

    private void onContainerUpdate(CommonPacketHandler.PacketParser p) throws IOException {
        // Handle container updates
        int windowId = p.readInt();
        int slot = p.readInt();
        ItemStack stack = p.readItemStack();
        
        if (Minecraft.getInstance().player != null && 
            Minecraft.getInstance().player.containerMenu != null &&
            Minecraft.getInstance().player.containerMenu.containerId == windowId) {
            Minecraft.getInstance().player.containerMenu.setItem(slot, 0, stack);
        }
    }

    private void onComputerUserList(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Computer.class).ifPresent(computer -> {
            int count = p.readInt();
            Set<UUID> users = new HashSet<>();
            for (int i = 0; i < count; i++) {
                users.add(p.readUUID());
            }
            computer.setUsers(users);
        });
    }

    private void onComputerState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Computer.class).ifPresent(computer -> {
            boolean isOn = p.readBoolean();
            computer.setRunning(isOn);
        });
    }

    private void onColorChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Colored.class).ifPresent(colored -> {
            int color = p.readInt();
            colored.setColor(color);
        });
    }

    private void onClipboard(CommonPacketHandler.PacketParser p) throws IOException {
        if (Minecraft.getInstance().player != null) {
            String value = p.readUTF();
            Minecraft.getInstance().keyboardHandler.setClipboard(value);
        }
    }

    private void onClientLog(CommonPacketHandler.PacketParser p) throws IOException {
        String message = p.readUTF();
        OpenComputers.log().info("[CLIENT] " + message);
    }

    private void onChargerState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Charger.class).ifPresent(charger -> {
            boolean isCharging = p.readBoolean();
            charger.setCharging(isCharging);
        });
    }

    private void onAnalyze(CommonPacketHandler.PacketParser p) throws IOException {
        BlockPos pos = p.readBlockPos();
        String address = p.readUTF();
        String name = p.readUTF();
        
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal(Strings.repeat("-", 30)));
            lines.add(Component.literal(name).withStyle(ChatFormatting.GOLD));
            lines.add(Component.literal(Strings.repeat("-", 30)));
            lines.add(Component.literal("Address: " + address).withStyle(ChatFormatting.GRAY));
            
            // Show the message above the hotbar
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("Analyzed " + name), false);
            
            // Implementation for showing analysis results would go here
        }
    }

    private void onAdapterState(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Adapter.class).ifPresent(adapter -> {
            boolean isActive = p.readBoolean();
            adapter.setActive(isActive);
        });
    }

    private void onDiskDriveMounts(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(diskDrive -> {
            int count = p.readInt();
            diskDrive.setMountPoints(Arrays.asList(p.readArray(String[]::new, count, p::readUTF)));
        });
    }

    private void onDriveMount(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(diskDrive -> {
            String path = p.readUTF();
            String mountPoint = p.readUTF();
            diskDrive.mount(path, mountPoint);
        });
    }

    private void onDriveUnmount(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(DiskDrive.class).ifPresent(diskDrive -> {
            String mountPoint = p.readUTF();
            diskDrive.unmount(mountPoint);
        });
    }

    private void onRobotLightChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(RobotProxy.class).ifPresent(t -> {
            if (t.robot != null) {
                t.robot.setLightColor(p.readInt());
            }
        });
    }

    private void onScreenColorChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            boolean isBackground = p.readBoolean();
            int index = p.readInt();
            int value = p.readInt();
            if (isBackground) {
                screen.backgroundColors[index] = value;
            } else {
                screen.foregroundColors[index] = value;
            }
            screen.markForRenderUpdate();
        });
    }

    private void onScreenCopy(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            int w = p.readInt();
            int h = p.readInt();
            int tx = p.readInt();
            int ty = p.readInt();
            screen.copy(col, row, w, h, tx, ty);
            screen.markForRenderUpdate();
        });
    }

    private void onScreenDepth(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            screen.setTier(Screen.Tier.byTierIndex(p.readByte()));
        });
    }

    private void onScreenFill(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            int w = p.readInt();
            int h = p.readInt();
            String value = p.readUTF();
            screen.fill(col, row, w, h, value);
            screen.markForRenderUpdate();
        });
    }

    private void onScreenPowerChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            screen.hasPower = p.readBoolean();
            screen.markForRenderUpdate();
        });
    }

    private void onScreenResolutionChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            screen.setResolution(p.readInt(), p.readInt());
            screen.markForRenderUpdate();
        });
    }

    private void onScreenSet(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            int col = p.readInt();
            int row = p.readInt();
            String text = p.readUTF();
            boolean vertical = p.readBoolean();
            screen.set(col, row, text, vertical);
            screen.markForRenderUpdate();
        });
    }

    private void onSound(CommonPacketHandler.PacketParser p) throws IOException {
        ResourceLocation sound = new ResourceLocation(p.readUTF());
        float volume = p.readFloat();
        float pitch = p.readFloat();
        world(p.player, sound).ifPresent(world -> {
            double x = p.readDouble();
            double y = p.readDouble();
            double z = p.readDouble();
            world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(sound),
                    SoundCategory.BLOCKS, volume, pitch, false);
        });
    }

    private void onSoundDisposable(CommonPacketHandler.PacketParser p) throws IOException {
        ResourceLocation sound = new ResourceLocation(p.readUTF());
        float volume = p.readFloat();
        float pitch = p.readFloat();
        world(p.player, sound).ifPresent(world -> {
            double x = p.readDouble();
            double y = p.readDouble();
            double z = p.readDouble();
            Audio.playSound(world, x, y, z, sound, volume, pitch);
        });
    }

    private void onSoundPattern(CommonPacketHandler.PacketParser p) throws IOException {
        SoundEvent sound = p.readRegistryEntry(ForgeRegistries.SOUND_EVENTS);
        if (sound != null) {
            world(p.player, p.readResourceLocation()).ifPresent(world -> {
                double x = p.readDouble();
                double y = p.readDouble();
                double z = p.readDouble();
                float volume = p.readFloat();
                float pitch = p.readFloat();
                world.playLocalSound(x, y, z, sound, SoundCategory.BLOCKS, volume, pitch, false);
            });
        }
    }

    private void onTabletPC(CommonPacketHandler.PacketParser p) throws IOException {
        p.readEntity(PlayerEntity.class).ifPresent(player -> {
            ItemStack stack = p.readItem();
            if (!stack.isEmpty() && stack.getItem() instanceof Tablet) {
                OpenComputers.proxy.itemRenderer().setStack(stack, p.readUTF(), p.readInt(), p.readInt(),
                        p.readBoolean(), p.readBoolean(), p.readByte() != 0, p.readBoolean());
            }
        });
    }

    private void onTextBufferInit(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            screen.textBuffer = new TextBuffer(p.readInt(), p.readInt(), p.readBoolean());
            screen.markForRenderUpdate();
        });
    }

    private void onTextBufferPowerChange(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            if (screen.textBuffer != null) {
                screen.textBuffer.setPowerState(p.readBoolean());
                screen.markForRenderUpdate();
            }
        });
    }

    private void onTextBufferMulti(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Screen.class).ifPresent(screen -> {
            if (screen.textBuffer != null) {
                int count = p.readInt();
                for (int i = 0; i < count; i++) {
                    String name = p.readUTF();
                    switch (name) {
                        case "set" -> {
                            int col = p.readInt();
                            int row = p.readInt();
                            String text = p.readUTF();
                            boolean vertical = p.readBoolean();
                            screen.textBuffer.set(col, row, text, vertical);
                        }
                        case "copy" -> {
                            int col = p.readInt();
                            int row = p.readInt();
                            int w = p.readInt();
                            int h = p.readInt();
                            int tx = p.readInt();
                            int ty = p.readInt();
                            screen.textBuffer.copy(col, row, w, h, tx, ty);
                        }
                        case "fill" -> {
                            int col = p.readInt();
                            int row = p.readInt();
                            int w = p.readInt();
                            int h = p.readInt();
                            String value = p.readUTF();
                            screen.textBuffer.fill(col, row, w, h, value);
                        }
                        case "setForeground" -> screen.textBuffer.foreground = p.readInt();
                        case "setBackground" -> screen.textBuffer.background = p.readInt();
                        case "setForegroundColor" -> {
                            int index = p.readInt();
                            int color = p.readInt();
                            screen.textBuffer.foregroundColors[index] = color;
                        }
                        case "setBackgroundColor" -> {
                            int index = p.readInt();
                            int color = p.readInt();
                            screen.textBuffer.backgroundColors[index] = color;
                        }
                        case "setPaletteColor" -> {
                            int index = p.readInt();
                            int color = p.readInt();
                            screen.textBuffer.palette[index] = color;
                        }
                        case "setPaletteColorRGB" -> {
                            int index = p.readInt();
                            int r = p.readByte() & 0xFF;
                            int g = p.readByte() & 0xFF;
                            int b = p.readByte() & 0xFF;
                            screen.textBuffer.palette[index] = (r << 16) | (g << 8) | b | 0xFF000000;
                        }
                    }
                }
                screen.markForRenderUpdate();
            }
        });
    }

    private void onWaypointLabel(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            waypoint.label = p.readUTF();
            waypoint.setChanged();
        });
    }

    private void onWaypointLabelTarget(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            waypoint.label = p.readUTF();
            waypoint.target = p.readBlockPos();
            waypoint.dimension = new ResourceLocation(p.readUTF());
            waypoint.setChanged();
        });
    }

    private void onWaypointPower(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            waypoint.setPowerState(p.readBoolean());
        });
    }

    private void onWaypointVisibility(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(Waypoint.class).ifPresent(waypoint -> {
            waypoint.visible = p.readBoolean();
            waypoint.setChanged();
        });
    }

    private void onWirelessNetworkStrength(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(WirelessNetworkCard.class).ifPresent(card -> {
            card.strength = p.readInt();
        });
    }

    private void onWirelessNetworkTarget(CommonPacketHandler.PacketParser p) throws IOException {
        p.readBlockEntity(WirelessNetworkCard.class).ifPresent(card -> {
            card.target = p.readBlockPos();
            card.dimension = new ResourceLocation(p.readUTF());
        });
    }

    private void onWirelessNetworkDebug(CommonPacketHandler.PacketParser p) throws IOException {
        if (Minecraft.getInstance().level != null) {
            int count = p.readInt();
            for (int i = 0; i < count; i++) {
                int x = p.readInt();
                int y = p.readInt();
                int z = p.readInt();
                int color = p.readInt();
                WirelessNetworkDebugRenderer.add(Minecraft.getInstance().level, x, y, z, color);
            }
        }
    }
    
    private static class PacketParser extends CommonPacketHandler.PacketParser {
        public PacketParser(InputStream stream, PlayerEntity player) {
            super(stream, player);
        }
    }
}
