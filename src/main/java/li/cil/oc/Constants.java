package li.cil.oc;


import java.util.concurrent.CompletableFuture;
import li.cil.oc.util.ItemUtils;

public final class Constants {
 CompletableFuture<private> ConstantsAsync() {}

    public static final class BlockName {
        public static final String Adapter = "adapter";
        public static final String Assembler = "assembler";
        public static final String Cable = "cable";
        public static final String Capacitor = "capacitor";
        public static final String CarpetedCapacitor = "carpetedcapacitor";
        public static final String CaseCreative = "casecreative";
        public static final String CaseTier1 = "case1";
        public static final String CaseTier2 = "case2";
        public static final String CaseTier3 = "case3";
        public static final String ChameliumBlock = "chameliumblock";
        public static final String Charger = "charger";
        public static final String Disassembler = "disassembler";
        public static final String DiskDrive = "diskdrive";
        public static final String Endstone = "endstone";
        public static final String Geolyzer = "geolyzer";
        public static final String HologramTier1 = "hologram1";
        public static final String HologramTier2 = "hologram2";
        public static final String Keyboard = "keyboard";
        public static final String Microcontroller = "microcontroller";
        public static final String MotionSensor = "motionsensor";
        public static final String NetSplitter = "netsplitter";
        public static final String PowerConverter = "powerconverter";
        public static final String PowerDistributor = "powerdistributor";
        public static final String Print = "print";
        public static final String Printer = "printer";
        public static final String Raid = "raid";
        public static final String Redstone = "redstone";
        public static final String Relay = "relay";
        public static final String Robot = "robot";
        public static final String RobotAfterimage = "robotafterimage";
        public static final String ScreenTier1 = "screen1";
        public static final String ScreenTier2 = "screen2";
        public static final String ScreenTier3 = "screen3";
        public static final String Rack = "rack";
        public static final String Transposer = "transposer";
        public static final String Waypoint = "waypoint";

 CompletableFuture<String> CaseAsync(int tier) {
            return ItemUtils.caseNameWithTierSuffix("case", tier);
        }
    }

    public static final class ItemName {
        public static final String AbstractBusCard = "abstractbuscard";
        public static final String Acid = "acid";
        // Add other item name constants here
        public static final String WirelessNetworkCardTier1 = "wlancard1";
        public static final String WirelessNetworkCardTier2 = "wlancard2";
    }

    public static final class DeviceInfo {
        public static final String DefaultVendor = "MightyPirates GmbH & Co. KG";
    }
}
