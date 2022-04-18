package me.derechtepilz.economy.commands;

import me.derechtepilz.economy.commands.nms.NMS;
import me.derechtepilz.economy.commands.nms.NMS_1_18_R1;

public interface EconomyPluginCommandAPIVersionHandler {
    static NMS getNMS(String version) {
        byte id = -1;
        switch (version) {
            case "1.13": {

            }
            case "1.13.1": {

            }
            case "1.13.2": {

            }
            case "1.14": {

            }
            case "1.14.1": {

            }
            case "1.14.2": {

            }
            case "1.14.3": {

            }
            case "1.14.4": {

            }
            case "1.15": {

            }
            case "1.15.1": {

            }
            case "1.15.2": {

            }
            case "1.16": {

            }
            case "1.16.1": {

            }
            case "1.16.2": {

            }
            case "1.16.3": {

            }
            case "1.16.4": {

            }
        }
        switch (id) {
            case 0: {
                return new NMS_1_18_R1();
            }
            default: {
                throw new UnsupportedOperationException("Your version of Minecraft is unsupported: " + version);
            }
        }
    }
}
