package me.derechtepilz.economy;

public class VersionHandler {
    private VersionHandler() {

    }

    private static final String[] supportedVersions = {
            "1.16.5",
            "1.17", "1.17.1",
            "1.18", "1.18.1", "1.18.2"
    };

    public static boolean isVersionSupported(String version) {
        for (String supportedVersion : supportedVersions) {
            if (supportedVersion.equals(version)) return true;
        }
        return false;
    }
}
