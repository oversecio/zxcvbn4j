package com.nulabinc.zxcvbn;

import com.nulabinc.zxcvbn.matchers.Match;
import com.nulabinc.zxcvbn.Matching;
import com.nulabinc.zxcvbn.matchers.Dictionary;
import com.nulabinc.zxcvbn.matchers.Keyboard;
import com.nulabinc.zxcvbn.guesses.RegexGuess;
import com.nulabinc.zxcvbn.matchers.RegexMatcher;
import com.nulabinc.zxcvbn.matchers.DateMatcher;
import com.nulabinc.zxcvbn.matchers.L33tMatcher;

import java.util.ArrayList;
import java.util.List;

public class Zxcvbn {

    public static void unloadDictionaryData() {
        synchronized (Zxcvbn.class) {
            Dictionary.unload();
            Matching.unload();
            Keyboard.unload();
            RegexGuess.unload();
            RegexMatcher.unload();
            DateMatcher.unload();
            L33tMatcher.unload();
        }
    }

    public Zxcvbn() {
    }

    public Strength measure(String password) {
        return measure(password, null);
    }

    public Strength measure(String password, List<String> sanitizedInputs) {
        if (password == null) {
            throw new IllegalArgumentException("Password is null.");
        }
        List<String> lowerSanitizedInputs = new ArrayList<>();
        if (sanitizedInputs != null) {
            for (String sanitizedInput : sanitizedInputs) {
                lowerSanitizedInputs.add(sanitizedInput.toLowerCase());
            }
        }
        long start = time();
        Matching matching = new Matching(lowerSanitizedInputs);
        List<Match> matches = matching.omnimatch(password);
        Strength strength = Scoring.mostGuessableMatchSequence(password, matches);
        strength.setCalcTime(time() - start);
        AttackTimes attackTimes = TimeEstimates.estimateAttackTimes(strength.getGuesses());
        strength.setCrackTimeSeconds(attackTimes.getCrackTimeSeconds());
        strength.setCrackTimesDisplay(attackTimes.getCrackTimesDisplay());
        strength.setScore(attackTimes.getScore());
        strength.setFeedback(Feedback.getFeedback(strength.getScore(), strength.getSequence()));
        return strength;
    }

    private long time() {
        return System.nanoTime();
    }

}
