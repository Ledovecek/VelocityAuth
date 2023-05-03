package me.ledovec.velocityauth.security;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public final class Security {

    public static String hashPassword(String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public static boolean passwordsMatch(String password, String secret) {
        String pwd = hashPassword(password);
        return secret.equals(pwd);
    }

}
