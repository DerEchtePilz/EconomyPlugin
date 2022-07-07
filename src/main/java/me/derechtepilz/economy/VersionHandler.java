package me.derechtepilz.economy;

import me.derechtepilz.economy.nms.*;

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
        NMS nms;
        if (!isVersionSupported(version)) {
            return null;
        }
        switch (version) {
            case "1.13" -> nms = new NMS_1_13();
            case "1.13.1" -> nms = new NMS_1_13_1();
            case "1.13.2" -> nms = new NMS_1_13_2();
            case "1.14", "1.14.1" -> nms = new NMS_1_14();
            case "1.14.2" -> nms = new NMS_1_14_2();
            case "1.14.3" -> nms = new NMS_1_14_3();
            case "1.14.4" -> nms = new NMS_1_14_4();
            case "1.15" -> nms = new NMS_1_15();
            case "1.15.1" -> nms = new NMS_1_15_1();
            case "1.15.2" -> nms = new NMS_1_15_2();
            case "1.16.1" -> nms = new NMS_1_16_1();
            case "1.16.2" -> nms = new NMS_1_16_2();
            case "1.16.3" -> nms = new NMS_1_16_3();
            case "1.16.4" -> nms = new NMS_1_16_4();
            case "1.16.5" -> nms = new NMS_1_16_5();
            case "1.17" -> nms = new NMS_1_17();
            case "1.17.1" -> nms = new NMS_1_17_1();
            case "1.18" -> nms = new NMS_1_18();
            case "1.18.1" -> nms = new NMS_1_18_1();
            case "1.18.2" -> nms = new NMS_1_18_2();
            case "1.19" -> nms = new NMS_1_19();
            default -> throw new IllegalStateException("Unexpected value: " + version);
        }
        return nms;
    }
}