package org.ai4math.testutils;

import org.ai4math.cspm.Keywords;

import java.util.Objects;

public class Utils {
    public static String typeOf(String message){
        String[] comps = message.split("[!?$.]",0);
        if (comps.length>1) {
            String value = comps[1];
            if (Objects.equals(value, "true") || Objects.equals(value, "false")) {
                return Keywords.BOOL;
            } else if (value.length() == 3 &&
                    Character.toString(value.charAt(0)).equals("'") &&
                    Character.isAlphabetic(value.charAt(1))) {
                return Keywords.CHAR;
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                return Keywords.INT;
            }
        }
        return null;
    }
}
