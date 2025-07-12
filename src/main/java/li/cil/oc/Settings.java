package li.cil.oc;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.typesafe.config.*;
import li.cil.oc.common.Tier;
import li.cil.oc.server.component.DebugCard;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Settings {
    // Resource paths and namespaces
    public static final String RESOURCE_DOMAIN = OpenComputers.ID;
    public static final String NAMESPACE = "oc:";
    public static final String SAVE_PATH = "opencomputers/";
    public static final String SCRIPT_PATH = "/assets/" + RESOURCE_DOMAIN + "/lua/";
    
    // Configuration values (simplified - will be populated from config)
    public final double screenTextFadeStartDistance;
    public final double maxScreenTextRenderDistance;
    public final boolean textLinearFiltering;
    public final boolean textAntiAlias;
    public final boolean robotLabels;
    public final float soundVolume;
    public final double fontCharScale;
    public final double hologramFadeStartDistance;
    public final double hologramRenderDistance;
    public final double hologramFlickerFrequency;
    public final int monochromeColor;
    public final String fontRenderer;
    public final int beepSampleRate;
    public final int beepAmplitude;
    public final float beepRadius;
    public final double[] nanomachineHudPos;
    
    // Power settings
    public final double baseXpToLevel;
    public final double constantXpGrowth;
    public final double exponentialXpGrowth;
    public final double robotActionXp;
    
    // Network settings
    public final double[] maxWirelessRange;
    
    // Debug settings
    public final DebugCardAccess debugCardAccess;
    
    // Integration settings
    public final boolean enableCommandBlockDriver;
    public final boolean allowItemStackNBTTags;
    public final double costProgrammingTable;
    
    private Settings(Config config) {
        // Client settings
        this.screenTextFadeStartDistance = config.getDouble("client.screenTextFadeStartDistance");
        this.maxScreenTextRenderDistance = config.getDouble("client.maxScreenTextRenderDistance");
        this.textLinearFiltering = config.getBoolean("client.textLinearFiltering");
        this.textAntiAlias = config.getBoolean("client.textAntiAlias");
        this.robotLabels = config.getBoolean("client.robotLabels");
        this.soundVolume = (float) Math.max(0, Math.min(2, config.getDouble("client.soundVolume")));
        this.fontCharScale = Math.max(0.5, Math.min(2, config.getDouble("client.fontCharScale")));
        this.hologramFadeStartDistance = Math.max(0, config.getDouble("client.hologramFadeStartDistance"));
        this.hologramRenderDistance = Math.max(0, config.getDouble("client.hologramRenderDistance"));
        this.hologramFlickerFrequency = Math.max(0, config.getDouble("client.hologramFlickerFrequency"));
        this.monochromeColor = Integer.decode(config.getString("client.monochromeColor"));
        this.fontRenderer = config.getString("client.fontRenderer");
        this.beepSampleRate = config.getInt("client.beepSampleRate");
        this.beepAmplitude = Math.max(0, Math.min(Byte.MAX_VALUE, config.getInt("client.beepVolume")));
        this.beepRadius = (float) Math.max(1, Math.min(32, config.getDouble("client.beepRadius")));
        
        // Process nanomachine HUD position
        List<Double> hudPosList = config.getDoubleList("client.nanomachineHudPos");
        this.nanomachineHudPos = new double[]{
            hudPosList.size() > 0 ? hudPosList.get(0) : 0,
            hudPosList.size() > 1 ? hudPosList.get(1) : 0
        };
        
        // Robot XP settings
        this.baseXpToLevel = Math.max(0, config.getDouble("robot.xp.baseValue"));
        this.constantXpGrowth = Math.max(1, config.getDouble("robot.xp.constantGrowth"));
        this.exponentialXpGrowth = Math.max(1, config.getDouble("robot.xp.exponentialGrowth"));
        this.robotActionXp = Math.max(0, config.getDouble("robot.xp.actionXp"));
        
        // Wireless settings
        List<Double> wirelessRangeList = config.getDoubleList("misc.maxWirelessRange");
        this.maxWirelessRange = new double[]{
            wirelessRangeList.size() > 0 ? Math.max(0, wirelessRangeList.get(0)) : 0,
            wirelessRangeList.size() > 1 ? Math.max(0, wirelessRangeList.get(1)) : 0
        };
        
        // Integration settings
        this.enableCommandBlockDriver = config.getBoolean("integration.vanilla.enableCommandBlockDriver");
        this.allowItemStackNBTTags = config.getBoolean("integration.vanilla.allowItemStackNBTTags");
        this.costProgrammingTable = Math.max(0, config.getDouble("integration.buildcraft.programmingTableCost"));
        
        // Debug card access
        this.debugCardAccess = DebugCardAccess.fromConfig(config);
    }
    
    // DebugCardAccess interface and implementations
    public interface DebugCardAccess {
        static DebugCardAccess fromConfig(Config config) {
            String mode = config.getString("debug.cardAccess");
            switch (mode.toLowerCase(Locale.ROOT)) {
                case "off": return DebugCardAccess.FORBIDDEN;
                case "on": return DebugCardAccess.ALLOWED;
                case "require_auth": return DebugCardAccess.REQUIRE_AUTH;
                default: 
                    OpenComputers.log.warn("Unknown debug card access mode: " + mode + ", defaulting to 'off'");
                    return DebugCardAccess.FORBIDDEN;
            }
        }
        
        String checkAccess(Optional<DebugCard.AccessContext> context);
        
        DebugCardAccess FORBIDDEN = context -> "debug card is disabled";
        DebugCardAccess ALLOWED = context -> null;
        DebugCardAccess REQUIRE_AUTH = context -> {
            if (!context.isPresent() || !context.get().isOnline()) {
                return "debug card requires authentication";
            }
            return null;
        };
    }
    
    // Configuration loading
    private static Settings instance;
    
    public static synchronized Settings get() {
        if (instance == null) {
            try {
                // Load default config
                Config defaultConfig = ConfigFactory.load("defaults");
                
                // Load user config
                Path configPath = FMLPaths.CONFIGDIR.get().resolve("opencomputers.conf");
                Config userConfig = ConfigFactory.parseFile(configPath.toFile())
                    .withFallback(defaultConfig);
                
                // Apply any migrations if needed
                userConfig = patchConfig(userConfig, defaultConfig);
                
                // Create settings instance
                instance = new Settings(userConfig);
                
                // Save the config back to ensure all defaults are written
                ConfigRenderOptions options = ConfigRenderOptions.defaults()
                    .setOriginComments(false)
                    .setJson(false)
                    .setFormatted(true);
                String rendered = userConfig.root().render(options);
                Files.createDirectories(configPath.getParent());
                Files.write(configPath, rendered.getBytes(StandardCharsets.UTF_8));
                
            } catch (Exception e) {
                OpenComputers.log.error("Error loading configuration", e);
                // Fall back to defaults if there's an error
                instance = new Settings(ConfigFactory.empty());
            }
        }
        return instance;
    }
    
    private static Config patchConfig(Config config, Config defaults) {
        // Implement any necessary config migrations here
        return config;
    }
    
    // Helper methods
    public static double[] getNanomachineHudPos() {
        return get().nanomachineHudPos;
    }
    
    public static double getMaxWirelessRange(int tier) {
        double[] ranges = get().maxWirelessRange;
        return tier > 0 && tier <= ranges.length ? ranges[tier - 1] : 0;
    }
    
    // Add other helper methods as needed
}
