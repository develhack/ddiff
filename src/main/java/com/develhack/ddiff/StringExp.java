package com.develhack.ddiff;

public class StringExp {
    public static void main(String[] args) {
        int max = 6;
        String str = "1234567890";
        if (str.length() > max) {
            str = str.substring(str.length() - max, str.length());
        }
        System.out.println(str);
    }
}
