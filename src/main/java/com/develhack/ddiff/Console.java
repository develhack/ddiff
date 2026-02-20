package com.develhack.ddiff;

public class Console {

    static int verbosity = 0;

    public static void log(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    public static void v(String format, Object... args) {
        if (verbosity >= 1) {
            log(format, args);
        }
    }

    public static void vv(String format, Object... args) {
        if (verbosity >= 2) {
            log(format, args);
        }
    }

    public static void vvv(String format, Object... args) {
        if (verbosity >= 3) {
            log(format, args);
        }
    }
}
