package me.derechtepilz.economy;

import me.derechtepilz.economy.nms.NMS;
import me.derechtepilz.economyplugin.nms.*;

public class VersionHandler {
    private VersionHandler() {
    }

    private static final String[] supportedVersions = {
            "1.13", "1.13.1", "1.13.2",
            "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4",
            "1.15", "1.15.1", "1.15.2",
            "1.16", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5",
            "1.17", "1.17.1",
            "1.18", "1.18.1", "1.18.2",
            "1.19"
    };

    public static boolean isVersionSupported(String version) {
        for (String supportedVersion : supportedVersions) {
            if (supportedVersion.equals(version)) return true;
        }
        return false;
    }

    public static NMS getSupportedNMS(String version) {
        if (!isVersionSupported(version)) {
            return null;
        }
        return switch (version) {
            case "1.13" -> new NMS_1_13();
            case "1.13.1" -> new NMS_1_13_1();
            case "1.13.2" -> new NMS_1_13_2();
            case "1.14", "1.14.1", "1.14.2" -> new NMS_1_14();
            case "1.14.3" -> new NMS_1_14_3();
            case "1.14.4" -> new NMS_1_14_4();
            case "1.15", "1.15.1", "1.15.2" -> new NMS_1_15();
            case "1.16.1" -> new NMS_1_16_R1();
            case "1.16.2", "1.16.3" -> new NMS_1_16_R2();
            case "1.16.4" -> new NMS_1_16_R3();
            case "1.16.5" -> new NMS_1_16_R4();
            case "1.17", "1.17.1" -> new NMS_1_17_R1();
            case "1.18", "1.18.1" -> new NMS_1_18_R1();
            case "1.18.2" -> new NMS_1_18_R2();
            case "1.19" -> new NMS_1_19_R1();
            default -> throw new IllegalStateException("Unexpected value: " + version);
        };
    }
}