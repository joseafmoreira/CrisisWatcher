package dev.crisiswatcher.server.room;

import org.apache.commons.codec.digest.DigestUtils;

import dev.crisiswatcher.server.model.UserModel.UserProfile;

public abstract class RoomSettingsGenerator {
    private static final int MAX_FIRST_DIGIT = 239;
    private static final int MAX_OTHER_DIGITS = 255;
    private static final int START_PORT = 1;
    private static final int END_PORT = 65535;
    private static int firstDigit = 224;
    private static int secondDigit = 0;
    private static int thirdDigit = 0;
    private static int forthDigit = 0;
    private static int port = START_PORT;

    public static String generateAddress() {
        String result = null;
        if (firstDigit <= MAX_FIRST_DIGIT) {
            result = firstDigit + "." + secondDigit + "." + thirdDigit + "." + forthDigit + ":" + port++;
            if (port > END_PORT) {
                port = START_PORT;
                forthDigit++;
                if (forthDigit > MAX_OTHER_DIGITS) {
                    forthDigit = 0;
                    thirdDigit++;
                }
                if (thirdDigit > MAX_OTHER_DIGITS) {
                    thirdDigit = 0;
                    secondDigit++;
                }
                if (secondDigit > MAX_OTHER_DIGITS) {
                    secondDigit = 0;
                    firstDigit++;
                }
            }
        }
        return result;
    }

    public static String generateCode(String name, String address, int port) {
        String code = null;
        UserProfile[] profiles = UserProfile.values();
        for (UserProfile profile : profiles) {
            if (profile.getValue().equals(name)) {
                code = name;
                break;
            }
        }
        if (code == null) code = DigestUtils.sha256Hex(name + "/" + address + ":" + port).replaceAll("[^A-Za-z0-9]", "").substring(0, 6).toUpperCase();
        return code;
    }
}
