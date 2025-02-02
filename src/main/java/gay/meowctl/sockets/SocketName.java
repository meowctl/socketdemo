package gay.meowctl.sockets;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SocketName {
    public enum Option {
        RANDOM, SHORT, LONG
    }

    private static final SecureRandom RNG = new SecureRandom();

    private final Option option;
    private final long pid;
    private int shortIdCounter = 0;
    private long longIdCounter = 0;

    public SocketName() {
        this(Option.RANDOM);
    }

    public SocketName(Option option) {
        this.option = option;
        this.pid = getpid();
    }

    public String nextName() {
        return switch (option) {
            case RANDOM -> RNG.nextBoolean() ? nextShortName() : nextLongName();
            case SHORT -> nextShortName();
            case LONG -> nextLongName();
        };
    }

    public synchronized String nextShortName() {
        // pid (1~32bit) + counter (24bit) + random (8bit)
        long streamId = pid << 32 | (long) shortIdCounter << 8 | RNG.nextLong(1<<8);

        if (++shortIdCounter >= 1<<20) {
            shortIdCounter = 0;
        }

        return "gb-" + Long.toUnsignedString(streamId, 36);
    }

    public synchronized String nextLongName() {
        // pid (1~32bit) + counter (63bit) + random (32bit)
        BigInteger streamId = BigInteger
                .valueOf(pid)
                .shiftLeft(63)
                .or(BigInteger.valueOf(longIdCounter))
                .shiftLeft(32)
                .or(BigInteger.valueOf(RNG.nextLong(1L<<32)));

        if (longIdCounter++ == Long.MAX_VALUE) {
            longIdCounter = 0;
        }

        return "gb-" + streamId.toString(36);
    }

    private long getpid() {
        long pid = ProcessHandle.current().pid() & 0xffffffffL;

        return pid != 0 ? pid : RNG.nextLong(1, 1L<<32);
    }
}
