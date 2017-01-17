package com.nulabinc.zxcvbn.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;
import com.nulabinc.zxcvbn.Zxcvbn;

public class RegexMatcher extends BaseMatcher {

    private static Map<String, String> REGEXEN;

    private static Map<String, String> getRegexgen() {
        synchronized (Zxcvbn.class) {
            if (REGEXEN==null) {
                REGEXEN = new HashMap<>();

                REGEXEN.put("recent_year", "19\\d\\d|200\\d|201\\d");
            }
            return REGEXEN;
        }
    }

    public static void unload() {
        synchronized (Zxcvbn.class) {
            REGEXEN = null;
        }
    }

    @Override
    public List<Match> execute(String password) {
        List<Match> matches = new ArrayList<>();
        for(Map.Entry<String, String> regexenRef: getRegexgen().entrySet()) {
            String name = regexenRef.getKey();
            java.util.regex.Matcher rxMatch = Pattern.compile(regexenRef.getValue()).matcher(password);
            while(rxMatch.find()){
                String token = rxMatch.group();
                matches.add(MatchFactory.createRegexMatch(
                        rxMatch.start(),
                        rxMatch.start() + token.length() - 1,
                        token,
                        name,
                        rxMatch));
            }
        }
        return this.sorted(matches);
    }
}
