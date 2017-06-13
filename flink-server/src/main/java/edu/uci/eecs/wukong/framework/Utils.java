package edu.uci.eecs.wukong.framework;

import java.math.BigInteger;
import java.util.Random;

public class Utils {
    private static final Random RANDOM = new Random();

    public static String randomExplicitHashKey() {
        return new BigInteger(128, RANDOM).toString(10);
    }
}
