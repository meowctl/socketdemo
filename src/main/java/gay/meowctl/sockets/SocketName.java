package gay.meowctl.sockets;

import java.security.SecureRandom;

public class SocketName {
    private static final String PREFIX = "gb-";
    private static final String CHARS = "0123456789-_qwertyuiopasdfghjklzxcvbnm";
    private static final SecureRandom RNG = new SecureRandom();

    public static String nextName(int randomChars) {
        var sb = new StringBuilder(PREFIX.length() + randomChars);

        sb.append(PREFIX);
        for (int i = 0; i < randomChars; i++) {
            int chIndex = RNG.nextInt(CHARS.length());
            char ch = CHARS.charAt(chIndex);

            // if chosen character is a letter, decide if it should be uppercase or not
            if (chIndex >= 12 && RNG.nextBoolean()) {
                ch = Character.toUpperCase(ch);
            }

            sb.append(ch);
        }
        return sb.toString();
    }

    public static String nextUniqueName() {
        return nextName(11);
    }
}
