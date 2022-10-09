package me.derechtepilz.economycore;

import java.lang.reflect.Field;

class ConfigHandler {

    private static double startBalance = 50.0;
    private static double interest = 1.0;
    private static int minimumDaysForInterest = 1;

    private static final double defaultStartBalance = 50.0;
    private static final double defaultInterest = 1.0;
    private static final int defaultMinimumDaysForInterest = 1;

    private ConfigHandler() {

    }

    public static double getStartBalance() {
        return startBalance;
    }

    public static void setStartBalance(double startBalance) {
        ConfigHandler.startBalance = startBalance;
    }

    public static double getInterest() {
        return interest;
    }

    public static void setInterest(double interest) {
        ConfigHandler.interest = interest;
    }

    public static int getMinimumDaysForInterest() {
        return minimumDaysForInterest;
    }

    public static void setMinimumDaysForInterest(int minimumDaysForInterest) {
        ConfigHandler.minimumDaysForInterest = minimumDaysForInterest;
    }

    public static void resetConfigValues() {
        startBalance = defaultStartBalance;
        interest = defaultInterest;
        minimumDaysForInterest = defaultMinimumDaysForInterest;
    }

    public static String getStringValue() {
        return "startBalance=" + startBalance + ",interest=" + interest + ",minimumDaysForInterest=" + minimumDaysForInterest;
    }

    public static void loadValues(String config) {
        String[] values = config.split(",");
        for (String value : values) {
            try {
                String[] valueSplit = value.split("=");
                String varName = valueSplit[0];
                Field variable = Class.forName(ConfigHandler.class.getCanonicalName()).getDeclaredField(varName);
                variable.setAccessible(true);

                if (variable.getType().equals(double.class)) {
                    variable.setDouble(ConfigHandler.class, Double.parseDouble(valueSplit[1]));
                }
                if (variable.getType().equals(int.class)) {
                    variable.setInt(ConfigHandler.class, Integer.parseInt(valueSplit[1]));
                }

                variable.setAccessible(false);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }
}
