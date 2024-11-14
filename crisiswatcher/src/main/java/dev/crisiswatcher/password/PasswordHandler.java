package dev.crisiswatcher.password;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class PasswordHandler {
    public static String hashPassword(String password) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }
}
