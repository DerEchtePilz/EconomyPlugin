package me.derechtepilz.economycore;

class EconomyAPIVersionHandler {
    private EconomyAPIVersionHandler() {}

    private static final String[] supportedVersions = {
            "1.16.x", "1.17.x", "1.18.x", "1.19.x"
    };

    public static boolean isVersionSupported(String version) {
        String[] versionNumber = version.split("\\.");
        versionNumber[2] = "x";
        String finalVersionNumber = versionNumber[0] + "." + versionNumber[1] + "." + versionNumber[2];
        for (String supportedVersion : supportedVersions) {
            if (supportedVersion.equals(finalVersionNumber)) {
                return true;
            }
        }
        return false;
    }

    public static void checkDependencies() {
        try {
            Class.forName("net.kyori.adventure.text.Component");
        } catch (ClassNotFoundException exception) {
            EconomyAPI.getLogger().severe("Couldn't find class 'net.kyori.adventure.text.Component'. The API and your plugin may break. Please upgrade your server to a paper server to prevent this.");
        }
    }
}
