package me.derechtepilz.economy.utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ChatFormatter {
    private ChatFormatter() {

    }

    public static String valueOf(Integer i) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(i);
    }

    public static String valueOf(Long l) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(l);
    }

    public static String valueOf(Double d) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(d);
    }

    public static String valueOf(Float f) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        return decimalFormat.format(f);
    }
}
