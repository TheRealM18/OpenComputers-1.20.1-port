package li.cil.oc.client;

import li.cil.oc.Settings;
import li.cil.oc.common.CompressedPacketBuilder;
import li.cil.oc.common.PacketType;
import li.cil.oc.common.SimplePacketBuilder;
import li.cil.oc.common.container.ContainerTypes;
import li.cil.oc.common.entity.Drone;
import li.cil.oc.common.tileentity.*;
import li.cil.oc.common.tileentity.traits.Computer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.UUID;

public final class PacketSender {
    // Prevent instantiation
    private PacketSender() {}

    public static void sendComputerPower(Computer computer, boolean power) {
        val pb = new SimplePacketBuilder(PacketType.ComputerPower);
        pb.writeBlockPos(computer.getBlockPos());
        pb.writeBoolean(power);
        pb.sendToServer();
    }

    public static void sendComputerState(Computer computer, boolean state) {
        val pb = new SimplePacketBuilder(PacketType.ComputerState);
        pb.writeBlockPos(computer.getBlockPos());
        pb.writeBoolean(state);
        pb.sendToServer();
    }

    public static void sendKeyDown(Computer computer, int keyCode, char keyChar) {
        if (computer != null) {
            val pb = new SimplePacketBuilder(PacketType.KeyDown);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeInt(keyCode);
            pb.writeChar(keyChar);
            pb.sendToServer();
        }
    }

    public static void sendKeyUp(Computer computer, int keyCode, char keyChar) {
        if (computer != null) {
            val pb = new SimplePacketBuilder(PacketType.KeyUp);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeInt(keyCode);
            pb.writeChar(keyChar);
            pb.sendToServer();
        }
    }

    public static void sendClipboard(Computer computer, String value) {
        if (computer != null) {
            val pb = new CompressedPacketBuilder(PacketType.Clipboard);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeUTF(value);
            pb.sendToServer();
        }
    }

    public static void sendMouseClick(Computer computer, int x, int y, int drag, int button) {
        if (computer != null) {
            val pb = new SimplePacketBuilder(PacketType.MouseClickOrDrag);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeInt(x);
            pb.writeInt(y);
            pb.writeInt(drag);
            pb.writeByte(button);
            pb.sendToServer();
        }
    }

    public static void sendMouseScroll(Computer computer, int x, int y, int scroll) {
        if (computer != null) {
            val pb = new SimplePacketBuilder(PacketType.MouseScroll);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeInt(x);
            pb.writeInt(y);
            pb.writeInt(scroll);
            pb.sendToServer();
        }
    }

    public static void sendMouseUp(Computer computer, int x, int y, int drag, int button) {
        if (computer != null) {
            val pb = new SimplePacketBuilder(PacketType.MouseUp);
            pb.writeBlockPos(computer.getBlockPos());
            pb.writeInt(x);
            pb.writeInt(y);
            pb.writeInt(drag);
            pb.writeByte(button);
            pb.sendToServer();
        }
    }

    public static void sendMultiPlace() {
        val pb = new SimplePacketBuilder(PacketType.MultiPartPlace);
        pb.sendToServer();
    }

    public static void sendPetVisibility(boolean visible) {
        val pb = new SimplePacketBuilder(PacketType.PetVisibility);
        pb.writeBoolean(visible);
        pb.sendToServer();
    }

    public static void sendRobotStateRequest(ResourceLocation dimension, BlockPos pos) {
        val pb = new SimplePacketBuilder(PacketType.RobotStateRequest);
        pb.writeResourceLocation(dimension);
        pb.writeBlockPos(pos);
        pb.sendToServer();
    }

    public static void sendServerPower(ServerRack rack, int slot, boolean power) {
        if (rack != null) {
            val pb = new SimplePacketBuilder(PacketType.ServerPower);
            pb.writeBlockPos(rack.getBlockPos());
            pb.writeByte(slot);
            pb.writeBoolean(power);
            pb.sendToServer();
        }
    }

    public static void sendTextBufferInit(TextBuffer buffer) {
        if (buffer != null) {
            val pb = new SimplePacketBuilder(PacketType.TextBufferInit);
            pb.writeBlockPos(buffer.getBlockPos());
            pb.sendToServer();
        }
    }

    public static void sendWaypointLabel(Waypoint waypoint, String label) {
        if (waypoint != null) {
            val pb = new SimplePacketBuilder(PacketType.WaypointLabel);
            pb.writeBlockPos(waypoint.getBlockPos());
            pb.writeUTF(label);
            pb.sendToServer();
        }
    }

    public static void sendWirelessRedstoneEnderCheck(Relay relay, UUID enderAccessId) {
        if (relay != null) {
            val pb = new SimplePacketBuilder(PacketType.WirelessRedstoneEnderCheck);
            pb.writeBlockPos(relay.getBlockPos());
            pb.writeUUID(enderAccessId);
            pb.sendToServer();
        }
    }

    public static void sendWirelessRedstoneEnderConfigure(Relay relay, short index, UUID enderAccessId, String name) {
        if (relay != null) {
            val pb = new SimplePacketBuilder(PacketType.WirelessRedstoneEnderConfigure);
            pb.writeBlockPos(relay.getBlockPos());
            pb.writeShort(index);
            pb.writeUUID(enderAccessId);
            pb.writeUTF(name);
            pb.sendToServer();
        }
    }

    public static void sendWirelessRedstoneEnderFetch(Relay relay, short index, UUID enderAccessId) {
        if (relay != null) {
            val pb = new SimplePacketBuilder(PacketType.WirelessRedstoneEnderFetch);
            pb.writeBlockPos(relay.getBlockPos());
            pb.writeShort(index);
            pb.writeUUID(enderAccessId);
            pb.sendToServer();
        }
    }

    public static void playParticleEffect(BlockPos pos, int type) {
        val pb = new SimplePacketBuilder(PacketType.ParticleEffect);
        pb.writeBlockPos(pos);
        pb.writeInt(type);
        pb.sendToServer();
    }

    public static void playSound(BlockPos pos, String name, float volume, float pitch) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().getSoundManager().play(
                new SimpleSound(
                    new ResourceLocation(name),
                    SoundCategory.BLOCKS,
                    volume,
                    pitch,
                    pos.getX() + 0.5f,
                    pos.getY() + 0.5f,
                    pos.getZ() + 0.5f
                )
            );
        }
    }
}
