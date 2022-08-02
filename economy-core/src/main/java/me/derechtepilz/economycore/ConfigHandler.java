package me.derechtepilz.economycore;

import java.lang.reflect.Field;

class ConfigHandler {

    private static double startBalance = 50.0;
    private static double interest = 1.0;
    private static int minimumDaysForInterest = 1;

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

    public static String getStringValue() {
        return "startBalance=" + startBalance + ",interest=" + interest + ",minimumDaysForInterest=" + minimumDaysForInterest;
    }

    public static void loadValues(String config) {
        String[] values = config.split(",");
        for (String value : values) {
            try {
                String[] valueSplit = value.split("=");
                Field variable = Class.forName(ConfigHandler.class.getCanonicalName()).getDeclaredField(valueSplit[0]);
                variable.setAccessible(true);
                variable.setDouble(ConfigHandler.class, Double.parseDouble(valueSplit[1]));
                variable.setAccessible(false);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }
}