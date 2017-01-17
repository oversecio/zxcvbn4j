package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.matchers.Match;

import java.util.HashMap;
import java.util.Map;
import com.nulabinc.zxcvbn.Zxcvbn;

public class RegexGuess extends BaseGuess {

    private static Map<String, Integer> CHAR_CLASS_BASES;

    private static Map<String, Integer> getCharBaseClasses() {
        synchronized (Zxcvbn.class) {
            if (CHAR_CLASS_BASES==null) {
                CHAR_CLASS_BASES = new HashMap<>();

                CHAR_CLASS_BASES.put("alpha_lower", 26);
                CHAR_CLASS_BASES.put("alpha_upper", 26);
                CHAR_CLASS_BASES.put("alpha", 52);
                CHAR_CLASS_BASES.put("alphanumeric", 62);
                CHAR_CLASS_BASES.put("digits", 10);
                CHAR_CLASS_BASES.put("symbols", 33);

            }
            return CHAR_CLASS_BASES;
        }
    }

    public static void unload() {
        synchronized (Zxcvbn.class) {
            CHAR_CLASS_BASES = null;
        }
    }

    @Override
    public double exec(Match match) {
        if (CHAR_CLASS_BASES.containsKey(match.regexName)) {
            return Math.pow(getCharBaseClasses().get(match.regexName), match.token.length());
        } else if ("recent_year".equals(match.regexName)) {
            double yearSpace = Math.abs(parseInt(match.token) - REFERENCE_YEAR);
            yearSpace = Math.max(yearSpace, MIN_YEAR_SPACE);
            return yearSpace;
        }
        return 0;
    }

    private static final int parseInt(String s) {
        int result = 0;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println(e.getStackTrace());
        }
        return result;
    }
}
