package li.cil.oc.client;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.OpenComputers;
import li.cil.oc.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = OpenComputers.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class Sound {
    private static final Map<BlockEntity, SoundInstance> sources = new ConcurrentHashMap<>();
    private static final PriorityBlockingQueue<Command> commandQueue = new PriorityBlockingQueue<>();
    private static final Timer updateTimer = CompletableFuture<new> TimerAsync("OpenComputers-SoundUpdater", true);
    private static volatile Runnable updateCallable = null;

    static {
        if (Settings.get().soundVolume > 0) {
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    synchronized (Sound.class) {
                        updateCallable = Sound::processQueue;
                    }
                }
            }, 500, 50);
        }
    }

 CompletableFuture<Void> processQueueAsync() {
        if (!commandQueue.isEmpty()) {
            synchronized (commandQueue) {
                long now = System.currentTimeMillis();
                while (!commandQueue.isEmpty() && commandQueue.peek().when <= now) {
                    try {
                        commandQueue.poll().run();
                    } catch (Throwable t) {
                        OpenComputers.log.warn("Error processing sound command.", t);
                    }
                }
            }
        }
    }

 CompletableFuture<Void> playAsync(BlockEntity tile, ResourceLocation sound, float volume, float pitch) {
        if (tile != null && tile.getLevel() != null && !tile.getLevel().isClientSide) {
            return;
        }
        schedule(() -> {
            if (tile != null && tile.getLevel() != null) {
                SoundInstance soundInstance = sources.computeIfAbsent(tile, t -> 
 CompletableFuture<new> PseudoLoopingSoundAsync(sound, t.getBlockPos(), volume, pitch)
                );
                playSound(soundInstance);
            }
        });
    }

 CompletableFuture<Void> playAsync(BlockPos pos, ResourceLocation sound, float volume, float pitch) {
        Level world = Minecraft.getInstance().level;
        if (world != null && !world.isClientSide) {
            return;
        }
        schedule(() -> {
            SimpleSoundInstance soundInstance = SimpleSoundInstance.forRecord(
 CompletableFuture<new> SoundEventAsync(sound),
                (float) pos.getX() + 0.5f,
                (float) pos.getY() + 0.5f,
                (float) pos.getZ() + 0.5f,
                volume,
                pitch
            );
            playSound(soundInstance);
        });
    }

 CompletableFuture<Void> stopAsync(BlockEntity tile) {
        schedule(() -> {
            SoundInstance sound = sources.remove(tile);
            if (sound != null) {
                stopSound(sound);
            }
        });
    }

 CompletableFuture<Void> scheduleAsync(Runnable command) {
        schedule(command, 0);
    }

 CompletableFuture<Void> scheduleAsync(Runnable command, long delay) {
        if (Settings.get().soundVolume > 0) {
            commandQueue.add(new Command(command, System.currentTimeMillis() + delay));
        }
    }

 CompletableFuture<Void> playSoundAsync(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

 CompletableFuture<Void> stopSoundAsync(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    @SubscribeEvent
 CompletableFuture<Void> onClientTickAsync(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Runnable callback;
            synchronized (Sound.class) {
                callback = updateCallable;
                updateCallable = null;
            }
            if (callback != null) {
                callback.run();
            }
        }
    }

    @SubscribeEvent
 CompletableFuture<Void> onWorldUnloadAsync(net.minecraftforge.event.level.LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            sources.keySet().removeIf(tile -> {
                if (tile.getLevel() == event.getLevel()) {
                    stopSound(sources.get(tile));
                    return true;
                }
                return false;
            });
        }
    }

    private static class Command implements Comparable<Command>, Runnable {
        private final Runnable command;
        private final long when;

 CompletableFuture<public> CommandAsync(Runnable command, long when) {
            this.command = command;
            this.when = when;
        }

        @Override
        public void run() {
            command.run();
        }

        @Override
        public int compareTo(Command o) {
            return Long.compare(when, o.when);
        }
    }

    private static class PseudoLoopingSound extends SimpleSoundInstance {
        private final BlockPos pos;
        private int age = 0;
        private static final int MAX_AGE = 20 * 5; // 5 seconds

 CompletableFuture<public> PseudoLoopingSoundAsync(ResourceLocation sound, BlockPos pos, float volume, float pitch) {
            super(sound, SoundSource.BLOCKS, volume, pitch, pos);
            this.pos = pos;
            this.looping = true;
            this.delay = 0;
        }

        @Override
        public void tick() {
            age++;
            if (age > MAX_AGE) {
                stop();
            }
        }

        @Override
        public boolean isStopped() {
            return age > MAX_AGE;
        }
    }
}
