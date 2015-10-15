package ru.linachan.util;

public class MathHelper {

    public static double round(double value, int precision) {
        if (precision < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, precision);

        return (double) Math.round(value * factor) / factor;
    }

    public static double adaptiveRound(double value) {
        int integerPart = (int) Math.round(value);

        if (integerPart != 0)
            return Math.round(value);

        double fraction = value - integerPart;

        int precision = 0;

        while (((int) fraction) == 0) {
            fraction *= 10;
            precision++;
        }

        return round(value, precision);
    }
}
