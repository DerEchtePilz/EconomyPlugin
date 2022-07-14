package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.utility.exceptions.InvalidRangeException;

public class RangeValidator {
    /**
     * Utility class used to check if integers are in a certain range
     * @param min Minimum integer required
     * @param max Maximum integer required
     * @param provided Integer from user input
     * @param exceptionMessage Will be used to create an exception if {@code provided} is not between {@code min} and {@code max}
     * @throws InvalidRangeException Will be thrown if {@code provided} is not between {@code min} and {@code max}
     */
    public RangeValidator(int min, int max, int provided, String exceptionMessage) {
        if (!(min <= provided && provided <= max)) {
           throw new InvalidRangeException(exceptionMessage);
        }
    }

    /**
     * Utility class used to check if doubles are in a certain range
     * @param min Minimum double required
     * @param max Maximum double required
     * @param provided Double from user input
     * @param exceptionMessage Will be used to create an exception if {@code provided} is not between {@code min} and {@code max}
     * @throws InvalidRangeException Will be thrown if {@code provided} is not between {@code min} and {@code max}
     */
    public RangeValidator(double min, double max, double provided, String exceptionMessage) {
        if (!(min <= provided && provided <= max)) {
            throw new InvalidRangeException(exceptionMessage);
        }
    }
}
