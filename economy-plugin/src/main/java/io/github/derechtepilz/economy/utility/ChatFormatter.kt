package io.github.derechtepilz.economy.utility

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ChatFormatter {

    fun valueOf(double: Double?): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        numberFormat.maximumFractionDigits = 2
        val decimalFormat: DecimalFormat = numberFormat as DecimalFormat
        return decimalFormat.format(double)
    }

    fun valueOf(float: Float?): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        numberFormat.maximumFractionDigits = 2
        val decimalFormat: DecimalFormat = numberFormat as DecimalFormat
        return decimalFormat.format(float)
    }

}