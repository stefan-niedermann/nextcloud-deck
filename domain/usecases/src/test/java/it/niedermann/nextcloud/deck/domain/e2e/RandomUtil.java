package it.niedermann.nextcloud.deck.domain.e2e;

import java.security.SecureRandom;

import jakarta.inject.Inject;

public class RandomUtil {

    @Inject
    public RandomUtil() {

    }

    private final SecureRandom RANDOM = new SecureRandom();

    public final String randomString(int length) {
        final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        return RANDOM.ints(length, 0, CHARS.length())
                .mapToObj(CHARS::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public final String randomize(String value) {
        return randomize(value, 5);
    }

    public final String randomize(String value, int length) {
        return "E2E_" + value + "_" + randomString(length);
    }
}
